package com.example.gymsystemmanagement.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AccesoActivity : AppCompatActivity() {
    private lateinit var tvRegistro: TextView
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var btnAcceso: Button
    private var codigoEnviado = false
    private var verificationId: String? = null
    private lateinit var ivGoogle: ImageView
    private lateinit var googleClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // Nuevo: ActivityResultLauncher para Google Sign In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(Exception::class.java)
                account?.let {
                    val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                            navegarAInicio()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                Log.e("AccesoActivity", "Google sign in failed", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        inicializarVistas()
        configurarFirebase()
        configurarListeners()
        configurarWindowInsets()
    }

    private fun inicializarVistas() {
        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)
        btnAcceso = findViewById(R.id.btnAcceso)
        ivGoogle = findViewById(R.id.ivGoogle)
    }

    private fun configurarFirebase() {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)
    }

    private fun configurarListeners() {
        // TextWatcher para el campo de teléfono
        tietCorreo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Resetear el error cuando el usuario empieza a escribir
                tilCorreo.error = null

                if (!codigoEnviado && s?.length == 9) {
                    enviarCodigoVerificacion(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // TextWatcher para el código de verificación
        tietClave.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tilClave.error = null

                if (s?.length == 6 && verificationId != null) {
                    verificarCodigo(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnAcceso.setOnClickListener {
            validarCampos()
        }

        ivGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

        tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }


    private fun enviarCodigoVerificacion(telefono: String) {
        codigoEnviado = true
        Toast.makeText(this, "Enviando SMS...", Toast.LENGTH_SHORT).show()

        tilClave.hint = "Código de verificación"
        tilClave.helperText = "Ingresa el código de 6 dígitos"

        val opciones = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+51$telefono")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Verificación automática completada
                    autenticarConCredencial(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    codigoEnviado = false
                    Log.e("AccesoActivity", "Verification failed", e)
                    Toast.makeText(
                        this@AccesoActivity,
                        "Error al enviar SMS: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    tilCorreo.error = "No se pudo enviar el código"
                }

                override fun onCodeSent(
                    verId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verId
                    Toast.makeText(
                        this@AccesoActivity,
                        "Código enviado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    tietClave.requestFocus()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(opciones)
    }

    private fun verificarCodigo(codigo: String) {
        verificationId?.let { verId ->
            val credential = PhoneAuthProvider.getCredential(verId, codigo)
            autenticarConCredencial(credential)
        }
    }

    private fun autenticarConCredencial(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                navegarAInicio()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
                Log.e("AccesoActivity", "Sign in failed", e)
                tietClave.text?.clear()
                tilClave.error = "Código inválido"
            }
    }

    private fun iniciarSesionConGoogle() {
        googleClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleClient.signInIntent)
        }
    }

    private fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error = false

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo o teléfono"
            error = true
        } else {
            tilCorreo.error = null
        }

        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = null
        }

        if (!error) {
            autenticarConBaseDatos(correo, clave)
        }
    }

    private fun autenticarConBaseDatos(correo: String, clave: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val usuarioEncontrado = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(this@AccesoActivity)
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery(
                    "SELECT * FROM usuario WHERE correo = ? AND clave = ?",
                    arrayOf(correo, clave)
                )
                val user = if (cursor.moveToFirst()) {
                    cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                } else null
                cursor.close()
                db.close()
                user
            }

            if (usuarioEncontrado != null) {
                Toast.makeText(this@AccesoActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
                navegarAInicio()
            } else {
                Toast.makeText(
                    this@AccesoActivity,
                    "Usuario o contraseña incorrectos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navegarAInicio() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun configurarWindowInsets() {
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

    override fun onStart() {
        super.onStart()
        // Verificar si hay un usuario activo
        auth.currentUser?.let {
            navegarAInicio()
        }
    }
}