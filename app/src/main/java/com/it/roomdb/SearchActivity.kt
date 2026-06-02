package com.it.roomdb

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.it.roomdb.data.db.AppDatabase
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    //global dependencies
    private lateinit var edtEntryStartDate: EditText
    private lateinit var edtEntryEndDate: EditText
    private lateinit var btnSearch: Button
    private lateinit var txtTotal: TextView
    private lateinit var entriesContainer: LinearLayout

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        db = AppDatabase.getDatabase(this)

        edtEntryStartDate = findViewById(R.id.edtEntryStartDate)
        edtEntryEndDate = findViewById(R.id.edtEntryEndDate)
        txtTotal = findViewById(R.id.txtTotal)
        entriesContainer = findViewById(R.id.entriesContainer)

        btnSearch = findViewById(R.id.btnSearch)

        edtEntryStartDate.setOnClickListener {
            showStartDatePicker()
        }

        edtEntryEndDate.setOnClickListener {
            showEndDatePicker()
        }

        btnSearch.setOnClickListener {
            searchEntries()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showStartDatePicker(){

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtEntryStartDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showEndDatePicker(){

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtEntryEndDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun searchEntries() {

        val startDate = edtEntryStartDate.text.toString()
        val endDate = edtEntryEndDate.text.toString()

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please enter both start and end dates", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val filteredEntries = db.expenseDao().getExpensesByDate(startDate, endDate)

            runOnUiThread {
                entriesContainer.removeAllViews()

                if (filteredEntries.isEmpty()) {
                    txtTotal.text = "Total: R0.00"

                    val noResultsText = TextView(this@SearchActivity)
                    noResultsText.text = "No expenses found for the selected period"
                    noResultsText.textSize = 16f

                    entriesContainer.addView(noResultsText)
                    return@runOnUiThread
                }

                var totalAmount = 0.0

                for (entry in filteredEntries) {

                    val entryText = TextView(this@SearchActivity)
                    entryText.text =
                        "Category: ${entry.categoryId}\n" +
                                "Amount: R${entry.amount}\n" +
                                "Date: ${entry.date}\n" +
                                "Description: ${entry.description}"

                    // Styling
                    entryText.textSize = 16f

                    // Add entry details to screen
                    entriesContainer.addView(entryText)

                    // If an image was saved with this entry
                    if (entry.imagePath != null) {
                        val imageView = ImageView(this@SearchActivity)

                        // Set layout parameters
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        // Maintain image proportions
                        imageView.adjustViewBounds = true
                        // Prevent very large images
                        imageView.maxHeight = 800

                        try {
                            // Load saved image from URI
                            imageView.setImageURI(Uri.parse(entry.imagePath))
                            // Display image neatly
                            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                            // Add image below entry details
                            entriesContainer.addView(imageView)

                        } catch (e: Exception) {
                            // If image fails to load
                            val imageErrorText = TextView(this@SearchActivity)
                            imageErrorText.text = "Image could not be loaded"
                            imageErrorText.textSize = 14f
                            entriesContainer.addView(imageErrorText)
                        }
                    }
                }
            }
        }
    }

}