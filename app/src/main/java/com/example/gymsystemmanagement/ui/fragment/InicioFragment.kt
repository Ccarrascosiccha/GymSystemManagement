package com.example.gymsystemmanagement.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.adapter.UsuarioAdapter
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioFragment : Fragment(R.layout.fragment_inicio){
    private lateinit var rvMiembros: RecyclerView
    private lateinit var fabAnhiadir: FloatingActionButton
    private lateinit var usuarioAdapter: UsuarioAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias de vistas
        rvMiembros = view.findViewById(R.id.rvMiembros)
        fabAnhiadir = view.findViewById(R.id.fabAnhiadir)

        // Configuración del RecyclerView
        rvMiembros.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Cargar datos desde SQLite
        cargarMiembrosDesdeSQLite()

        // Acción del botón flotante
        fabAnhiadir.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // entrada del nuevo fragment
                    R.anim.slide_out_left,  // salida del actual
                    R.anim.slide_in_left,   // cuando vuelves atrás
                    R.anim.slide_out_right  // al cerrar el fragment
                )
                .replace(R.id.fragmentContainer, RegistroUsuarioFragment())
                .addToBackStack(null)
                .commit()

        }

    }

    private fun cargarMiembrosDesdeSQLite() {
        viewLifecycleOwner.lifecycleScope.launch {
            val usuarios = withContext(Dispatchers.IO) {
                val dbHelper = AppDatabaseHelper(requireContext())
                val db = dbHelper.readableDatabase
                val lista = mutableListOf<Usuario>()

                val cursor = db.rawQuery(
                    """
                    SELECT id, dni, apellidoPaterno, apellidoMaterno, nombres, celular, sexo, correo, direccion, fechaRegistro, rol, clave, estado
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

            usuarioAdapter = UsuarioAdapter(usuarios)
            rvMiembros.adapter = usuarioAdapter
            usuarioAdapter.notifyDataSetChanged()
        }
    }
}