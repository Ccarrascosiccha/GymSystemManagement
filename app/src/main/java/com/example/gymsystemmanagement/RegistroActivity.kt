package com.example.gymsystemmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.textfield.TextInputEditText

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
        tietCorreo=findViewById(R.id.tietCorreo)
        tietClave=findViewById(R.id.tietClave)
        rbtMasculino =findViewById(R.id.rbtMasculino)
        rbtFemenino =findViewById(R.id.rbtFemenino)
        rbNinguno =findViewById(R.id.rbNinguno)
        btnVerUsuarios=findViewById(R.id.btnVerUsuarios)
        adapter= ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaUsuarios
        )
        btnVerUsuarios.setOnClickListener {
            val usuario = Usuario(
                codigo = 1,
                dni = tietDni.text.toString().toInt(),
                apellidoPaterno = tietApellidoPaterno.text.toString(),
                apellidoMaterno = tietApellidoMaterno.text.toString(),
                nombres = tietNombres.text.toString(),
                celular = tietCelular.text.toString().toInt(),
                sexo = 'M',
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