package com.example.gymsystemmanagement.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ConfiguracionFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsuarioEmail: TextView
    private lateinit var btnCerrarSesion: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        inicializarVistas(view)
        configurarListeners()
        mostrarInformacionUsuario()
    }

    private fun inicializarVistas(view: View) {
        tvUsuarioEmail = view.findViewById(R.id.tvUsuarioEmail)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)
    }

    private fun configurarListeners() {
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun mostrarInformacionUsuario() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            tvUsuarioEmail.text = currentUser.email ?: "Usuario sin correo"
        } else {
            tvUsuarioEmail.text = "Sesión local"
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        // Cerrar sesión de Firebase
        auth.signOut()

        // Cerrar sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(requireActivity(), gso).signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // Navegar a AccesoActivity
            val intent = Intent(requireActivity(), AccesoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}