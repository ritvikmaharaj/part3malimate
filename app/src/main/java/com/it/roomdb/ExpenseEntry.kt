package com.it.roomdb

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.it.roomdb.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.it.roomdb.R

class ExpenseEntryActivity : AppCompatActivity() {

    private lateinit var etCategory: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_expense_entry)

        etCategory = findViewById(R.id.etCategory)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {

            saveExpense()
        }
    }

    private fun saveExpense() {

        val category = etCategory.text.toString()
        val amountText = etAmount.text.toString()
        val date = etDate.text.toString()

        if (category.isEmpty() ||
            amountText.isEmpty() ||
            date.isEmpty()) {

            Toast.makeText(
                this,
                "Fill all fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val amount = amountText.toFloat()

        val expense = Expense(
            category,
            amount,
            date
        )

        val userId = FirebaseAuth.getInstance()
            .currentUser!!.uid

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("expenses")

        ref.push().setValue(expense)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Expense Saved",
                    Toast.LENGTH_SHORT
                ).show()

                etCategory.text.clear()
                etAmount.text.clear()
                etDate.text.clear()
            }
    }
}
