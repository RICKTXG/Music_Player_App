package com.opensource.musicplayerapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var time_text: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var play_btn: ImageButton
    private var startTime = 0.0
    private var finalTime = 0.0
    private var oneTimeOnly = 0
    private var forwardTime = 10000
    private var backwardTime = 10000
    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        time_text = findViewById(R.id.time)
        seekBar = findViewById(R.id.seekbar)
        play_btn = findViewById(R.id.play)
        val title_text : TextView = findViewById(R.id.textView)
        var pause_btn : ImageButton = findViewById(R.id.pause)
        var forward_btn : ImageButton = findViewById(R.id.forward)
        var back_btn: ImageButton = findViewById(R.id.rewind)


        mediaPlayer = MediaPlayer.create(this, R.raw.nightchanges)

        seekBar.isClickable = false

        play_btn.setOnClickListener {
            mediaPlayer?.start()
            finalTime = mediaPlayer?.duration?.toDouble() ?: 0.0
            startTime = mediaPlayer?.currentPosition?.toDouble() ?: 0.0

            if (oneTimeOnly == 0) {
                seekBar.max = finalTime.toInt()
                oneTimeOnly = 1
            }

            val minutes = TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
            val seconds = TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                    TimeUnit.MINUTES.toSeconds(minutes)
            val timeLabel = String.format("%02d:%02d", minutes, seconds)
            time_text.text = timeLabel

            seekBar.progress = startTime.toInt()
            handler.postDelayed(UpdateSongTime, 1000) // Update every 1 second (1000 milliseconds)
        }

        // Setting the music title
        title_text.text = ""+ resources.getResourceEntryName(R.raw.nightchanges)

        // Stop Button
        pause_btn.setOnClickListener(){
            mediaPlayer?.pause()
        }

        // Forward Button
        forward_btn.setOnClickListener(){
            var temp = startTime
            if ((temp + forwardTime) <= finalTime){
                startTime = startTime + forwardTime
                mediaPlayer?.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this, "Can't make forward", Toast.LENGTH_SHORT).show()
            }
        }

        // Backward Button
        back_btn.setOnClickListener(){
            var temp = startTime.toInt()

            if ((temp - backwardTime) > 0){
                startTime = startTime - backwardTime
                mediaPlayer?.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this,
                    "Can't Jump backward",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private val UpdateSongTime: Runnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                startTime = player.currentPosition.toDouble()

                val minutes = TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                val seconds = TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(minutes)

                val timeLabel = String.format("%02d:%02d", minutes, seconds)
                time_text.text = timeLabel

                seekBar.progress = startTime.toInt()
                handler.postDelayed(this, 1000) // Update every 1 second (1000 milliseconds)
            }
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}

