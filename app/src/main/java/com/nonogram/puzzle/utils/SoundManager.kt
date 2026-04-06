package com.nonogram.puzzle.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager private constructor(
    private val context: Context
) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var idCellFill = 0
    private var idCellMark = 0
    private var idWrongCell = 0
    private var idHeartBreak = 0
    private var idGameOver = 0
    private var idRowSatisfied = 0
    private var idPuzzleWin = 0
    private var idHint = 0
    private var idLevelStart = 0
    private var idButtonTap = 0
    private var idCategorySelect = 0
    private var idLevelSelect = 0
    private var idDialogOpen = 0
    private var idBackNav = 0
    private var idMarkToggle = 0
    private var idAllStars = 0
    private var idDailyComplete = 0
    private var idStreakMilestone = 0
    private var idTimerWarning = 0

    @Volatile
    private var isSoundEnabled = true

    @Volatile
    private var isLoaded = false

    private val tempFiles = ConcurrentHashMap<Int, File>()
    private val loadedCount = AtomicInteger(0)

    companion object {
        private const val TOTAL_SOUNDS = 19

        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            val app = context.applicationContext
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(app).also { INSTANCE = it }
            }
        }

        private fun clearInstance() {
            INSTANCE = null
        }
    }

    private val loadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            tempFiles.remove(sampleId)?.delete()
            if (status == 0) {
                val n = loadedCount.incrementAndGet()
                if (n >= TOTAL_SOUNDS) {
                    isLoaded = true
                }
            }
        }
        loadScope.launch {
            loadAllSounds()
        }
    }

    private fun loadAllSounds() {
        idButtonTap = load(genTap())
        idCategorySelect = load(genCategorySelect())
        idLevelSelect = load(genLevelSelect())
        idCellFill = load(genCellFill())
        idCellMark = load(genCellMark())
        idWrongCell = load(genWrongCell())
        idHeartBreak = load(genHeartBreak())
        idGameOver = load(genGameOver())
        idRowSatisfied = load(genRowSatisfied())
        idPuzzleWin = load(genPuzzleWin())
        idHint = load(genHint())
        idLevelStart = load(genLevelStart())
        idDialogOpen = load(genDialogOpen())
        idBackNav = load(genBackNav())
        idMarkToggle = load(genMarkToggle())
        idAllStars = load(genAllStars())
        idDailyComplete = load(genDailyComplete())
        idStreakMilestone = load(genStreakMilestone())
        idTimerWarning = load(genTimerWarning())
    }

    private fun load(pcm: ByteArray): Int {
        val file = File.createTempFile("snd", ".wav", context.cacheDir)
        file.writeBytes(pcm)
        val sid = soundPool.load(file.absolutePath, 1)
        tempFiles[sid] = file
        return sid
    }

    private fun buildWav(
        sampleRate: Int = 44100,
        durationMs: Int,
        generator: (sampleIndex: Int, totalSamples: Int) -> Double
    ): ByteArray {
        val totalSamples = sampleRate * durationMs / 1000
        val samples = ShortArray(totalSamples) { i ->
            val raw = generator(i, totalSamples).coerceIn(-1.0, 1.0)
            (raw * 32767).toInt().toShort()
        }
        return wavHeader(totalSamples, sampleRate) + shortArrayToBytes(samples)
    }

    private fun wavHeader(samples: Int, rate: Int): ByteArray {
        val dataSize = samples * 2
        val totalSize = 36 + dataSize
        return ByteArray(44).also { h ->
            fun putInt(pos: Int, v: Int) {
                h[pos] = (v and 0xff).toByte()
                h[pos + 1] = (v shr 8 and 0xff).toByte()
                h[pos + 2] = (v shr 16 and 0xff).toByte()
                h[pos + 3] = (v shr 24 and 0xff).toByte()
            }
            fun putShort(pos: Int, v: Int) {
                h[pos] = (v and 0xff).toByte()
                h[pos + 1] = (v shr 8 and 0xff).toByte()
            }
            "RIFF".toByteArray().copyInto(h, 0)
            putInt(4, totalSize)
            "WAVE".toByteArray().copyInto(h, 8)
            "fmt ".toByteArray().copyInto(h, 12)
            putInt(16, 16)
            putShort(20, 1)
            putShort(22, 1)
            putInt(24, rate)
            putInt(28, rate * 2)
            putShort(32, 2)
            putShort(34, 16)
            "data".toByteArray().copyInto(h, 36)
            putInt(40, dataSize)
        }
    }

    private fun shortArrayToBytes(s: ShortArray): ByteArray {
        val b = ByteArray(s.size * 2)
        s.forEachIndexed { i, v ->
            b[i * 2] = (v.toInt() and 0xff).toByte()
            b[i * 2 + 1] = (v.toInt() shr 8 and 0xff).toByte()
        }
        return b
    }

    private fun sine(freq: Double, t: Double) =
        sin(2.0 * PI * freq * t)

    private fun fade(
        i: Int,
        total: Int,
        fadeIn: Double = 0.05,
        fadeOut: Double = 0.15
    ): Double {
        val p = i.toDouble() / total
        val fi = if (p < fadeIn) p / fadeIn else 1.0
        val fo = if (p > 1 - fadeOut) {
            (1 - p) / fadeOut
        } else {
            1.0
        }
        return fi * fo
    }

    private fun genTap() = buildWav(durationMs = 40) { i, n ->
        val t = i / 44100.0
        (sine(800.0, t) * 0.3 + sine(1200.0, t) * 0.2) *
            fade(i, n, fadeOut = 0.6)
    }

    private fun genCategorySelect() = buildWav(durationMs = 80) { i, n ->
        val t = i / 44100.0
        val freq = if (t < 0.02) 600.0 else 900.0
        sine(freq, t) * 0.45 * fade(i, n, 0.02, 0.4)
    }

    private fun genLevelSelect() = buildWav(durationMs = 100) { i, n ->
        val t = i / 44100.0
        val f1 = sine(700.0, t) * if (t < 0.05) 1.0 else 0.0
        val f2 = sine(1050.0, t) * if (t >= 0.05) 1.0 else 0.0
        (f1 + f2) * 0.4 * fade(i, n, 0.01, 0.3)
    }

    private fun genCellFill() = buildWav(durationMs = 80) { i, n ->
        val t = i / 44100.0
        val freq = 900.0 - t * 1500.0
        sine(freq.coerceAtLeast(600.0), t) * 0.5 *
            fade(i, n, 0.005, 0.5)
    }

    private fun genCellMark() = buildWav(durationMs = 60) { i, n ->
        val t = i / 44100.0
        (sine(450.0, t) * 0.3 + sine(670.0, t) * 0.15) *
            fade(i, n, 0.01, 0.5)
    }

    private fun genWrongCell() = buildWav(durationMs = 180) { i, n ->
        val t = i / 44100.0
        val buzz = sine(180.0, t) + sine(270.0, t) * 0.7
        val vibrato = sin(2.0 * PI * 12.0 * t)
        buzz * (1.0 + 0.3 * vibrato) * 0.4 *
            fade(i, n, 0.01, 0.4)
    }

    private fun genHeartBreak() = buildWav(durationMs = 250) { i, n ->
        val t = i / 44100.0
        val freq = 300.0 * exp(-t * 8.0) + 80.0
        val impact = sine(freq, t) * exp(-t * 10.0)
        val crack = if (t < 0.02) {
            sin(t * 5000.0) * 0.5
        } else {
            0.0
        }
        (impact + crack) * 0.6
    }

    private fun genGameOver() = buildWav(durationMs = 700) { i, n ->
        val t = i / 44100.0
        val tones = listOf(
            Triple(0.0, 0.2, 400.0),
            Triple(0.22, 0.42, 320.0),
            Triple(0.44, 0.70, 220.0)
        )
        var out = 0.0
        tones.forEach { (start, end, freq) ->
            if (t in start..end) {
                val local = (t - start) / (end - start)
                out += sine(freq, t) *
                    exp(-local * 3.0) * 0.5
            }
        }
        out
    }

    private fun genRowSatisfied() = buildWav(durationMs = 150) { i, n ->
        val t = i / 44100.0
        val base = sine(880.0, t)
        val fifth = sine(1320.0, t) * 0.5
        (base + fifth) * 0.35 * fade(i, n, 0.01, 0.5)
    }

    private fun genPuzzleWin() = buildWav(durationMs = 800) { i, n ->
        val t = i / 44100.0
        val notes = listOf(
            Triple(0.0, 0.2, 523.25),
            Triple(0.18, 0.38, 659.25),
            Triple(0.36, 0.56, 783.99),
            Triple(0.54, 0.80, 1046.5)
        )
        var out = 0.0
        notes.forEach { (start, end, freq) ->
            if (t in start..end) {
                val local = (t - start) / (end - start)
                val env = sin(PI * local)
                out += sine(freq, t) * env * 0.45
                out += sine(freq * 2, t) * env * 0.12
            }
        }
        out
    }

    private fun genHint() = buildWav(durationMs = 300) { i, n ->
        val t = i / 44100.0
        val baseFreq = 600.0 + t * 2000.0
        val shimmer = sine(baseFreq, t)
        val shimmer2 = sine(baseFreq * 1.5, t) * 0.4
        val twinkle = sin(2.0 * PI * 30.0 * t) * 0.3 + 0.7
        (shimmer + shimmer2) * twinkle *
            fade(i, n, 0.02, 0.4) * 0.45
    }

    private fun genLevelStart() = buildWav(durationMs = 350) { i, n ->
        val t = i / 44100.0
        val progress = t / 0.35
        val freq = 150.0 + progress * 600.0
        val noise = sin(t * freq * 2.0 * PI)
        val tone = sine(freq, t) * 0.3
        (noise * 0.4 + tone) * fade(i, n, 0.05, 0.3)
    }

    private fun genDialogOpen() = buildWav(durationMs = 150) { i, n ->
        val t = i / 44100.0
        val freq = 400.0 + t * 300.0
        sine(freq, t) * 0.35 * fade(i, n, 0.1, 0.3)
    }

    private fun genBackNav() = buildWav(durationMs = 120) { i, n ->
        val t = i / 44100.0
        val freq = 800.0 - t * 500.0
        sine(freq.coerceAtLeast(200.0), t) * 0.3 *
            fade(i, n, 0.05, 0.2)
    }

    private fun genMarkToggle() = buildWav(durationMs = 60) { i, n ->
        val t = i / 44100.0
        val click1 = if (t < 0.02) sine(1000.0, t) * 0.5 else 0.0
        val click2 = if (t >= 0.03 && t < 0.05) {
            sine(1200.0, t) * 0.3
        } else {
            0.0
        }
        (click1 + click2) * fade(i, n, 0.01, 0.3)
    }

    private fun genAllStars() = buildWav(durationMs = 600) { i, n ->
        val t = i / 44100.0
        var out = 0.0
        listOf(0.0 to 1047.0, 0.15 to 1319.0, 0.30 to 1568.0)
            .forEach { (startT, freq) ->
                if (t >= startT && t < startT + 0.25) {
                    val local = (t - startT) / 0.25
                    val env = sin(PI * local)
                    out += sine(freq, t) * env * 0.4
                    out += sine(freq * 2, t) * env * 0.15
                }
            }
        out
    }

    private fun genDailyComplete() = buildWav(durationMs = 1000) { i, n ->
        val t = i / 44100.0
        val melody = listOf(
            Triple(0.0, 0.22, 523.25),
            Triple(0.20, 0.42, 659.25),
            Triple(0.40, 0.62, 783.99),
            Triple(0.60, 0.85, 1046.5),
            Triple(0.82, 1.00, 1046.5)
        )
        var out = 0.0
        melody.forEach { (start, end, freq) ->
            if (t in start..end) {
                val local = (t - start) / (end - start)
                val env = sin(PI * local)
                out += sine(freq, t) * env * 0.4
                out += sine(freq * 1.5, t) * env * 0.2
            }
        }
        out
    }

    private fun genStreakMilestone() = buildWav(durationMs = 700) { i, n ->
        val t = i / 44100.0
        val notes = listOf(
            Triple(0.0, 0.15, 440.0),
            Triple(0.13, 0.28, 550.0),
            Triple(0.26, 0.40, 660.0),
            Triple(0.38, 0.70, 880.0)
        )
        var out = 0.0
        notes.forEach { (start, end, freq) ->
            if (t in start..end) {
                val local = (t - start) / (end - start)
                val env = if (local < 0.1) local / 0.1 else exp(-local * 2.0)
                out += sine(freq, t) * env * 0.45
            }
        }
        out
    }

    private fun genTimerWarning() = buildWav(durationMs = 80) { i, n ->
        val t = i / 44100.0
        val tick = sine(1200.0, t) * exp(-t * 40.0)
        tick * 0.6
    }

    private fun play(soundId: Int, volume: Float = 1f, rate: Float = 1f) {
        if (!isSoundEnabled || !isLoaded || soundId == 0) return
        soundPool.play(soundId, volume, volume, 1, 0, rate)
    }

    fun playButtonTap() = play(idButtonTap, 0.5f)
    fun playCategorySelect() = play(idCategorySelect, 0.7f)
    fun playLevelSelect() = play(idLevelSelect, 0.7f)
    fun playCellFill() = play(idCellFill, 0.8f)
    fun playCellMark() = play(idCellMark, 0.6f)
    fun playWrongCell() = play(idWrongCell, 0.9f)
    fun playHeartBreak() = play(idHeartBreak, 1.0f)
    fun playGameOver() = play(idGameOver, 1.0f)
    fun playRowSatisfied() = play(idRowSatisfied, 0.7f)
    fun playPuzzleWin() = play(idPuzzleWin, 1.0f)
    fun playHint() = play(idHint, 0.8f)
    fun playLevelStart() = play(idLevelStart, 0.6f)
    fun playDialogOpen() = play(idDialogOpen, 0.5f)
    fun playBackNav() = play(idBackNav, 0.4f)
    fun playMarkToggle() = play(idMarkToggle, 0.6f)
    fun playAllStars() = play(idAllStars, 1.0f)
    fun playDailyComplete() = play(idDailyComplete, 1.0f)
    fun playStreakMilestone() = play(idStreakMilestone, 1.0f)
    fun playTimerWarning() = play(idTimerWarning, 0.8f)

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun release() {
        soundPool.release()
        clearInstance()
    }
}
