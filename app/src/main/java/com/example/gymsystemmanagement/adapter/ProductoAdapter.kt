package com.example.gymsystemmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.Producto

class ProductoAdapter(private val items: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val p = items[position]

        holder.tvTitle.text = p.title
        holder.tvPrice.text = "S/ %.2f".format(p.price)
        holder.tvCategory.text = p.category
        Glide.with(holder.itemView.context)
            .load(p.image)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.ivImage)
        holder.rbRating.rating = p.rating?.rate?.toFloat()!!
    }

    override fun getItemCount() = items.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivFotoProducto)
        val tvTitle: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategoriaProducto)
        val  rbRating : RatingBar = itemView.findViewById(R.id.rtbProducts)
    }
}
