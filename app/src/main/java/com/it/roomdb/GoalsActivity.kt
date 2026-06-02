package com.it.roomdb

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var tvGoalStatus: TextView

    private lateinit var progressBudget: ProgressBar
    private lateinit var tvPercentage: TextView
    private lateinit var tvBudgetFeedback: TextView

    private val sharedPrefKey = "MaliMateGoals"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoals = findViewById(R.id.btnSaveGoals)
        tvGoalStatus = findViewById(R.id.tvGoalStatus)

        progressBudget = findViewById(R.id.progressBudget)
        tvPercentage = findViewById(R.id.tvPercentage)
        tvBudgetFeedback = findViewById(R.id.tvBudgetFeedback)

        loadSavedGoals()

        btnSaveGoals.setOnClickListener {

            val minText = etMinGoal.text.toString()
            val maxText = etMaxGoal.text.toString()

            if (minText.isEmpty() || maxText.isEmpty()) {
                Toast.makeText(this, "Please enter both goals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val min = minText.toDoubleOrNull()
            val max = maxText.toDoubleOrNull()

            if (min == null || max == null) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (min < 0 || max < 0) {
                Toast.makeText(this, "Goals cannot be negative", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (min >= max) {
                Toast.makeText(
                    this,
                    "Minimum goal must be less than maximum goal",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            saveGoals(min, max)
            updateGoalStatus(min, max)

            Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveGoals(min: Double, max: Double) {

        val prefs = getSharedPreferences(sharedPrefKey, MODE_PRIVATE)

        prefs.edit()
            .putFloat("minGoal", min.toFloat())
            .putFloat("maxGoal", max.toFloat())
            .apply()
    }

    private fun loadSavedGoals() {

        val prefs = getSharedPreferences(sharedPrefKey, MODE_PRIVATE)

        val min = prefs.getFloat("minGoal", -1f)
        val max = prefs.getFloat("maxGoal", -1f)

        if (min != -1f && max != -1f) {

            etMinGoal.setText(min.toString())
            etMaxGoal.setText(max.toString())

            updateGoalStatus(min.toDouble(), max.toDouble())
        }
    }

    private fun updateGoalStatus(min: Double, max: Double) {

        tvGoalStatus.text =
            "Monthly Spending Goal:\nMinimum: R%.2f\nMaximum: R%.2f"
                .format(min, max)

        // Example spending amount
        val spentAmount = 2500.0

        // Calculate percentage
        val percentage = ((spentAmount / max) * 100).toInt()

        progressBudget.progress = percentage

        tvPercentage.text = "$percentage% of budget used"

        // Budget feedback
        if (spentAmount < min) {

            tvBudgetFeedback.text = "Below Minimum Spending"
            tvBudgetFeedback.setTextColor(Color.YELLOW)

        } else if (spentAmount > max) {

            tvBudgetFeedback.text = "Over Budget"
            tvBudgetFeedback.setTextColor(Color.RED)

        } else {

            tvBudgetFeedback.text = "Within Budget"
            tvBudgetFeedback.setTextColor(Color.GREEN)
        }
    }
}


