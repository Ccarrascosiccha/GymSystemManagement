package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.gymsystemmanagement.entity.Membresia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MembresiaDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * Insertar una nueva membresía
     */
    fun insertarMembresia(membresia: Membresia): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("idUsuario", membresia.idUsuario)
            put("idPlan", membresia.idPlan)
            put("fechaInicio", membresia.fechaInicio)
            put("fechaFin", membresia.fechaFin)
            put("estado", membresia.estado)
        }
        return db.insert("Membresia", null, valores)
    }

    /**
     * Obtener una membresía por su ID
     */
    fun obtenerMembresiaPorId(id: Int): Membresia? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Membresia",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var membresia: Membresia? = null
        if (cursor.moveToFirst()) {
            membresia = cursorAMembresia(cursor)
        }
        cursor.close()
        return membresia
    }

    /**
     * Obtener todas las membresías activas
     */
    fun obtenerMembresiasActivas(): List<Membresia> {
        val db = dbHelper.readableDatabase
        val membresias = mutableListOf<Membresia>()

        val cursor = db.query(
            "Membresia",
            null,
            "estado = ?",
            arrayOf("Activa"),
            null, null, "fechaInicio DESC"
        )

        while (cursor.moveToNext()) {
            membresias.add(cursorAMembresia(cursor))
        }
        cursor.close()
        return membresias
    }

    /**
     * Obtener todas las membresías
     */
    fun obtenerTodasMembresias(): List<Membresia> {
        val db = dbHelper.readableDatabase
        val membresias = mutableListOf<Membresia>()

        val cursor = db.query(
            "Membresia",
            null,
            null, null, null, null,
            "fechaInicio DESC"
        )

        while (cursor.moveToNext()) {
            membresias.add(cursorAMembresia(cursor))
        }
        cursor.close()
        return membresias
    }

    /**
     * Obtener membresías con detalles completos (JOIN con Usuario y PlanMembresia)
     * Esta función es ideal para mostrar en un RecyclerView
     */
    fun obtenerMembresiasConDetalles(): List<MembresiaDetalle> {
        val db = dbHelper.readableDatabase
        val membresias = mutableListOf<MembresiaDetalle>()

        val query = """
            SELECT 
                m.id,
                m.idUsuario,
                m.idPlan,
                m.fechaInicio,
                m.fechaFin,
                m.estado,
                u.nombres || ' ' || u.apellidoPaterno || ' ' || u.apellidoMaterno as nombreCompleto,
                u.dni,
                p.nombre as nombrePlan,
                p.precio
            FROM Membresia m
            INNER JOIN Usuario u ON m.idUsuario = u.id
            INNER JOIN PlanMembresia p ON m.idPlan = p.id
            ORDER BY m.fechaInicio DESC
        """

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            membresias.add(
                MembresiaDetalle(
                    id = cursor.getInt(0),
                    idUsuario = cursor.getInt(1),
                    idPlan = cursor.getInt(2),
                    fechaInicio = cursor.getString(3),
                    fechaFin = cursor.getString(4),
                    estado = cursor.getString(5),
                    nombreCompleto = cursor.getString(6),
                    dni = cursor.getInt(7),
                    nombrePlan = cursor.getString(8),
                    precio = cursor.getDouble(9)
                )
            )
        }
        cursor.close()
        return membresias
    }

    /**
     * Obtener todas las membresías de un usuario específico
     */
    fun obtenerMembresiasDeUsuario(idUsuario: Int): List<Membresia> {
        val db = dbHelper.readableDatabase
        val membresias = mutableListOf<Membresia>()

        val cursor = db.query(
            "Membresia",
            null,
            "idUsuario = ?",
            arrayOf(idUsuario.toString()),
            null, null, "fechaInicio DESC"
        )

        while (cursor.moveToNext()) {
            membresias.add(cursorAMembresia(cursor))
        }
        cursor.close()
        return membresias
    }

    /**
     * Obtener la membresía activa de un usuario
     */
    fun obtenerMembresiaActivaDeUsuario(idUsuario: Int): Membresia? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Membresia",
            null,
            "idUsuario = ? AND estado = ?",
            arrayOf(idUsuario.toString(), "Activa"),
            null, null, "fechaInicio DESC", "1"
        )

        var membresia: Membresia? = null
        if (cursor.moveToFirst()) {
            membresia = cursorAMembresia(cursor)
        }
        cursor.close()
        return membresia
    }

    /**
     * Actualizar una membresía completa
     */
    fun actualizarMembresia(membresia: Membresia): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("idUsuario", membresia.idUsuario)
            put("idPlan", membresia.idPlan)
            put("fechaInicio", membresia.fechaInicio)
            put("fechaFin", membresia.fechaFin)
            put("estado", membresia.estado)
        }
        return db.update(
            "Membresia",
            valores,
            "id = ?",
            arrayOf(membresia.id.toString())
        )
    }

    /**
     * Actualizar solo el estado de una membresía
     */
    fun actualizarEstadoMembresia(id: Int, estado: String): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("estado", estado)
        }
        return db.update(
            "Membresia",
            valores,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Eliminar una membresía
     */
    fun eliminarMembresia(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("Membresia", "id = ?", arrayOf(id.toString()))
    }

    /**
     * Verificar y actualizar automáticamente las membresías vencidas
     * Esta función compara la fecha fin con la fecha actual y cambia el estado a "Vencida"
     */
    fun verificarYActualizarMembresiasVencidas() {
        val db = dbHelper.writableDatabase
        val fechaActual = dateFormat.format(Date())

        val valores = ContentValues().apply {
            put("estado", "Vencida")
        }

        db.update(
            "Membresia",
            valores,
            "estado = ? AND fechaFin < ?",
            arrayOf("Activa", fechaActual)
        )
    }

    /**
     * Verificar si un usuario tiene membresía activa
     */
    fun tieneMembresiaTiva(idUsuario: Int): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM Membresia WHERE idUsuario = ? AND estado = 'Activa'",
            arrayOf(idUsuario.toString())
        )

        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        return count > 0
    }

    /**
     * Obtener membresías próximas a vencer (7 días o menos)
     */
    fun obtenerMembresiasProximasAVencer(): List<MembresiaDetalle> {
        val db = dbHelper.readableDatabase
        val membresias = mutableListOf<MembresiaDetalle>()

        val query = """
            SELECT 
                m.id,
                m.idUsuario,
                m.idPlan,
                m.fechaInicio,
                m.fechaFin,
                m.estado,
                u.nombres || ' ' || u.apellidoPaterno || ' ' || u.apellidoMaterno as nombreCompleto,
                u.dni,
                p.nombre as nombrePlan,
                p.precio
            FROM Membresia m
            INNER JOIN Usuario u ON m.idUsuario = u.id
            INNER JOIN PlanMembresia p ON m.idPlan = p.id
            WHERE m.estado = 'Activa'
            AND julianday(m.fechaFin) - julianday('now') <= 7
            AND julianday(m.fechaFin) - julianday('now') > 0
            ORDER BY m.fechaFin ASC
        """

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            membresias.add(
                MembresiaDetalle(
                    id = cursor.getInt(0),
                    idUsuario = cursor.getInt(1),
                    idPlan = cursor.getInt(2),
                    fechaInicio = cursor.getString(3),
                    fechaFin = cursor.getString(4),
                    estado = cursor.getString(5),
                    nombreCompleto = cursor.getString(6),
                    dni = cursor.getInt(7),
                    nombrePlan = cursor.getString(8),
                    precio = cursor.getDouble(9)
                )
            )
        }
        cursor.close()
        return membresias
    }

    /**
     * Convertir un cursor a un objeto Membresia
     */
    private fun cursorAMembresia(cursor: Cursor): Membresia {
        return Membresia(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
            idPlan = cursor.getInt(cursor.getColumnIndexOrThrow("idPlan")),
            fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")),
            fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")),
            estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
        )
    }

    /**
     * Data class para representar una membresía con detalles completos
     * Incluye información del usuario y del plan
     */
    data class MembresiaDetalle(
        val id: Int,
        val idUsuario: Int,
        val idPlan: Int,
        val fechaInicio: String,
        val fechaFin: String,
        val estado: String,
        val nombreCompleto: String,
        val dni: Int,
        val nombrePlan: String,
        val precio: Double
    )
}