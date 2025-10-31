package com.example.gymsystemmanagement.entity

import java.text.SimpleDateFormat
import java.util.*

data class Transaccion(
    val id: Int = 0,
    val idUsuario: Int,
    val idMembresia: Int,
    val monto: Double,
    val tipo: String, // Cr (Crédito/Ingreso), Db (Débito/Egreso)
    val descripcion: String,
    val fecha: String
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        private val displayDateOnly = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Tipos de transacción
        const val TIPO_CREDITO = "Cr"  // Ingreso
        const val TIPO_DEBITO = "Db"   // Egreso
    }

    // Verifica si es un ingreso (crédito)
    fun esIngreso(): Boolean {
        return tipo == TIPO_CREDITO
    }

    // Verifica si es un egreso (débito)
    fun esEgreso(): Boolean {
        return tipo == TIPO_DEBITO
    }

    // Obtiene el monto formateado con signo
    fun montoFormateado(): String {
        val signo = if (esIngreso()) "+" else "-"
        return "$signo S/. ${String.format("%.2f", monto)}"
    }

    // Obtiene solo el monto formateado sin signo
    fun montoSinSigno(): String {
        return "S/. ${String.format("%.2f", monto)}"
    }

    // Formatea la fecha completa (fecha y hora)
    fun fechaFormateada(): String {
        return try {
            val fechaDate = dateFormat.parse(fecha)
            displayFormat.format(fechaDate!!)
        } catch (e: Exception) {
            fecha
        }
    }

    // Formatea solo la fecha (sin hora)
    fun fechaSoloFecha(): String {
        return try {
            val fechaDate = dateFormat.parse(fecha)
            displayDateOnly.format(fechaDate!!)
        } catch (e: Exception) {
            fecha
        }
    }

    // Obtiene el tipo de transacción en texto legible
    fun tipoTexto(): String {
        return if (esIngreso()) "Ingreso" else "Egreso"
    }

    // Valida que la transacción sea correcta
    fun esValida(): Boolean {
        return idUsuario > 0 &&
                idMembresia > 0 &&
                monto > 0 &&
                tipo in listOf(TIPO_CREDITO, TIPO_DEBITO) &&
                descripcion.isNotBlank() &&
                fecha.isNotBlank()
    }

    // Obtiene un resumen de la transacción
    fun resumen(): String {
        return "${tipoTexto()} - ${montoFormateado()} - ${fechaSoloFecha()}"
    }
}