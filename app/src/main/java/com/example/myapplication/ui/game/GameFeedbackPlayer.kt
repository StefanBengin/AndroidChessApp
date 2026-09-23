package com.example.myapplication.ui.game

import android.content.Context
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.myapplication.data.local.AppSettings
import com.example.myapplication.domain.model.Move


class GameFeedbackPlayer(private val context: Context) {
    /*
    private val soundPool = SoundPool.Builder().setMaxStreams(4).build()
    private val moveSoundId = soundPool.load(context, R.raw.move, 1)
    private val captureSoundId = soundPool.load(context, R.raw.capture, 1)
    private val checkSoundId = soundPool.load(context, R.raw.check, 1)

    fun onMovePlayed(move: Move, isCheck: Boolean, settings: AppSettings) {
        if (settings.soundEnabled) {
            val soundId = when {
                isCheck -> checkSoundId
                move.capturedPiece != null || move.isEnPassant -> captureSoundId
                else -> moveSoundId
            }
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
        if (settings.hapticsEnabled) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            //vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun release() = soundPool.release()
    */
    fun onMovePlayed(move: Move, isCheck: Boolean, settings: AppSettings){

    }
    fun release(){}
}