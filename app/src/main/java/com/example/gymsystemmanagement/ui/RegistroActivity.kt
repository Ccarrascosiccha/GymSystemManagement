package com.example.gymsystemmanagement.ui

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
class RegistroActivity: AppCompatActivity() {
    private lateinit var tietDni : TextInputEditText
    private lateinit var tietApellidoPaterno : TextInputEditText
    private lateinit var tietApellidoMaterno : TextInputEditText
    private lateinit var tietNombres : TextInputEditText
    private lateinit var tietCelular : TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var rbtMasculino: RadioButton
    private lateinit var rbtFemenino: RadioButton
    private lateinit var rbNinguno: RadioButton
    private lateinit var  btnVerUsuarios: Button
    private lateinit var btnGuardar : Button
    private lateinit var tietClaveConfirm : TextInputEditText
    private lateinit var tietDireccion : TextInputEditText
    private lateinit var containerPassword : LinearLayout
    private lateinit var tilDni : TextInputLayout
    private lateinit var tilApellidoPaterno : TextInputLayout
    private lateinit var tilApellidoMaterno : TextInputLayout
    private lateinit var tilNombres : TextInputLayout
    private lateinit var tilCelular : TextInputLayout
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilClave : TextInputLayout
    private lateinit var tilClaveConfirm : TextInputLayout
    private lateinit var tilDireccion : TextInputLayout
    private lateinit var actRol: MaterialAutoCompleteTextView
    private val listaUsuarios = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        tietDni=findViewById(R.id.tietDni)
        tietApellidoPaterno=findViewById(R.id.tietApellidoPaterno)
        tietApellidoMaterno =findViewById(R.id.tietApellidoMaterno)
        tietNombres =findViewById(R.id.tietNombres)
        tietCelular=findViewById(R.id.tietCelular)
        tietDireccion = findViewById(R.id.tietDireccion)
        tietCorreo=findViewById(R.id.tietCorreo)
        tietClave=findViewById(R.id.tietClave)
        rbtMasculino =findViewById(R.id.rbtMasculino)
        rbtFemenino =findViewById(R.id.rbtFemenino)
        tietClaveConfirm=findViewById(R.id.tietClaveConfirm)
        tilDni             = findViewById(R.id.tilDni)
        tilApellidoPaterno = findViewById(R.id.tilApellidoPaterno)
        tilApellidoMaterno = findViewById(R.id.tilApellidoMaterno)
        tilNombres         = findViewById(R.id.tilNombres)
        tilCelular         = findViewById(R.id.tilCelular)
        tilDireccion       = findViewById(R.id.tilDireccion)
        tilCorreo          = findViewById(R.id.tilCorreo)
        tilClave           = findViewById(R.id.tilClave)
        tilClaveConfirm    = findViewById(R.id.tilClaveConfirm)

        btnGuardar=findViewById(R.id.btnGuardar)
        btnVerUsuarios=findViewById(R.id.btnVerUsuarios)
        actRol=findViewById(R.id.actRol)
        actRol.setSimpleItems(R.array.roles)
        containerPassword = findViewById(R.id.containerPassword)
        actRol.setOnItemClickListener { _, _, _, _ ->
            val rolSel = actRol.text.toString().trim()
            containerPassword.visibility =
                if (rolSel == "Admin" || rolSel == "Empleado") View.VISIBLE else View.GONE
        }
        adapter= ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaUsuarios
        )
        //validacion de campos
         fun validarCampos(): Boolean {
            var error = false
            val dni = tietDni.text.toString().trim()
            val apePat = tietApellidoPaterno.text.toString().trim()
            val apeMat = tietApellidoMaterno.text.toString().trim()
            val nombres = tietNombres.text.toString().trim()
            val celular = tietCelular.text.toString().trim()
            val direccion = tietDireccion.text.toString().trim()
            val correo = tietCorreo.text.toString().trim()
            val rol = actRol.text.toString().trim()
            val clave = tietClave.text.toString().trim()
            val claveConfirm = tietClaveConfirm.text.toString().trim()

            tilDni.error = null
            tilApellidoPaterno.error = null
            tilApellidoMaterno.error = null
            tilNombres.error = null
            tilCelular.error = null
            tilDireccion.error = null
            tilCorreo.error = null
            tilClave.error = null
            tilClaveConfirm.error = null

            if (dni.length != 8 || dni.any { !it.isDigit() }) {
                tilDni.error = "DNI debe tener 8 dígitos"
                error = true
            }
            if (apePat.isEmpty()) {
                tilApellidoPaterno.error = "Ingrese apellido paterno"
                error = true
            }
            if (apeMat.isEmpty()) {
                tilApellidoMaterno.error = "Ingrese apellido materno"
                error = true
            }
            if (nombres.isEmpty()) {
                tilNombres.error = "Ingrese nombres"
                error = true
            }
            if (celular.length != 9 || celular.any { !it.isDigit() }) {
                tilCelular.error = "Celular debe tener 9 dígitos"
                error = true
            }
            if (direccion.isEmpty()) {
                tilDireccion.error = "Ingrese dirección"
                error = true
            }
            if (correo.isEmpty()) {
                tilCorreo.error = "Ingrese correo"
                error = true
            }
            if (rol.isEmpty()) {
                Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show()
                error = true
            }
            if (containerPassword.visibility == View.VISIBLE) {
                if (clave.isEmpty()) {
                    tilClave.error = "Ingrese una contraseña"
                    error = true
                } else if (clave.length < 6) {
                    tilClave.error = "Debe tener al menos 6 caracteres"
                    error = true
                }
                if (claveConfirm != clave) {
                    tilClaveConfirm.error = "Las contraseñas no coinciden"
                    error = true
                }
            }
            return !error
        }
        fun limpiarFormulario() {
            tietDni.text?.clear()
            tietApellidoPaterno.text?.clear()
            tietApellidoMaterno.text?.clear()
            tietNombres.text?.clear()
            tietCelular.text?.clear()
            tietDireccion.text?.clear()
            tietCorreo.text?.clear()
            tietClave.text?.clear()
            actRol.text?.clear()
            rbtMasculino.isChecked = false
            rbtFemenino.isChecked = false
            tietDni.requestFocus()
        }
        btnGuardar.setOnClickListener {
                if (!validarCampos()) return@setOnClickListener
                val dbHelper = AppDatabaseHelper(this)
                val db = dbHelper.writableDatabase

                try {
                    db.beginTransaction()
                    val correo = "${tietCorreo.text.toString().trim()}@cibertec.edu.pe"
                    val values = ContentValues().apply {
                        put("dni", tietDni.text.toString().toIntOrNull() ?: run {
                            Toast.makeText(this@RegistroActivity, "DNI inválido", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        })
                        put("apellidoPaterno", tietApellidoPaterno.text.toString())
                        put("apellidoMaterno", tietApellidoMaterno.text.toString())
                        put("nombres",          tietNombres.text.toString())
                        put("celular",          tietCelular.text.toString())
                        put("direccion",        tietDireccion.text.toString())
                        put("correo",           correo)
                        put("sexo", if (rbtMasculino.isChecked) "M" else if (rbtFemenino.isChecked) "F" else "N")
                        put("rol",    actRol.text.toString())
                        put("estado", "Activo")
                        put("clave",  tietClave.text.toString())
                    }
                    val id = db.insert("Usuario", null, values)
                    if (id == -1L) {
                        Toast.makeText(this, "No se pudo guardar el usuario", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    db.setTransactionSuccessful()
                    Toast.makeText(this, "Usuario guardado (ID = $id)", Toast.LENGTH_SHORT).show()
                    limpiarFormulario()
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(this, "DNI o correo ya registrado", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    db.endTransaction()
                    db.close()
                }
        }
        btnVerUsuarios.setOnClickListener {
            val usuario = Usuario(
                id = 1,
                dni = tietDni.text.toString().toInt(),
                apellidoPaterno = tietApellidoPaterno.text.toString(),
                apellidoMaterno = tietApellidoMaterno.text.toString(),
                nombres = tietNombres.text.toString(),
                celular = tietCelular.text.toString(),
                sexo = "M",
                correo = tietCorreo.text.toString(),
                clave = tietClave.text.toString()
            )
            val intent = Intent(this, HistorialActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        btnVerUsuarios.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
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
}