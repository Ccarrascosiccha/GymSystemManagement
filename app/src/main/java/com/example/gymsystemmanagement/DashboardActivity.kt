package com.example.gymsystemmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.adapter.UsuarioAdapter
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var rvMiembros: RecyclerView
    private lateinit var fabAnhiadir: FloatingActionButton
    private lateinit var usuarioAdapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvMiembros = findViewById(R.id.rvMiembros)
        fabAnhiadir = findViewById(R.id.fabAnhiadir)

        rvMiembros.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        cargarMiembrosDesdeSQLite()

        fabAnhiadir.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun cargarMiembrosDesdeSQLite() {
        lifecycleScope.launch {
            val usuarios = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(this@DashboardActivity)
                val db = dbHelper.readableDatabase
                val lista = mutableListOf<Usuario>()

                val cursor = db.rawQuery(
                    """
                    SELECT id, dni, apellidoPaterno, apellidoMaterno, nombres, celular, sexo, correo, clave
                    FROM Usuario
                    WHERE estado='Activo'
                    ORDER BY datetime(fechaRegistro) DESC
                    LIMIT 10
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
                                clave = cursor.getString(8)
                            )
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
                db.close()
                lista
            }

            // Crear e asignar el adapter con la lista de usuarios
            usuarioAdapter = UsuarioAdapter(usuarios)
            rvMiembros.adapter = usuarioAdapter
            usuarioAdapter.notifyDataSetChanged()
        }
    }
}
