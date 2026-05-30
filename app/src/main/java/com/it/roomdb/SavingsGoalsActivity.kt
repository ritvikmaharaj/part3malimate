package com.it.roomdb

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.it.roomdb.data.db.AppDatabase
import com.it.roomdb.data.entity.SavingsGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavingsGoalsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var etGoalName: EditText
    private lateinit var etTargetAmount: EditText
    private lateinit var etContribution: EditText
    private lateinit var btnAddGoal: Button
    private lateinit var btnContribute: Button
    private lateinit var listView: ListView
    private lateinit var tvSelected: TextView

    private val items = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var goals = listOf<SavingsGoal>()
    private var selectedGoalIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_goals)

        db = AppDatabase.getDatabase(this)

        etGoalName = findViewById(R.id.etGoalName)
        etTargetAmount = findViewById(R.id.etTargetAmount)
        etContribution = findViewById(R.id.etContribution)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        btnContribute = findViewById(R.id.btnContribute)
        listView = findViewById(R.id.savingsListView)
        tvSelected = findViewById(R.id.tvSelectedGoal)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        loadGoals()

        btnAddGoal.setOnClickListener {
            val name = etGoalName.text.toString().trim()
            val targetText = etTargetAmount.text.toString().trim()

            if (name.isEmpty() || targetText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val target = targetText.toDoubleOrNull()
            if (target == null || target <= 0) {
                Toast.makeText(this, "Enter a valid target amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                db.savingsGoalDao().insert(SavingsGoal(name = name, targetAmount = target))
                loadGoals()
            }

            etGoalName.text.clear()
            etTargetAmount.text.clear()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedGoalIndex = position
            tvSelected.text = "Selected: ${goals[position].name}"
        }

        btnContribute.setOnClickListener {
            if (selectedGoalIndex == -1) {
                Toast.makeText(this, "Select a goal first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contribText = etContribution.text.toString().trim()
            val contrib = contribText.toDoubleOrNull()

            if (contrib == null || contrib <= 0) {
                Toast.makeText(this, "Enter a valid contribution amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = goals[selectedGoalIndex]
            val updated = goal.copy(currentAmount = goal.currentAmount + contrib)

            lifecycleScope.launch(Dispatchers.IO) {
                db.savingsGoalDao().update(updated)
                loadGoals()
            }

            etContribution.text.clear()

            if (updated.currentAmount >= updated.targetAmount) {
                Toast.makeText(this, "🎉 Goal achieved! Well done!", Toast.LENGTH_LONG).show()
                awardGamificationPoints(50)
            } else {
                Toast.makeText(this, "Contribution added!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGoals() {
        lifecycleScope.launch(Dispatchers.IO) {
            goals = db.savingsGoalDao().getAllGoals()
            runOnUiThread {
                items.clear()
                items.addAll(goals.map { goal ->
                    val progress = if (goal.targetAmount > 0)
                        (goal.currentAmount / goal.targetAmount * 100).toInt() else 0
                    "${goal.name} — R%.2f / R%.2f ($progress%%)".format(
                        goal.currentAmount, goal.targetAmount)
                })
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun awardGamificationPoints(points: Int) {
        val prefs = getSharedPreferences("MaliMateGamification", MODE_PRIVATE)
        val current = prefs.getInt("points", 0)
        prefs.edit().putInt("points", current + points).apply()
    }
}