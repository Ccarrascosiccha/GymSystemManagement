package com.example.gymsystemmanagement.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.entity.Usuario

class UsuarioRepository(private val context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    // Insertar un nuevo usuario
    fun insertar(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("dni", usuario.dni)
            put("apellidoPaterno", usuario.apellidoPaterno)
            put("apellidoMaterno", usuario.apellidoMaterno)
            put("nombres", usuario.nombres)
            put("celular", usuario.celular)
            put("sexo", usuario.sexo)
            put("correo", usuario.correo)
            put("direccion", usuario.direccion)
            put("rol", usuario.rol)
            put("clave", usuario.clave)
            put("estado", usuario.estado)
        }
        val id = db.insert("Usuario", null, values)
        db.close()
        return id
    }

    // Listar usuarios activos
    fun listarActivos(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Usuario>()
        val cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE estado='Activo' ORDER BY datetime(fechaRegistro) DESC",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Usuario(
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
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    fun actualizar(usuario: Usuario): Int {
        val dbHelper = AppDatabaseHelper(context)
        val db = dbHelper.writableDatabase

        // Validar duplicado de DNI
        val cursorDni = db.rawQuery(
            "SELECT id FROM Usuario WHERE dni = ? AND id != ?",
            arrayOf(usuario.dni.toString(), usuario.id.toString())
        )
        if (cursorDni.moveToFirst()) {
            cursorDni.close()
            db.close()
            throw SQLiteConstraintException("DNI duplicado")
        }
        cursorDni.close()

        // Validar duplicado de correo
        val cursorCorreo = db.rawQuery(
            "SELECT id FROM Usuario WHERE correo = ? AND id != ?",
            arrayOf(usuario.correo, usuario.id.toString())
        )
        if (cursorCorreo.moveToFirst()) {
            cursorCorreo.close()
            db.close()
            throw SQLiteConstraintException("Correo duplicado")
        }
        cursorCorreo.close()

        val valores = ContentValues().apply {
            put("dni", usuario.dni)
            put("apellidoPaterno", usuario.apellidoPaterno)
            put("apellidoMaterno", usuario.apellidoMaterno)
            put("nombres", usuario.nombres)
            put("celular", usuario.celular)
            put("sexo", usuario.sexo)
            put("correo", usuario.correo)
            put("direccion", usuario.direccion)
            put("rol", usuario.rol)
            put("clave", usuario.clave)
        }

        val filas = db.update("Usuario", valores, "id = ?", arrayOf(usuario.id.toString()))
        db.close()
        return filas
    }


}
