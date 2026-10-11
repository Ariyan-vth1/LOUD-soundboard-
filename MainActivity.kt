package com.loud.soundboard

import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {
    private val purple = Color.rgb(88, 101, 242)
    private val background = Color.rgb(29, 30, 35)
    private val panel = Color.rgb(42, 43, 50)
    private val textColor = Color.WHITE
    private val sounds = mutableListOf<Pair<String, Uri>>()
    private var player: MediaPlayer? = null
    private lateinit var soundList: LinearLayout
    private val PICK_AUDIO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
            setPadding(dp(18), dp(22), dp(18), dp(18))
        }

        val title = TextView(this).apply {
            text = "LOUD"
            textSize = 30f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "SOUNDBOARD  •  YOUR SOUNDS, YOUR RULES"
            textSize = 11f
            setTextColor(Color.rgb(180, 182, 195))
            setPadding(0, dp(3), 0, dp(18))
        }
        root.addView(subtitle)

        val discord = makeButton("OPEN DISCORD", purple)
        discord.setOnClickListener {
            val launch = packageManager.getLaunchIntentForPackage("com.discord")
            if (launch != null) startActivity(launch)
            else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.com/app")))
        }
        root.addView(discord, matchWrap())

        val add = makeButton("+  ADD YOUR AUDIO", Color.rgb(59, 165, 93))
        add.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/*"
            }
            startActivityForResult(intent, PICK_AUDIO)
        }
        val addParams = matchWrap()
        addParams.topMargin = dp(10)
        root.addView(add, addParams)

        val heading = TextView(this).apply {
            text = "YOUR SOUNDS"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textColor)
            setPadding(0, dp(22), 0, dp(10))
        }
        root.addView(heading)

        val scroll = ScrollView(this)
        soundList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(soundList)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val note = TextView(this).apply {
            text = "Note: audio plays on this phone. To let everyone hear it in a Discord voice channel, connect a Discord voice bot or a supported audio-routing setup."
            textSize = 12f
            setTextColor(Color.rgb(185, 185, 195))
            setPadding(0, dp(12), 0, 0)
        }
        root.addView(note)
        setContentView(root)
        renderSounds()
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val name = uri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() } ?: "Custom audio ${sounds.size + 1}"
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            sounds.add(name to uri)
            renderSounds()
        }
    }

    private fun renderSounds() {
        soundList.removeAllViews()
        if (sounds.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No sounds yet\\nTap ADD YOUR AUDIO to import an audio file."
                textSize = 14f
                setTextColor(Color.rgb(180, 182, 195))
                gravity = Gravity.CENTER
                setPadding(dp(12), dp(30), dp(12), dp(30))
            }
            soundList.addView(empty)
        }
        sounds.forEachIndexed { index, item ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), dp(10), dp(8), dp(10))
                background = rounded(panel, 14)
            }
            val label = TextView(this).apply {
                text = item.first
                textSize = 14f
                setTextColor(textColor)
                maxLines = 2
            }
            row.addView(label, LinearLayout.LayoutParams(0, -2, 1f))
            val play = Button(this).apply {
                text = "PLAY"
                textSize = 12f
                setTextColor(Color.WHITE)
                background = rounded(purple, 10)
                setOnClickListener { playSound(item.second) }
            }
            row.addView(play)
            val stop = Button(this).apply {
                text = "■"
                textSize = 12f
                setTextColor(Color.WHITE)
                background = rounded(Color.rgb(210, 65, 75), 10)
                setOnClickListener { stopSound() }
            }
            row.addView(stop)
            val params = matchWrap()
            params.bottomMargin = dp(8)
            soundList.addView(row, params)
        }
    }

    private fun playSound(uri: Uri) {
        stopSound()
        try {
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(this@MainActivity, uri)
                setOnCompletionListener { stopSound() }
                prepare()
                start()
            }
        } catch (_: Exception) {
            stopSound()
        }
    }

    private fun stopSound() {
        try { player?.stop() } catch (_: Exception) {}
        player?.release()
        player = null
    }

    override fun onDestroy() {
        stopSound()
        super.onDestroy()
    }

    private fun makeButton(label: String, color: Int) = Button(this).apply {
        text = label
        textSize = 13f
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(Color.WHITE)
        background = rounded(color, 14)
        isAllCaps = false
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
