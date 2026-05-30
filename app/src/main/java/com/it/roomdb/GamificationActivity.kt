package com.it.roomdb

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GamificationActivity : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvBadges: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnEarnPoints: Button
    private lateinit var btnBack: Button

    private val sharedPrefKey = "MaliMateGamification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)

        tvPoints = findViewById(R.id.tvPoints)
        tvLevel = findViewById(R.id.tvLevel)
        tvBadges = findViewById(R.id.tvBadges)
        tvStatus = findViewById(R.id.tvStatus)
        btnEarnPoints = findViewById(R.id.btnEarnPoints)
        btnBack = findViewById(R.id.btnBack)

        loadStats()

        // Award points when user logs a transaction
        btnEarnPoints.setOnClickListener {
            awardPoints(10)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun awardPoints(points: Int) {
        val prefs = getSharedPreferences(sharedPrefKey, MODE_PRIVATE)
        val currentPoints = prefs.getInt("points", 0)
        val newPoints = currentPoints + points

        prefs.edit().putInt("points", newPoints).apply()

        loadStats()
        tvStatus.text = "+$points points earned!"
    }

    private fun loadStats() {
        val prefs = getSharedPreferences(sharedPrefKey, MODE_PRIVATE)
        val points = prefs.getInt("points", 0)
        val level = calculateLevel(points)
        val badges = calculateBadges(points)

        tvPoints.text = "Points: $points"
        tvLevel.text = "Level: $level"
        tvBadges.text = badges
    }

    private fun calculateLevel(points: Int): String {
        return when {
            points >= 500 -> "Gold 🥇"
            points >= 200 -> "Silver 🥈"
            points >= 100 -> "Bronze 🥉"
            else -> "Beginner 🌱"
        }
    }

    private fun calculateBadges(points: Int): String {
        val badges = mutableListOf<String>()
        if (points >= 10) badges.add("🏅 First Transaction")
        if (points >= 50) badges.add("⭐ Getting Started")
        if (points >= 100) badges.add("🔥 On a Roll")
        if (points >= 200) badges.add("💪 Budget Master")
        if (points >= 500) badges.add("👑 MaliMate Champion")

        return if (badges.isEmpty()) "No badges yet — start logging!"
        else badges.joinToString("\n")
    }
}