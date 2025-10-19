package com.example.gymsystemmanagement.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.HistorialAdapter
import com.example.gymsystemmanagement.entity.Usuario

class HistorialActivity: AppCompatActivity() {
    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }

        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.setHasFixedSize(true)
        rvHistorial.layoutManager = LinearLayoutManager(this)
        val usuarios = mutableListOf<Usuario>()
        (intent.getSerializableExtra("usuario") as? Usuario)?.let { usuarios.add(it) }

        if (usuarios.isEmpty()) {
            usuarios += listOf(
                Usuario(1, 123456789, "Carrasco", "Siccha", "Carlos Daniel", "999999999", "M", "prueba@gmail.com", "123a"),
                Usuario(2, 123456789, "Carrasco", "Siccha", "Carlos Daniel", "999999999", "M", "prueba@gmail.com", "123a")
            )
        }
        historialAdapter = HistorialAdapter(usuarios)
        rvHistorial.adapter = historialAdapter
    }
}
