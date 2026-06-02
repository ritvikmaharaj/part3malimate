package com.it.roomdb

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.it.roomdb.data.db.AppDatabase
import com.it.roomdb.data.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var input: EditText
    private lateinit var button: Button
    private lateinit var listView: ListView
    private lateinit var btnGoToGoals: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnSearchCategory: Button
    private lateinit var btnSearchExpense: Button
    private lateinit var btnGamification: Button
    private lateinit var btnSavingsGoals: Button

    private val items = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        db = AppDatabase.getDatabase(this)

        input = findViewById(R.id.categoryEditText)
        button = findViewById(R.id.addButton)
        listView = findViewById(R.id.listView)
        btnGoToGoals = findViewById(R.id.btnGoToGoals)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnSearchCategory = findViewById(R.id.btnSearchCategory)
        btnSearchCategory = findViewById(R.id.btnSearchCategory)
        btnGamification = findViewById(R.id.btnGamification)
        btnSavingsGoals = findViewById(R.id.btnSavingsGoals)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        loadCategories()

        button.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                db.categoryDao().insert(Category(name = text))
                loadCategories()
            }
            input.text.clear()
        }

        btnGoToGoals.setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        btnSearchCategory.setOnClickListener {
            startActivity(Intent(this, CategorySearchActivity::class.java))
        }

        btnSearchExpense.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        btnGamification.setOnClickListener {
            startActivity(Intent(this, GamificationActivity::class.java))
        }

        btnSavingsGoals.setOnClickListener {
            startActivity(Intent(this, SavingsGoalsActivity::class.java))
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = db.categoryDao().getAllCategories()
            runOnUiThread {
                items.clear()
                items.addAll(data.map { it.name })
                adapter.notifyDataSetChanged()
            }
        }
    }
}