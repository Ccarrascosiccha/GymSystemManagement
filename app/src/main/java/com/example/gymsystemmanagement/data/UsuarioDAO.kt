package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import com.example.gymsystemmanagement.entity.Usuario

class UsuarioDAO(context : Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun insertar (usuario : Usuario) : Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("dni", usuario.dni)
            put("apellidoPaterno", usuario.apellidoPaterno)
            put("apellidoMaterno", usuario.apellidoMaterno)
            put("nombres", usuario.nombres)
            put("celular", usuario.celular)
            put("sexo", usuario.sexo.toString())
            put("correo", usuario.correo)
            put("direccion", usuario.direccion)
            put("fechaRegistro", usuario.fechaRegistro)
            put("rol", usuario.rol)
            put("clave", usuario.clave)
            put("estado", usuario.estado)
        }
        return  db.insert("Usuario", null, values)
    }
    fun listarTodos (id : Int) : List<Usuario> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Usuario>()
        val cursor = db.rawQuery("SELECT * FROM Usuario WHERE Id = ?", arrayOf(id.toString()))
        while (cursor.moveToNext()){
            lista.add(
                Usuario(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    dni = cursor.getInt(cursor.getColumnIndexOrThrow("dni")) ,
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
        }
        cursor.close()
        db.close()
        return lista

    }
}