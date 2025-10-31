package com.example.gymsystemmanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.adapter.HistorialAdapter
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.example.gymsystemmanagement.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.*
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.example.gymsystemmanagement.ui.fragment.RegistroUsuarioFragment
import com.example.gymsystemmanagement.R

class HistorialUsuariosFragment : Fragment() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvHistorial = view.findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        GlobalScope.launch(Dispatchers.Main) {
            val repo = UsuarioRepository(requireContext())
            val usuarios = withContext(Dispatchers.IO) { repo.listarActivos() }

            historialAdapter = HistorialAdapter(
                usuarios,
                onOpciones = { usuario -> mostrarDialogOpciones(usuario) }
            )

            rvHistorial.adapter = historialAdapter
        }
    }


    private fun mostrarDialogOpciones(usuario: Usuario) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_opciones, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
        val btnEditar = dialogView.findViewById<MaterialButton>(R.id.btnEditar)
        val btnEliminar = dialogView.findViewById<MaterialButton>(R.id.btnEliminar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelar)

        tvTitulo.text = "Opciones para ${usuario.nombres}"

        btnEditar.setOnClickListener {
            dialog.dismiss()
            editarUsuario(usuario)
        }

        btnEliminar.setOnClickListener {
            dialog.dismiss()
            confirmarEliminacion(usuario)
        }

        btnCancelar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun editarUsuario(usuario: Usuario) {
        val fragment = RegistroUsuarioFragment().apply {
            arguments = Bundle().apply {
                putInt("idUsuario", usuario.id)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun confirmarEliminacion(usuario: Usuario) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar a ${usuario.nombres}?")
            .setPositiveButton("Sí, eliminar") { _, _ -> eliminarUsuario(usuario) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarUsuario(usuario: Usuario) {
        val dbHelper = AppDatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase
        val filas = db.delete("Usuario", "id=?", arrayOf(usuario.id.toString()))
        db.close()

        if (filas > 0) {
            Toast.makeText(requireContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
            cargarUsuarios()
        } else {
            Toast.makeText(requireContext(), "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
