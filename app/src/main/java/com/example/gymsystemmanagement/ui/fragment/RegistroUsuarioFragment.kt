package com.example.gymsystemmanagement.ui.fragment

import android.content.ContentValues
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario
import com.example.gymsystemmanagement.repository.UsuarioRepository
import com.example.gymsystemmanagement.ui.HistorialUsuariosFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroUsuarioFragment : Fragment() {

    private var idUsuario: Int? = null // Si es distinto de null → modo edición

    // --- Vistas ---
    private lateinit var tietDni: TextInputEditText
    private lateinit var tietApellidoPaterno: TextInputEditText
    private lateinit var tietApellidoMaterno: TextInputEditText
    private lateinit var tietNombres: TextInputEditText
    private lateinit var tietCelular: TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tietClaveConfirm: TextInputEditText
    private lateinit var tietDireccion: TextInputEditText

    private lateinit var rbtMasculino: RadioButton
    private lateinit var rbtFemenino: RadioButton
    private lateinit var btnGuardar: Button
    private lateinit var btnVerUsuarios: Button
    private lateinit var actRol: MaterialAutoCompleteTextView
    private lateinit var containerPassword: LinearLayout

    // --- TextInputLayouts para validación ---
    private lateinit var tilDni: TextInputLayout
    private lateinit var tilApellidoPaterno: TextInputLayout
    private lateinit var tilApellidoMaterno: TextInputLayout
    private lateinit var tilNombres: TextInputLayout
    private lateinit var tilCelular: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var tilClaveConfirm: TextInputLayout
    private lateinit var tilDireccion: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registro_usuario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializarVistas(view)
        configurarEventos()
        idUsuario = arguments?.getInt("idUsuario")

        if (idUsuario != null) {
            cargarDatosUsuario(idUsuario!!)
            btnGuardar.text = "Actualizar usuario"
            btnGuardar.setBackgroundColor(resources.getColor(R.color.amarillo2, null))
        }

        // Ajuste para el teclado
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom))
            insets
        }
    }

    private fun inicializarVistas(view: View) {
        tietDni = view.findViewById(R.id.tietDni)
        tietApellidoPaterno = view.findViewById(R.id.tietApellidoPaterno)
        tietApellidoMaterno = view.findViewById(R.id.tietApellidoMaterno)
        tietNombres = view.findViewById(R.id.tietNombres)
        tietCelular = view.findViewById(R.id.tietCelular)
        tietDireccion = view.findViewById(R.id.tietDireccion)
        tietCorreo = view.findViewById(R.id.tietCorreo)
        tietClave = view.findViewById(R.id.tietClave)
        tietClaveConfirm = view.findViewById(R.id.tietClaveConfirm)
        rbtMasculino = view.findViewById(R.id.rbtMasculino)
        rbtFemenino = view.findViewById(R.id.rbtFemenino)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnVerUsuarios = view.findViewById(R.id.btnVerUsuarios)
        actRol = view.findViewById(R.id.actRol)
        containerPassword = view.findViewById(R.id.containerPassword)

        tilDni = view.findViewById(R.id.tilDni)
        tilApellidoPaterno = view.findViewById(R.id.tilApellidoPaterno)
        tilApellidoMaterno = view.findViewById(R.id.tilApellidoMaterno)
        tilNombres = view.findViewById(R.id.tilNombres)
        tilCelular = view.findViewById(R.id.tilCelular)
        tilDireccion = view.findViewById(R.id.tilDireccion)
        tilCorreo = view.findViewById(R.id.tilCorreo)
        tilClave = view.findViewById(R.id.tilClave)
        tilClaveConfirm = view.findViewById(R.id.tilClaveConfirm)

        actRol.setSimpleItems(resources.getStringArray(R.array.roles))
    }

    private fun configurarEventos() {
        actRol.setOnItemClickListener { _, _, _, _ ->
            val rolSel = actRol.text.toString().trim()
            containerPassword.visibility =
                if (rolSel == "Admin" || rolSel == "Empleado") View.VISIBLE else View.GONE
        }

        btnGuardar.setOnClickListener {
            if (idUsuario != null) actualizarUsuario(idUsuario!!)
            else guardarNuevoUsuario()
        }

        btnVerUsuarios.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, HistorialUsuariosFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun cargarDatosUsuario(id: Int) {
        val dbHelper = AppDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT dni, apellidoPaterno, apellidoMaterno, nombres, celular, sexo, correo, direccion, rol, clave FROM Usuario WHERE id = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            tietDni.setText(cursor.getInt(0).toString())
            tietApellidoPaterno.setText(cursor.getString(1))
            tietApellidoMaterno.setText(cursor.getString(2))
            tietNombres.setText(cursor.getString(3))
            tietCelular.setText(cursor.getString(4))
            val sexo = cursor.getString(5)
            if (sexo == "M") rbtMasculino.isChecked = true else rbtFemenino.isChecked = true
            tietCorreo.setText(cursor.getString(6))
            tietDireccion.setText(cursor.getString(7))
            actRol.setText(cursor.getString(8), false)
            tietClave.setText(cursor.getString(9))
        }

        cursor.close()
        db.close()
    }

    private fun validarCampos(): Boolean {
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

        listOf(
            tilDni, tilApellidoPaterno, tilApellidoMaterno, tilNombres,
            tilCelular, tilDireccion, tilCorreo, tilClave, tilClaveConfirm
        ).forEach { it.error = null }

        if (dni.length != 8 || dni.any { !it.isDigit() }) {
            tilDni.error = "DNI debe tener 8 dígitos"; error = true
        }
        if (apePat.isEmpty()) tilApellidoPaterno.error = "Ingrese apellido paterno".also { error = true }
        if (apeMat.isEmpty()) tilApellidoMaterno.error = "Ingrese apellido materno".also { error = true }
        if (nombres.isEmpty()) tilNombres.error = "Ingrese nombres".also { error = true }
        if (celular.length != 9 || celular.any { !it.isDigit() }) {
            tilCelular.error = "Celular debe tener 9 dígitos"; error = true
        }
        if (direccion.isEmpty()) tilDireccion.error = "Ingrese dirección".also { error = true }
        if (correo.isEmpty()) tilCorreo.error = "Ingrese correo".also { error = true }
        if (rol.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione un rol", Toast.LENGTH_SHORT).show()
            error = true
        }
        if (containerPassword.visibility == View.VISIBLE) {
            if (clave.isEmpty()) {
                tilClave.error = "Ingrese una contraseña"; error = true
            } else if (clave.length < 6) {
                tilClave.error = "Debe tener al menos 6 caracteres"; error = true
            }
            if (claveConfirm != clave) {
                tilClaveConfirm.error = "Las contraseñas no coinciden"; error = true
            }
        }
        return !error
    }

    private fun limpiarFormulario() {
        tietDni.text?.clear()
        tietApellidoPaterno.text?.clear()
        tietApellidoMaterno.text?.clear()
        tietNombres.text?.clear()
        tietCelular.text?.clear()
        tietDireccion.text?.clear()
        tietCorreo.text?.clear()
        tietClave.text?.clear()
        tietClaveConfirm.text?.clear()
        actRol.text?.clear()
        rbtMasculino.isChecked = false
        rbtFemenino.isChecked = false
    }

    private fun guardarNuevoUsuario() {
        if (!validarCampos()) return
        try {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date())
            val usuario = Usuario(
                dni = tietDni.text.toString().toInt(),
                apellidoPaterno = tietApellidoPaterno.text.toString(),
                apellidoMaterno = tietApellidoMaterno.text.toString(),
                nombres = tietNombres.text.toString(),
                celular = tietCelular.text.toString(),
                sexo = if (rbtMasculino.isChecked) "M" else "F",
                correo = tietCorreo.text.toString(),
                direccion = tietDireccion.text.toString(),
                fechaRegistro = fechaActual,
                rol = actRol.text.toString(),
                clave = tietClave.text.toString(),
                estado = "Activo",
                id = 0
            )
            val repo = UsuarioRepository(requireContext())
            val id = repo.insertar(usuario)

            if (id == -1L) {
                mostrarSnackbar("No se pudo guardar el usuario", R.color.red)
                return
            }

            mostrarSnackbar("Usuario guardado correctamente", R.color.verde1, "Ver") {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HistorialUsuariosFragment())
                    .addToBackStack(null)
                    .commit()
            }

            limpiarFormulario()
        } catch (e: SQLiteConstraintException) {
            mostrarSnackbar("DNI o correo ya registrado", R.color.red)
        } catch (e: Exception) {
            mostrarSnackbar("Error: ${e.message}", R.color.red)
        }
    }

    private fun actualizarUsuario(id: Int) {
        if (!validarCampos()) return
        val usuario = Usuario(
            id = id,
            dni = tietDni.text.toString().toInt(),
            apellidoPaterno = tietApellidoPaterno.text.toString(),
            apellidoMaterno = tietApellidoMaterno.text.toString(),
            nombres = tietNombres.text.toString(),
            celular = tietCelular.text.toString(),
            sexo = if (rbtMasculino.isChecked) "M" else "F",
            correo = tietCorreo.text.toString(),
            direccion = tietDireccion.text.toString(),
            fechaRegistro = "",
            rol = actRol.text.toString(),
            clave = tietClave.text.toString(),
            estado = "Activo"
        )

        try {
            val repo = UsuarioRepository(requireContext())
            val filas = repo.actualizar(usuario)

            if (filas > 0) {
                mostrarSnackbar("Usuario actualizado correctamente", R.color.verde1, "Ver lista") {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HistorialUsuariosFragment())
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                mostrarSnackbar("No se realizaron cambios", R.color.amarillo2)
            }

        } catch (e: SQLiteConstraintException) {
            val mensaje = when {
                e.message?.contains("DNI") == true -> "Ya existe otro usuario con el mismo DNI"
                e.message?.contains("Correo") == true -> "Ya existe otro usuario con el mismo correo"
                else -> "Error: datos duplicados"
            }
            mostrarSnackbar(mensaje, R.color.red)

        } catch (e: Exception) {
            mostrarSnackbar("Error inesperado: ${e.message}", R.color.red)
        }
    }



    private fun mostrarSnackbar(mensaje: String, color: Int, accion: String? = null, onClick: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(requireView(), mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(color, null))
            .setTextColor(resources.getColor(R.color.white, null))

        if (accion != null && onClick != null) {
            snackbar.setAction(accion) { onClick() }
        }

        snackbar.show()
    }
}
