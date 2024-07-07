package com.example.listgrid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newscatalog.R
import id.sttbandung.listgrid.ItemList

class AdapterList(private val itemLists: kotlin.collections.List<ItemList>) : RecyclerView.Adapter<AdapterList.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: ItemList)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder (itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val judul: TextView = itemView.findViewById(R.id.title)
        val subJudul: TextView = itemView.findViewById(R.id.desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from (parent.context).inflate(R.layout.activity_item_list, parent,  false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemLists[position]
        holder.judul.text = item.judul
        holder.subJudul.text = item.subJudul
        Glide.with(holder.imageView.context).load(item.imageUrl).into(holder.imageView)

        holder.itemView.setOnClickListener{
            listener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemLists.size
    }
}