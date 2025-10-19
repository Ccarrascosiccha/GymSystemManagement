package com.example.gymsystemmanagement.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.HistorialAdapter
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        cargarHistorialDesdeSQLite()
    }
    private fun cargarHistorialDesdeSQLite() {
        lifecycleScope.launch {
            val usuarios = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(this@HistorialActivity)
                val db = dbHelper.readableDatabase
                val lista = mutableListOf<Usuario>()
                val cursor = db.rawQuery(
                    """
                        SELECT id, dni, apellidoPaterno, apellidoMaterno, nombres, celular, sexo, correo,direccion,fechaRegistro,rol,clave,estado
                        FROM Usuario
                        WHERE estado='Activo'
                        ORDER BY datetime(fechaRegistro) DESC
                        """, null
                )
                if (cursor.moveToFirst()) {
                    do {
                        lista.add(
                            Usuario(
                                id = cursor.getInt(0),
                                dni = cursor.getInt(1),
                                apellidoPaterno = cursor.getString(2),
                                apellidoMaterno = cursor.getString(3),
                                nombres = cursor.getString(4),
                                celular = cursor.getString(5),
                                sexo = cursor.getString(6),
                                correo = cursor.getString(7),
                                direccion = cursor.getString(8),
                                fechaRegistro = cursor.getString(9),
                                rol = cursor.getString(10),
                                clave = cursor.getString(11),
                                estado = cursor.getString(12)
                            )
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
                db.close()
                lista
            }
            historialAdapter = HistorialAdapter(usuarios)
            rvHistorial.adapter = historialAdapter
            historialAdapter.notifyDataSetChanged()
        }
    }
}
