package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import com.example.gymsystemmanagement.entity.Membresia
import com.example.gymsystemmanagement.entity.MembresiaCompleta
import com.example.gymsystemmanagement.entity.Usuario

class MembresiaDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)


    /**
     * Insertar nueva membresía
     */
    fun insertarMembresia(membresia: Membresia): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("idUsuario", membresia.idUsuario)
            put("idPlan", membresia.idPlan)
            put("fechaInicio", membresia.fechaInicio)
            put("fechaFin", membresia.fechaFin)
            put("estado", membresia.estado)
        }
        return db.insert("Membresia", null, values)
    }
    /**
     * Obtener todas las membresías registradas
     */fun obtenerMembresiasActivasCompletas(): List<MembresiaCompleta> {
        val lista = mutableListOf<MembresiaCompleta>()
        val db = dbHelper.readableDatabase

        // Ajusta los nombres de tablas y columnas si son diferentes
        val query = """
        SELECT 
            m.*, 
            u.id AS idUsuario, u.nombres, u.apellidoPaterno, u.apellidoMaterno, 
            u.dni, u.celular, u.correo,
            p.nombre, p.precio
        FROM Membresia m
        INNER JOIN Usuario u ON m.idUsuario = u.id
        INNER JOIN PlanMembresia p ON m.id = p.id
        WHERE m.estado = 'Activa'
        ORDER BY m.fechaInicio DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val membresia = Membresia.fromCursor(cursor)

                val usuario = Usuario(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                    apellidoPaterno = cursor.getString(cursor.getColumnIndexOrThrow("apellidoPaterno")),
                    apellidoMaterno = cursor.getString(cursor.getColumnIndexOrThrow("apellidoMaterno")),
                    dni = cursor.getInt(cursor.getColumnIndexOrThrow("dni")),
                    celular = cursor.getString(cursor.getColumnIndexOrThrow("celular")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
                )

                val nombrePlan = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val precioPlan = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

                lista.add(MembresiaCompleta(membresia, usuario, nombrePlan, precioPlan))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    /**
     * Actualizar estado de una membresía
     */
    fun actualizarEstado(idMembresia: Int, nuevoEstado: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        return db.update(
            "Membresia",
            values,
            "id = ?",
            arrayOf(idMembresia.toString())
        )
    }

    /**
     * Obtener membresía activa de un usuario
     */
    fun obtenerMembresiaActivaDeUsuario(idUsuario: Int): Membresia? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Membresia",
            null,
            "idUsuario = ? AND estado = ?",
            arrayOf(idUsuario.toString(), "Activa"),
            null,
            null,
            "fechaInicio DESC",
            "1"
        )

        var membresia: Membresia? = null
        if (cursor.moveToFirst()) {
            membresia = Membresia.fromCursor(cursor)
        }
        cursor.close()
        return membresia
    }

    /**
     * Obtener todas las membresías de un usuario
     */
    fun obtenerMembresiasDeUsuario(idUsuario: Int): List<Membresia> {
        val lista = mutableListOf<Membresia>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Membresia",
            null,
            "idUsuario = ?",
            arrayOf(idUsuario.toString()),
            null,
            null,
            "fechaInicio DESC"
        )

        while (cursor.moveToNext()) {
            lista.add(Membresia.fromCursor(cursor))
        }
        cursor.close()
        return lista
    }
}