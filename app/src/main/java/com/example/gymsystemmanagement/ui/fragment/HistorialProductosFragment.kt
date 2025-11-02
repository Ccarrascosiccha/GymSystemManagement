package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.ProductoAdapter
import com.example.gymsystemmanagement.api.FakeStoreApiClient
import com.example.gymsystemmanagement.entity.Producto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialProductosFragment: Fragment(R.layout.fragment_historial_productos)  {

    private lateinit var rvProductos: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private  val productos = mutableListOf<Producto>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvProductos= view.findViewById(R.id.rvProductos)
        rvProductos.layoutManager = LinearLayoutManager(requireContext())
        productoAdapter=ProductoAdapter(productos)
        rvProductos.adapter=productoAdapter

        cargarProductosDesdeApi()
    }
    private fun cargarProductosDesdeApi() {
        FakeStoreApiClient.apiService.getProducts().enqueue(object :
            Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (response.isSuccessful && response.body() != null) {
                    productos.clear()
                    productos.addAll(response.body()!!)
                    productoAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar", Toast.LENGTH_SHORT).show()
                Log.e("Error al carga de la API", "" + t.message)
            }
        })
    }
}