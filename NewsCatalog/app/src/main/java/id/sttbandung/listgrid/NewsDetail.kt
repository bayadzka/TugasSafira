package id.sttbandung.listgrid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newscatalog.R
import com.google.firebase.firestore.FirebaseFirestore

class NewsDetail : AppCompatActivity() {
    private lateinit var newsTitle: TextView
    private lateinit var newsSubtitle: TextView
    private lateinit var newsImage: ImageView
    private lateinit var edit: Button
    private lateinit var hapus: Button
    private lateinit var db: FirebaseFirestore
    private var id: String? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Initialize UI components
        newsTitle = findViewById(R.id.newsTitle)
        newsSubtitle = findViewById(R.id.newsSubtitle)
        newsImage = findViewById(R.id.newsImage)
        edit = findViewById(R.id.editButton)
        hapus = findViewById(R.id.deleteButton)
        db = FirebaseFirestore.getInstance()

        // Get data from Intent
        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        subtitle = intent.getStringExtra("desc")
        imageUrl = intent.getStringExtra("imageUrl")

        // Set data to UI components
        newsTitle.text = title
        newsSubtitle.text = subtitle
        Glide.with(this).load(imageUrl).into(newsImage)

        // Set click listener for edit button
        edit.setOnClickListener {
            val editIntent = Intent(this@NewsDetail, NewsAdd::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("desc", subtitle)
                putExtra("imageUrl", imageUrl)
            }
            startActivity(editIntent)
        }

        // Set click listener for delete button
        hapus.setOnClickListener {
            id?.let { documentId ->
                db.collection("news").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this@NewsDetail, "News deleted successfully", Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(this@NewsDetail, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(mainIntent)
                        finish() // Close the activity and go back to the previous screen
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@NewsDetail, "Error deleting news: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.w("NewsDetail", "Error deleting document", e)
                    }
            }
        }
    }
}
