package com.vc.mypacapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.vc.mypacapp.R

class MainActivity : AppCompatActivity() {

    companion object {
        val itemNames = mutableListOf<String>()
        val categories = mutableListOf<String>()
        val quantities = mutableListOf<Int>()
        val notes = mutableListOf<String>()

        val availableCategories = listOf("clothing", "toiletries", "documents", "electronics", "misc")
        val availableQuantities = (1..10).map { it.toString() }

        fun preloadItems() {
            if (itemNames.isNotEmpty()) return
            itemNames.addAll(listOf("T-shirts and pants", "tooth brush", "shoes", "passport"))
            categories.addAll(listOf("clothing", "toiletries", "clothing", "documents"))
            quantities.addAll(listOf(5, 1, 2, 1))
            notes.addAll(
                listOf(
                    "comfortable for traveling",
                    "hygiene essential",
                    "walking - dress up",
                    "important for traveling"
                )
            )
        }
    }

    private lateinit var etItemName: AutoCompleteTextView
    private lateinit var etCategory: AutoCompleteTextView
    private lateinit var spinnerQuantity: Spinner
    private lateinit var etNotes: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnPreload: Button
    private lateinit var btnViewList: Button

    private lateinit var itemNameAdapter: ArrayAdapter<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>

    private var selectedValidItem = false
    private var selectedValidCategory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etItemName = findViewById(R.id.etItemName)
        etCategory = findViewById(R.id.etCategory)
        spinnerQuantity = findViewById(R.id.spinnerQuantity)
        etNotes = findViewById(R.id.etNotes)
        btnAdd = findViewById(R.id.btnAdd)
        btnPreload = findViewById(R.id.btnPreload)
        btnViewList = findViewById(R.id.btnViewList)

        // Preload items on startup
        preloadItems()

        // Setup item name dropdown
        itemNameAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemNames)
        etItemName.setAdapter(itemNameAdapter)
        etItemName.threshold = 1
        etItemName.setOnItemClickListener { _, _, _, _ ->
            selectedValidItem = true
            etItemName.error = null
        }
        etItemName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedValidItem = false
                etItemName.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        etItemName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = etItemName.text.toString().trim()
                selectedValidItem = itemNames.any { it.equals(text, ignoreCase = true) }
                if (!selectedValidItem) etItemName.error = "Select a valid item from the dropdown"
            }
        }

        // Setup category dropdown
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableCategories)
        etCategory.setAdapter(categoryAdapter)
        etCategory.threshold = 1
        etCategory.setOnItemClickListener { _, _, _, _ ->
            selectedValidCategory = true
            etCategory.error = null
        }
        etCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedValidCategory = false
                etCategory.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        etCategory.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = etCategory.text.toString().trim()
                selectedValidCategory = availableCategories.any { it.equals(text, ignoreCase = true) }
                if (!selectedValidCategory) etCategory.error = "Select a valid category from the dropdown"
            }
        }

        // Setup quantity spinner
        val quantityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, availableQuantities)
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerQuantity.adapter = quantityAdapter

        // Add button click listener
        btnAdd.setOnClickListener {
            val name = etItemName.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val quantityStr = spinnerQuantity.selectedItem.toString()
            val note = etNotes.text.toString().trim()

            var hasError = false
            etItemName.error = null
            etCategory.error = null

            if (name.isEmpty()) {
                etItemName.error = "Item name is required"
                hasError = true
            } else if (!selectedValidItem) {
                etItemName.error = "Select a valid item from the dropdown"
                hasError = true
            }

            if (category.isEmpty()) {
                etCategory.error = "Category is required"
                hasError = true
            } else if (!selectedValidCategory) {
                etCategory.error = "Select a valid category from the dropdown"
                hasError = true
            }

            val quantity = quantityStr.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Please select a valid quantity", Toast.LENGTH_SHORT).show()
                hasError = true
            }

            if (hasError) return@setOnClickListener

            // Avoid duplicate entries (optional)
            if (!itemNames.any { it.equals(name, ignoreCase = true) }) {
                itemNames.add(name)
                itemNameAdapter.notifyDataSetChanged()
            }
            if (!categories.any { it.equals(category, ignoreCase = true) }) {
                categories.add(category)
            }
            quantities.add(quantity!!)
            notes.add(note)

            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()

            // Reset form fields
            etItemName.text.clear()
            etCategory.text.clear()
            spinnerQuantity.setSelection(0)
            etNotes.text.clear()
            selectedValidItem = false
            selectedValidCategory = false
        }

        // Preload button click listener
        btnPreload.setOnClickListener {
            preloadItems()
            itemNameAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Packing list loaded. Please select from dropdown.", Toast.LENGTH_SHORT).show()
        }

        // View list button click listener
        btnViewList.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}
