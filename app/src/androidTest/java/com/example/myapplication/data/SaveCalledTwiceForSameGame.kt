package com.example.myapplication.data

import com.example.myapplication.data.repository.SavedGamesRepositoryImpl
import com.example.myapplication.ui.game.GameMode
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

/*
class SaveCalledTwiceForSameGame{
    @Test
    fun save_calledTwiceForSameGame_updatesRatherThanDuplicates() = runTest {
        val repo = SavedGamesRepositoryImpl(dao)

        val id1 = repo.save(null, historyAfterOneMove, GameMode.LocalTwoPlayer)
        val id2 = repo.save(id1, historyAfterTwoMoves, GameMode.LocalTwoPlayer)

        assertEquals(id1, id2)
        assertEquals(1, dao.count()) // still exactly one row, not two
    }
}
*/