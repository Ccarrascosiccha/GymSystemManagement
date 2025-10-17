package com.example.gymsystemmanagement

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AccesoActivity : AppCompatActivity() {
    var tvRegistro : TextView?=null
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietPass : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilPass : TextInputLayout
    private lateinit var btnAcceso: Button
    private  lateinit var  ivLlamada: ImageView
    private lateinit var ivLanguage : ImageView
    private lateinit var ivWeb : ImageView
    private val listaUsuarios = mutableListOf(
        Usuario(1, 123456789, "Carrasco", "Siccga", "Carlos Daniel",999999999,'M',"test@gmail.com","123a"),
        Usuario(2, 123456789, "Carrasco", "Siccga", "Carlos Daniel",999999999,'M',"prueba@gmail.com","123a"),
        Usuario(3, 123456789, "Carrasco", "Siccga", "Carlos Daniel",999999999,'M',"prueba@gmail.com","123a"),
        Usuario(4, 123456789, "Carrasco", "Siccga", "Carlos Daniel",999999999,'M',"prueba@gmail.com","123a"),
        Usuario(5, 123456789, "Carrasco", "Siccga", "Carlos Daniel",999999999,'M',"prueba@gmail.com","123a"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietPass = findViewById(R.id.tietPass)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilPass = findViewById(R.id.tilPass)
        ivLlamada = findViewById(R.id.ivLlamada)
        ivLanguage = findViewById(R.id.ivLanguage)
        ivWeb=findViewById(R.id.ivWeb)
        btnAcceso = findViewById(R.id.btnAcceso)


        btnAcceso.setOnClickListener {
        validarCampos()
        }
        ivLlamada.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            }else{
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = "tel:+51974144528".toUri()
                startActivity(intent)
            }
        }
        ivWeb.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData("https://google.com".toUri())
            startActivity(intent)
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
    fun validarCampos(){
        val correo = tietCorreo.text.toString().trim()
        val clave= tietPass.text.toString().trim()
        var error : Boolean = false
        if(correo.isEmpty()){
            tilCorreo.error="Ingrese un correo"
            error=true
        }else{
            tilCorreo.error=""
        }
        if (clave.isEmpty()) {
            tilPass.error="Ingrese una contraseña"
            error = true
        }else{
            tilPass.error=""
        }
        if (!error) {
            var usuario : Usuario ?= null
            for (u in listaUsuarios) {
                if (u.correo == ("$correo@gmail.com") && u.clave == clave) {
                    usuario = u
                }
            }
            if (usuario !=null){
                startActivity(Intent(this, DashboardActivity::class.java))
                Toast.makeText(this,"Bienvenido"+ usuario.nombres, Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this,"Usuario o contraseña incorrectos",Toast.LENGTH_SHORT).show()
            }
        }
    }

}