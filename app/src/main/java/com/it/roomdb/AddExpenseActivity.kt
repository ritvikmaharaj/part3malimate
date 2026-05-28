package com.it.roomdb

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.it.roomdb.data.dao.ExpenseDao
import com.it.roomdb.data.db.AppDatabase
import com.it.roomdb.data.entity.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AddExpenseActivity : AppCompatActivity() {

    // Database
    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    // UI
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSave: Button
    private lateinit var imagePreview: ImageView

    // Image URI
    private var photoUri: Uri? = null


    // CAMERA RESULT

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            imagePreview.setImageURI(photoUri)
        }
    }


    // GALLERY RESULT

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
            imagePreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Init DB
        db = AppDatabase.getDatabase(this)
        expenseDao = db.expenseDao()

        // Bind UI
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSave = findViewById(R.id.btnSave)
        imagePreview = findViewById(R.id.imagePreview)

        // Image selection (Camera OR Gallery)
        btnTakePhoto.setOnClickListener {
            showImageOptions()
        }

        // Save expense
        btnSave.setOnClickListener {
            saveExpense()
        }
    }


    // IMAGE OPTIONS (Camera / Gallery)

    private fun showImageOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (checkCameraPermission()) {
                            openCamera()
                        } else {
                            requestCameraPermission()
                        }
                    }
                    1 -> openGallery()
                }
            }
            .show()
    }


    // PERMISSIONS

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    // CAMERA

    private fun openCamera() {

        val photoFile = createImageFile()

        photoUri = FileProvider.getUriForFile(
            this,
            packageName + ".provider",
            photoFile
        )

        photoUri?.let {
            cameraLauncher.launch(it)
        }
    }

    private fun createImageFile(): File {
        val fileName = "expense_" + System.currentTimeMillis()
        val storageDir = getExternalFilesDir("Pictures")
        return File.createTempFile(fileName, ".jpg", storageDir)
    }


    // GALLERY

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    //
    // SAVE EXPENSE

    private fun saveExpense() {

        val description = etDescription.text.toString()
        val amountText = etAmount.text.toString()
        val date = etDate.text.toString()
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()

        if (description.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDouble()

        val expense = Expense(
            description = description,
            amount = amount,
            date = date,
            startTime = startTime,
            endTime = endTime,
            categoryId = 1,
            imagePath = photoUri?.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            expenseDao.insert(expense)

            runOnUiThread {
                Toast.makeText(this@AddExpenseActivity, "Saved", Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }
    }


    // CLEAR FORM

    private fun clearFields() {
        etDescription.text.clear()
        etAmount.text.clear()
        etDate.text.clear()
        etStartTime.text.clear()
        etEndTime.text.clear()
        imagePreview.setImageDrawable(null)
        photoUri = null
    }
}