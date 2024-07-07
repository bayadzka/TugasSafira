package id.sttbandung.listgrid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newscatalog.R

data class ItemList (
    var id: String,
    var judul: String,
    var subJudul: String,
    var imageUrl: String
)
