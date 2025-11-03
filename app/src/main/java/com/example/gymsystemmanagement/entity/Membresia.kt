package com.example.gymsystemmanagement.entity

import java.text.SimpleDateFormat
import java.util.*

data class Membresia(
    val id: Int = 0,
    val idUsuario: Int,
    val idPlan: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val estado: String = "Activa" // Activa, Vencida, Cancelada
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        fun obtenerFechaActual(): String {
            return dateFormat.format(Date())
        }
        fun calcularFechaFin(dias: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, dias)
            return dateFormat.format(calendar.time)
        }
        fun fromCursor(cursor: android.database.Cursor): Membresia {
            return Membresia(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
                idPlan = cursor.getInt(cursor.getColumnIndexOrThrow("idPlan")),
                fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fechaInicio")),
                fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fechaFin")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
            )
        }
    }

    // Verifica si la membresía está activa
    fun estaActiva(): Boolean {
        return estado == "Activa"
    }

    // Verifica si la fecha de fin ya pasó
    private fun fechaFinPasada(): Boolean {
        return try {
            val fechaFinDate = dateFormat.parse(fechaFin)
            val fechaActual = Date()
            fechaFinDate?.before(fechaActual) ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Obtiene los días restantes de la membresía
    fun diasRestantes(): Int {
        return try {
            val fechaFinDate = dateFormat.parse(fechaFin)
            val fechaActual = Date()
            val diferencia = fechaFinDate!!.time - fechaActual.time
            val dias = (diferencia / (1000 * 60 * 60 * 24)).toInt()
            if (dias < 0) 0 else dias
        } catch (e: Exception) {
            0
        }
    }

    // Formatea la fecha de inicio para mostrar
    fun fechaInicioFormateada(): String {
        return try {
            val fecha = dateFormat.parse(fechaInicio)
            displayFormat.format(fecha!!)
        } catch (e: Exception) {
            fechaInicio
        }
    }

    // Formatea la fecha de fin para mostrar
    fun fechaFinFormateada(): String {
        return try {
            val fecha = dateFormat.parse(fechaFin)
            displayFormat.format(fecha!!)
        } catch (e: Exception) {
            fechaFin
        }
    }

    // Verifica si está próxima a vencer (7 días o menos)
    fun proximaAVencer(): Boolean {
        val dias = diasRestantes()
        return dias in 1..7 && estaActiva()
    }

}