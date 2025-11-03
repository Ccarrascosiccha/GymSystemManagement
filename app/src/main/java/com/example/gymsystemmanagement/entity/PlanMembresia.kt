package com.example.gymsystemmanagement.entity

import android.content.ContentValues
import android.database.Cursor
import java.text.NumberFormat
import java.util.*

data class PlanMembresia(
    val id: Int = 0,
    val nombre: String = "",           // ⚠️ Tu tabla usa "nombre", no "nombrePlan"
    val descripcion: String = "",      // ⚠️ Tu tabla usa "descripcion", no "descripcionPlan"
    val duracionMeses: Int = 0,
    val precio: Double = 0.0           // ⚠️ Tu tabla usa "precio", no "precioPlan"
) {

    companion object {
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

        fun fromCursor(cursor: Cursor): PlanMembresia {
            return PlanMembresia(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                duracionMeses = cursor.getInt(cursor.getColumnIndexOrThrow("duracionMeses")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            )
        }

        fun obtenerPlanesPredefinidos(): List<PlanMembresia> {
            return listOf(
                PlanMembresia(
                    nombre = "Plan Mensual",
                    descripcion = "Acceso completo al gimnasio por 1 mes",
                    duracionMeses = 1,
                    precio = 100.0
                ),
                PlanMembresia(
                    nombre = "Plan Trimestral",
                    descripcion = "Acceso completo al gimnasio por 3 meses con 10% de descuento",
                    duracionMeses = 3,
                    precio = 270.0
                ),
                PlanMembresia(
                    nombre = "Plan Semestral",
                    descripcion = "Acceso completo al gimnasio por 6 meses con 15% de descuento",
                    duracionMeses = 6,
                    precio = 510.0
                ),
                PlanMembresia(
                    nombre = "Plan Anual",
                    descripcion = "Acceso completo al gimnasio por 12 meses con 25% de descuento",
                    duracionMeses = 12,
                    precio = 900.0
                )
            )
        }
    }

    fun precioFormateado(): String {
        return currencyFormat.format(precio)
    }

    fun precioMensual(): Double {
        return if (duracionMeses > 0) precio / duracionMeses else 0.0
    }

    fun precioMensualFormateado(): String {
        return currencyFormat.format(precioMensual())
    }

    fun duracionTexto(): String {
        return when {
            duracionMeses == 1 -> "1 mes"
            duracionMeses < 12 -> "$duracionMeses meses"
            duracionMeses == 12 -> "1 año"
            duracionMeses % 12 == 0 -> "${duracionMeses / 12} años"
            else -> "$duracionMeses meses"
        }
    }


}