package com.it.roomdb

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

    private val items = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        db = AppDatabase.getDatabase(this)

        input = findViewById(R.id.categoryEditText)
        button = findViewById(R.id.addButton)
        listView = findViewById(R.id.listView)

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
//testing

}