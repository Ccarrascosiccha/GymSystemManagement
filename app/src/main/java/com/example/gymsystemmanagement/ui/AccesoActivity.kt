package com.example.gymsystemmanagement.ui

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.example.gymsystemmanagement.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccesoActivity : AppCompatActivity() {
    var tvRegistro : TextView?=null
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietPass : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilPass : TextInputLayout
    private lateinit var btnAcceso: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietPass = findViewById(R.id.tietPass)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilPass = findViewById(R.id.tilPass)
        btnAcceso = findViewById(R.id.btnAcceso)


        btnAcceso.setOnClickListener {
        validarCampos()
        }

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
    }
    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietPass.text.toString().trim()
        var error = false

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            error = true
        } else {
            tilCorreo.error = null
        }

        if (clave.isEmpty()) {
            tilPass.error = "Ingrese una contraseña"
            error = true
        } else {
            tilPass.error = null
        }

        if (error) return

        CoroutineScope(Dispatchers.Main).launch {
            val usuEncontrado = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(this@AccesoActivity)
                val db = dbHelper.readableDatabase
                var usuario: Usuario? = null
                var cursor: Cursor? = null

                try {
                    cursor = db.rawQuery(
                        "SELECT * FROM Usuario WHERE correo = ? AND clave = ?",
                        arrayOf(correo, clave)
                    )
                    if (cursor.moveToFirst()) {
                        usuario = Usuario(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            dni = cursor.getInt(cursor.getColumnIndexOrThrow("dni")),
                            apellidoPaterno = cursor.getString(cursor.getColumnIndexOrThrow("apellidoPaterno")),
                            apellidoMaterno = cursor.getString(cursor.getColumnIndexOrThrow("apellidoMaterno")),
                            nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                            celular = cursor.getString(cursor.getColumnIndexOrThrow("celular")),
                            sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo")),
                            correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                            direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                            fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fechaRegistro")),
                            rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
                            clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                            estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
                        )
                    }
                } catch (e: Exception) {
                    Log.e("AccesoActivity", "Error al validar usuario: ${e.message}")
                } finally {
                    cursor?.close()
                    db.close()
                }

                usuario
            }

            if (usuEncontrado != null) {
                // Mostrar nombre y apellido materno
                val nombreCompleto = "${usuEncontrado.nombres} ${usuEncontrado.apellidoMaterno}"
                Toast.makeText(
                    this@AccesoActivity,
                    "Bienvenido, $nombreCompleto",
                    Toast.LENGTH_LONG
                ).show()

                startActivity(Intent(this@AccesoActivity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this@AccesoActivity,
                    "Usuario o contraseña incorrecta",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}