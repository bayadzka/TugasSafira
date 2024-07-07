package id.sttbandung.listgrid

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newscatalog.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class NewsAdd : AppCompatActivity() {

    private lateinit var title: EditText
    private lateinit var desc: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveNews: Button
    private lateinit var chooseImage: Button
    private var imageUri: Uri? = null
    private lateinit var dbNews: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_add)

        // Initialize Firebase
        dbNews = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize UI components
        title = findViewById(R.id.title)
        desc = findViewById(R.id.desc)
        imageView = findViewById(R.id.imageView)
        saveNews = findViewById(R.id.btnAdd)
        chooseImage = findViewById(R.id.btnChooseImage)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Loading...")
        }

        chooseImage.setOnClickListener {
            openFileChooser()
        }

        saveNews.setOnClickListener {
            val newsTitle = title.text.toString().trim()
            val newsDesc = desc.text.toString().trim()

            if (newsTitle.isEmpty() || newsDesc.isEmpty()) {
                Toast.makeText(this, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressDialog.show()

            if (imageUri != null) {
                uploadImageToStorage(newsTitle, newsDesc)
            } else {
                saveData(newsTitle, newsDesc, "")
            }
        }

        // Check if update
        val updateOption = intent
        id = updateOption.getStringExtra("id")
        val judul = updateOption.getStringExtra("title")
        val deskripsi = updateOption.getStringExtra("desc")
        val image = updateOption.getStringExtra("imageUrl")

        if (judul != null && deskripsi != null && image != null) {
            title.setText(judul)
            desc.setText(deskripsi)
            Glide.with(this).load(image).into(imageView)
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToStorage(newsTitle: String, newsDesc: String) {
        val storageRef = storage.reference.child("news_images/" + System.currentTimeMillis() + ".jpg")
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                storageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    val imageUrl = uri.toString()
                    saveData(newsTitle, newsDesc, imageUrl)
                }
            }
            .addOnFailureListener { e: Exception ->
                progressDialog.dismiss()
                Toast.makeText(this@NewsAdd, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveData(newsTitle: String, newsDesc: String, imageUrl: String) {
        val news = HashMap<String, Any>()
        news["title"] = newsTitle
        news["desc"] = newsDesc
        news["imageUrl"] = imageUrl

        if (id != null) {
            // Your update code here
            dbNews.collection("news").document(id!!)
                .update(news)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity and go back to the previous screen
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "Error updating news: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.w("NewsAdd", "Error updating document", e)
                }
        } else {
            dbNews.collection("news")
                .add(news)
                .addOnSuccessListener { documentReference ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News added successfully", Toast.LENGTH_SHORT).show()
                    title.setText("")
                    desc.setText("")
                    imageView.setImageResource(0) // Clear the ImageView
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "Error adding news: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.w("NewsAdd", "Error adding document", e)
                }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
