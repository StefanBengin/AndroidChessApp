package com.example.myapplication.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.GameplaySettings
import com.example.myapplication.data.repository.SavedGamesRepository
import com.example.myapplication.domain.ai.ChessEngine
import com.example.myapplication.domain.ai.GameMode
import com.example.myapplication.domain.ai.GameSource
import com.example.myapplication.domain.engine.GameResultDetector
import com.example.myapplication.domain.engine.MoveGenerator
import com.example.myapplication.domain.model.*
import com.example.myapplication.domain.usecase.PgnWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.mapNotNull

private data class HistoryEntry(
    val state: GameState,
    val move: Move?,
    val pieceIds: Map<Square, Long>   // exact, precomputed — never guessed
)

class GameViewModel(
    private val chessEngine: ChessEngine,
    private val savedGamesRepository: SavedGamesRepository,
    private val source: GameSource, // null => brand-new game
    gameplaySettingsFlow: Flow<GameplaySettings>
) : ViewModel() {

    private val gameplaySettings: StateFlow<GameplaySettings> =
        gameplaySettingsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, GameplaySettings())
    private val gameMode: GameMode = when (source) {   // val — resolved once, at construction, never reassigned
        is GameSource.NewGame -> source.gameMode
        is GameSource.Resume -> source.savedGame.gameMode
    }

    private var currentSavedGameId: Long? = (source as? GameSource.Resume)?.savedGame?.id

    private val history: MutableList<HistoryEntry> = buildHistoryWithIds(
        when (source) {
            is GameSource.NewGame -> listOf(GameState.newGame() to null)
            is GameSource.Resume -> source.savedGame.history
        }
    )
    private var historyIndex = history.lastIndex

    private val _uiState = MutableStateFlow(buildUiState(isAiThinking = false))  // no isLoading — nothing left to wait for
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        maybeTriggerAiMove()
    }

    private val currentGameState: GameState get() = history[historyIndex].state
    //private var currentSavedGameId : Long? = savedGameId
    private var nextPieceId = 0L

    private fun initialPieceIds(state: GameState): Map<Square, Long> {
        val ids = mutableMapOf<Square, Long>()
        for (sq in 0 until 128) {
            if (!Squares.isOnBoard(sq)) continue
            if (state.board[sq] != null) ids[sq] = nextPieceId++
        }
        return ids
    }

    private fun computePieceIds(previousIds: Map<Square, Long>, move: Move): Map<Square, Long> {
        val ids = previousIds.toMutableMap()
        val movingId = ids.remove(move.from) ?: nextPieceId++ // shouldn't happen, but never crash over it

        if (move.isEnPassant) {
            val capturedSq = Squares.of(Squares.file(move.to), Squares.rank(move.from))
            ids.remove(capturedSq)
        } else if (move.capturedPiece != null) {
            ids.remove(move.to)
        }

        if (move.castleSide != null) {
            val rank = Squares.rank(move.from)
            val (rookFrom, rookTo) = when (move.castleSide) {
                CastleSide.KINGSIDE -> Squares.of(7, rank) to Squares.of(5, rank)
                CastleSide.QUEENSIDE -> Squares.of(0, rank) to Squares.of(3, rank)
            }
            ids.remove(rookFrom)?.let { ids[rookTo] = it }
        }

        ids[move.to] = if (move.promotion != null) nextPieceId++ else movingId // promotion always gets a fresh id
        return ids
    }

    private fun buildHistoryWithIds(raw: List<Pair<GameState, Move?>>): MutableList<HistoryEntry> {
        val result = mutableListOf<HistoryEntry>()
        var ids = initialPieceIds(raw.first().first)
        result.add(HistoryEntry(raw.first().first, null, ids))
        for (i in 1 until raw.size) {
            val (state, move) = raw[i]
            ids = computePieceIds(ids, move!!)
            result.add(HistoryEntry(state, move, ids))
        }
        return result
    }

    fun onSquareTapped(square: Square) {
        val current = _uiState.value
        if (current.pendingPromotion != null || current.gameResult != null) return // ignore taps mid-promotion or post-game
        if (!isHumanTurn()) return

        val selected = current.selectedSquare
        if (selected == null) {
            // first tap: select a square if it holds a piece belonging to the side to move
            val piece = currentGameState.board[square]
            if (piece != null && piece.color == currentGameState.sideToMove) {
                selectSquare(square)
            }
            return
        }

        if (square == selected) {
            deselect()
            return
        }

        val legalMoves = MoveGenerator.legalMoves(currentGameState)
        val matchingMoves = legalMoves.filter { it.from == selected && it.to == square }

        when {
            matchingMoves.isEmpty() -> {
                // tapped an invalid square — if it's another of our own pieces, re-select instead of just deselecting
                val piece = currentGameState.board[square]
                if (piece != null && piece.color == currentGameState.sideToMove) {
                    selectSquare(square)
                } else {
                    deselect()
                }
            }
            matchingMoves.size == 1 -> applyMove(matchingMoves.first())
            else -> {
                if (gameplaySettings.value.autoQueen) {
                    applyMove(matchingMoves.first { it.promotion == PieceType.QUEEN })
                } else {
                    _uiState.update { it.copy(pendingPromotion = PendingPromotion(selected, square)) }
                }
            }
        }
    }

    fun onPromotionChosen(promotion: PieceType) {
        val pending = _uiState.value.pendingPromotion ?: return
        val move = MoveGenerator.legalMoves(currentGameState)
            .first { it.from == pending.from && it.to == pending.to && it.promotion == promotion }
        applyMove(move)
    }

    fun onPromotionCancelled() {
        _uiState.update { it.copy(pendingPromotion = null, selectedSquare = null, legalDestinations = emptySet()) }
    }

    private fun isHumanTurn(): Boolean = when (gameMode) {
        is GameMode.LocalTwoPlayer -> true
        is GameMode.VsAi -> currentGameState.sideToMove != gameMode.aiColor
    }

    fun undo() {
        val current = _uiState.value
        if (current.pendingPromotion != null || current.gameResult != null || current.isAiThinking) return
        if (historyIndex == 0) return

        val stepsBack = if (gameMode is GameMode.VsAi && historyIndex >= 2) 2 else 1
        historyIndex -= stepsBack
        _uiState.value = buildUiState()
        maybeTriggerAiMove()
    }

    fun redo() {
        val current = _uiState.value
        if (current.pendingPromotion != null || current.gameResult != null || current.isAiThinking) return
        if (historyIndex == history.lastIndex) return

        val stepsForward = if (gameMode is GameMode.VsAi && historyIndex + 2 <= history.lastIndex) 2 else 1
        historyIndex += stepsForward
        _uiState.value = buildUiState()
        maybeTriggerAiMove()
    }

    private fun selectSquare(square: Square) {
        val legalMoves = MoveGenerator.legalMoves(currentGameState)
        val destinations = legalMoves.filter { it.from == square }.map { it.to }.toSet()
        _uiState.update { it.copy(selectedSquare = square, legalDestinations = destinations) }
    }

    private fun deselect() {
        _uiState.update { it.copy(selectedSquare = null, legalDestinations = emptySet()) }
    }

    private fun applyMove(move: Move) {
        val next = currentGameState.applyMove(move)
        while (history.size > historyIndex + 1) history.removeAt(history.lastIndex)
        val newIds = computePieceIds(history[historyIndex].pieceIds, move)
        history.add(HistoryEntry(next, move, newIds))
        historyIndex = history.lastIndex

        _uiState.value = buildUiState(pendingPromotion = null)
        maybeTriggerAiMove()

        viewModelScope.launch {
            currentSavedGameId = savedGamesRepository.save(
                currentSavedGameId,
                history.map { it.state to it.move },
                gameMode,
                GameResultDetector.detect(currentGameState)
            )
        }
    }

    private fun maybeTriggerAiMove() {
        if (gameMode !is GameMode.VsAi) return
        if (currentGameState.sideToMove != gameMode.aiColor) return
        if (_uiState.value.gameResult != null) return

        val stateSnapshotIndex = historyIndex
        _uiState.update { it.copy(isAiThinking = true) }

        viewModelScope.launch {
            val move = chessEngine.findBestMove(currentGameState, gameMode.difficulty)
            if (historyIndex != stateSnapshotIndex) return@launch
            applyMove(move)
            _uiState.update { it.copy(isAiThinking = false) }
        }
    }

    private fun buildUiState(
        pendingPromotion: PendingPromotion? = null,
        isAiThinking : Boolean = _uiState.value.isAiThinking
    ): GameUiState {
        val entry = history[historyIndex]
        val state = currentGameState
        val positionedPieces = entry.pieceIds.map { (square, id) ->
            PositionedPiece(id, state.board[square]!!, square)
        }
        val inCheck = state.isInCheck()
        val result = GameResultDetector.detect(state)
        val playedMoves = history.subList(1, historyIndex + 1).map { it.move!! }

        return GameUiState(
            positionedPieces = positionedPieces,
            sideToMove = state.sideToMove,
            lastMove = history[historyIndex].move,
            checkedKingSquare = if (inCheck) state.board.kingSquare(state.sideToMove) else null,
            pendingPromotion = pendingPromotion,
            gameResult = result,
            canUndo = historyIndex > 0,
            canRedo = historyIndex < history.lastIndex,
            isAiThinking = isAiThinking,
            moveHistorySan = PgnWriter.writeSanList(playedMoves),
            capturedPieces = playedMoves.mapNotNull { it.capturedPiece },
            gameMode = gameMode
        )
    }

    suspend fun currentPgn(): String {
        val moves = history.subList(1, historyIndex + 1).map { it.move!! }
        return withContext(Dispatchers.Default) { PgnWriter.write(moves) }
    }

    override fun onCleared() {

    }
}