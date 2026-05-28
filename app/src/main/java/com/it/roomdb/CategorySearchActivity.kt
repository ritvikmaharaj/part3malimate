package com.it.roomdb

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.it.roomdb.data.db.AppDatabase
import kotlinx.coroutines.launch

class CategorySearchActivity : AppCompatActivity() {

    //global dependencies
    private lateinit var edtCatStartDate: EditText
    private lateinit var edtCatEndDate: EditText
    private lateinit var edtCategorySearch: EditText

    private lateinit var btnSearch: Button
    private lateinit var txtTotal: TextView
    private lateinit var txtCategory: TextView

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_search)

        db = AppDatabase.getDatabase(this)

        edtCatStartDate = findViewById(R.id.edtCatStartDate)
        edtCatEndDate = findViewById(R.id.edtCatEndDate)
        edtCategorySearch = findViewById(R.id.edtCategorySearch)
        txtTotal = findViewById(R.id.txtTotal)
        txtCategory = findViewById(R.id.txtCategory)

        btnSearch = findViewById(R.id.btnSearch)

        //Methods
        edtCatStartDate.setOnClickListener {
            showStartDatePicker()
        }

        edtCatEndDate.setOnClickListener {
            showEndDatePicker()
        }

        btnSearch.setOnClickListener {
            searchCategories()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun showStartDatePicker(){
        //initialize variables
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
                edtCatStartDate.setText(selectedDate)
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
                edtCatEndDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun searchCategories() {
        //dates users input
        val startDate = edtCatStartDate.text.toString()
        val endDate = edtCatEndDate.text.toString()
        //category - for user input
        val category = edtCategorySearch.text.toString()

        //validation
        if (startDate.isEmpty() || endDate.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please enter both dates and category", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            //search - to match user input to entry list with same category
            val search = db.expenseDao().getTotalPerCategory(category, startDate, endDate)
            val catSearch = db.categoryDao().getAllCategories().filter { it.equals(category) }

            if (catSearch.toString() == category) {
                runOnUiThread {
                    txtCategory.text = "No categories match."
                    txtTotal.text = "Total : R0.00"
                }
                return@launch
            }

            //check if user entered category
            if (search.isEmpty()) {
                runOnUiThread {
                    txtCategory.text = "No categories found for selected peroid."
                    txtTotal.text = "Total : R0.00"
                }
                return@launch
            }


            var categoriesText = " "
            var totalAmount = 0.0

            for (cat in search) {
                categoriesText += "Category: ${cat.categoryId}\n"
                categoriesText += "Amount: ${cat.amount}\n\n"

                totalAmount += cat.amount
            }

            txtCategory.text = categoriesText
            txtTotal.text = "Total : R%.2f".format(totalAmount)
        }
    }
}