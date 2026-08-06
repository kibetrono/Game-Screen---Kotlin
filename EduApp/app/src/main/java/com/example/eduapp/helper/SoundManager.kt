package com.example.eduapp.helper

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * App functionality: sound. Plays short tone feedback for correct/incorrect answers
 * and level completion. Uses Android's built-in ToneGenerator so no audio asset
 * files are required, and respects the user's sound-on/off preference.
 *
 * Note: ToneGenerator plays through the STREAM_MUSIC (media) volume, not the
 * ringtone/notification volume - if you don't hear anything, check the device's
 * media volume first (many emulators/devices default this to low or muted).
 *
 * Not a Composable - hold one instance for the lifetime of the screen that needs it
 * and call release() when done (see GameScreen's DisposableEffect).
 */
class SoundManager {

    private var toneGenerator: ToneGenerator? = null

    private fun generator(): ToneGenerator? {
        toneGenerator?.let { return it }
        return try {
            ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME).also {
                toneGenerator = it
            }
        } catch (e: RuntimeException) {
            // ToneGenerator can fail to construct on some devices/emulators if the
            // audio system is unavailable; fail silently rather than crash the game.
            Log.w(TAG, "Could not create ToneGenerator", e)
            null
        }
    }

    fun playCorrect() = play(ToneGenerator.TONE_PROP_ACK, 150)
    fun playIncorrect() = play(ToneGenerator.TONE_PROP_NACK, 200)
    fun playLevelComplete() = play(ToneGenerator.TONE_PROP_BEEP2, 400)

    private fun play(tone: Int, durationMs: Int) {
        try {
            val played = generator()?.startTone(tone, durationMs)
            if (played != true) {
                Log.w(TAG, "startTone($tone) returned false - tone was not queued")
            }
        } catch (e: RuntimeException) {
            Log.w(TAG, "startTone($tone) failed", e)
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }

    companion object {
        private const val TAG = "SoundManager"
    }
}
