package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.gymsystemmanagement.entity.Transaccion
import java.text.SimpleDateFormat
import java.util.*

class TransaccionDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun insertarTransaccion(transaccion: Transaccion): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("idUsuario", transaccion.idUsuario)
            put("idMembresia", transaccion.idMembresia)
            put("monto", transaccion.monto)
            put("tipo", transaccion.tipo)
            put("descripcion", transaccion.descripcion)
            put("fecha", transaccion.fecha)
        }
        return db.insert("Transaccion", null, valores)
    }

    fun obtenerTransaccionPorId(id: Int): Transaccion? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Transaccion",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var transaccion: Transaccion? = null
        if (cursor.moveToFirst()) {
            transaccion = cursorATransaccion(cursor)
        }
        cursor.close()
        return transaccion
    }

    fun obtenerTodasTransacciones(): List<Transaccion> {
        val db = dbHelper.readableDatabase
        val transacciones = mutableListOf<Transaccion>()

        val cursor = db.query(
            "Transaccion",
            null,
            null, null, null, null,
            "fecha DESC"
        )

        while (cursor.moveToNext()) {
            transacciones.add(cursorATransaccion(cursor))
        }
        cursor.close()
        return transacciones
    }

    fun obtenerTransaccionesDeUsuario(idUsuario: Int): List<Transaccion> {
        val db = dbHelper.readableDatabase
        val transacciones = mutableListOf<Transaccion>()

        val cursor = db.query(
            "Transaccion",
            null,
            "idUsuario = ?",
            arrayOf(idUsuario.toString()),
            null, null, "fecha DESC"
        )

        while (cursor.moveToNext()) {
            transacciones.add(cursorATransaccion(cursor))
        }
        cursor.close()
        return transacciones
    }

    fun obtenerTransaccionesDeMembresia(idMembresia: Int): List<Transaccion> {
        val db = dbHelper.readableDatabase
        val transacciones = mutableListOf<Transaccion>()

        val cursor = db.query(
            "Transaccion",
            null,
            "idMembresia = ?",
            arrayOf(idMembresia.toString()),
            null, null, "fecha DESC"
        )

        while (cursor.moveToNext()) {
            transacciones.add(cursorATransaccion(cursor))
        }
        cursor.close()
        return transacciones
    }

    fun obtenerTransaccionesPorTipo(tipo: String): List<Transaccion> {
        val db = dbHelper.readableDatabase
        val transacciones = mutableListOf<Transaccion>()

        val cursor = db.query(
            "Transaccion",
            null,
            "tipo = ?",
            arrayOf(tipo),
            null, null, "fecha DESC"
        )

        while (cursor.moveToNext()) {
            transacciones.add(cursorATransaccion(cursor))
        }
        cursor.close()
        return transacciones
    }

    fun obtenerTransaccionesPorFecha(fechaInicio: String, fechaFin: String): List<Transaccion> {
        val db = dbHelper.readableDatabase
        val transacciones = mutableListOf<Transaccion>()

        val cursor = db.query(
            "Transaccion",
            null,
            "fecha BETWEEN ? AND ?",
            arrayOf(fechaInicio, fechaFin),
            null, null, "fecha DESC"
        )

        while (cursor.moveToNext()) {
            transacciones.add(cursorATransaccion(cursor))
        }
        cursor.close()
        return transacciones
    }

    fun calcularTotalPorTipo(tipo: String): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(monto) FROM Transaccion WHERE tipo = ?",
            arrayOf(tipo)
        )

        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun eliminarTransaccion(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("Transaccion", "id = ?", arrayOf(id.toString()))
    }

    private fun cursorATransaccion(cursor: Cursor): Transaccion {
        return Transaccion(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
            idMembresia = cursor.getInt(cursor.getColumnIndexOrThrow("idMembresia")),
            monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
            tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
            fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
        )
    }
}