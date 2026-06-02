package com.it.roomdb

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.it.roomdb.Expense
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GraphActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart

    private val categoryTotals = HashMap<String, Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        barChart = findViewById(R.id.barChart)

        loadExpenses()
    }

    private fun loadExpenses() {

        val userId = FirebaseAuth.getInstance()
            .currentUser!!.uid

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("expenses")

        ref.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    categoryTotals.clear()

                    for (expenseSnapshot in snapshot.children) {

                        val expense = expenseSnapshot
                            .getValue(Expense::class.java)

                        if (expense != null) {

                            val currentTotal =
                                categoryTotals[expense.category] ?: 0f

                            categoryTotals[expense.category] =
                                currentTotal + expense.amount
                        }
                    }

                    displayChart()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun displayChart() {

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        var index = 0f

        for ((category, total) in categoryTotals) {

            entries.add(BarEntry(index, total))
            labels.add(category)

            index++
        }

        val dataSet = BarDataSet(
            entries,
            "Expenses Per Category"
        )

        dataSet.colors = listOf(
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.MAGENTA,
            Color.CYAN
        )

        dataSet.valueTextSize = 14f

        val data = BarData(dataSet)

        barChart.data = data

        val xAxis = barChart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        addGoalLines()

        barChart.animateY(1000)

        barChart.invalidate()
    }

    private fun addGoalLines() {

        val yAxis = barChart.axisLeft

        val minLine = LimitLine(
            200f,
            "Min Goal"
        )

        minLine.lineColor = Color.GREEN
        minLine.lineWidth = 2f

        val maxLine = LimitLine(
            500f,
            "Max Goal"
        )

        maxLine.lineColor = Color.RED
        maxLine.lineWidth = 2f

        yAxis.addLimitLine(minLine)
        yAxis.addLimitLine(maxLine)
    }
}
