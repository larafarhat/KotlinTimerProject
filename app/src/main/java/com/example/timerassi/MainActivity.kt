package com.example.timerassi

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    var ticks = 0
    var running = false
    var wasRunning = false
    var paused = false
    var chosenTime = 0

    val TICKS_KEY = "ticks"
    val RUNNING_KEY = "running"
    val WAS_RUNNING_KEY = "was_running"
    val PAUSED_KEY = "paused"
    val CHOSEN_TIME_KEY = "chosen_time"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState != null) {
            ticks = savedInstanceState.getInt(TICKS_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            wasRunning = savedInstanceState.getBoolean(WAS_RUNNING_KEY)
            paused = savedInstanceState.getBoolean(PAUSED_KEY)
            chosenTime = savedInstanceState.getInt(CHOSEN_TIME_KEY)
            if(!wasRunning) {
                if (running || paused) {
                    runTimer()
                }
            }
        }
        updateTimerText()
        val incrementButton = findViewById<Button>(R.id.increment_button)
        incrementButton.setOnClickListener {
            incrementTimer()
        }

        val decrementButton = findViewById<Button>(R.id.decrement_button)
        decrementButton.setOnClickListener {

            decrementTimer()
        }

        val setTimerButton = findViewById<Button>(R.id.set_25)
        setTimerButton.setOnClickListener {
            onClickSetTimer(it)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(TICKS_KEY, ticks)
        outState.putBoolean(RUNNING_KEY, running)
        outState.putBoolean(WAS_RUNNING_KEY, wasRunning)
        outState.putBoolean(PAUSED_KEY, paused)
        outState.putInt(CHOSEN_TIME_KEY, chosenTime)
        super.onSaveInstanceState(outState)
    }



    override fun onPause() {
        super.onPause()

        if (running) {
            paused = true
            running = false
        }
    }
    override fun onResume() {
        super.onResume()

        if (paused) {
            running = true
            paused = false
            runTimer()
        }
    }


    private fun updateTimerText() {
        val TextView = findViewById<TextView>(R.id.time_passed_text_view)
        val seconds = ticks % 60
        val minutes = ticks / 60 % 60
        val hours = ticks / 3600
        val stringToDisplay = String.format("%d: %02d:%02d ", hours, minutes, seconds)
        TextView.text = stringToDisplay

        if (running) {
            val color = when {
                ticks <= 0 -> Color.RED
                ticks <= chosenTime / 3 -> Color.RED
                ticks < 2 * chosenTime / 3 -> Color.rgb(255, 199, 13)
                ticks >= 2 * chosenTime / 3 -> Color.rgb(0, 166, 44)

                else -> Color.BLACK
            }
            TextView.setTextColor(color)
        }
    }


    fun runTimer() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (running && ticks > 0) {
                    ticks--
                    updateTimerText()
                    handler.postDelayed(this, 1000)

                } else if (ticks == 0 && !paused) {
                    showPopup()
                }
                if (ticks == 0) {
                    val TextView = findViewById<TextView>(R.id.time_passed_text_view)
                    TextView.setTextColor(Color.BLACK)
                    running = false
                    chosenTime = 0
                }

            }

        })
    }


    private fun showPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Timer Ended")
        builder.setMessage("Your timer has ended.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun onClickStart(view: View?) {

        if (ticks != 0 && !running) {

            running = true
            runTimer()
            wasRunning = true

        }
    }

    fun onClickStop(view: View?) {
        running = false
        wasRunning = false
        if (ticks != 0) {
            paused = true
        }
    }

    fun onClickReset(view: View?) {
        running = false
        ticks = 0
        chosenTime = 0
        wasRunning = false
        paused = false
        updateTimerText()
        val textView = findViewById<TextView>(R.id.time_passed_text_view)
        textView.setTextColor(Color.BLACK)

    }

    fun incrementTimer() {
        if (!running && !paused) {
            ticks = ticks + 60
            chosenTime = chosenTime + 60
            updateTimerText()
        }
    }

    fun decrementTimer() {
        if (!running && !paused) {
            if (ticks > 0) {
                ticks = ticks - 60
                chosenTime = chosenTime - 60
                updateTimerText()
            }
        }
    }

    fun onClickSetTimer(view: View) {

        ticks = 25 * 60
        chosenTime = ticks
        updateTimerText()
    }


}