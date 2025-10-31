package com.example.gymsystemmanagement.repository

import android.content.Context
import com.example.gymsystemmanagement.data.MembresiaDAO
import com.example.gymsystemmanagement.data.PlanMembresiaDAO
import com.example.gymsystemmanagement.data.TransaccionDAO
import com.example.gymsystemmanagement.entity.Membresia
import com.example.gymsystemmanagement.entity.PlanMembresia
import com.example.gymsystemmanagement.entity.Transaccion
import java.text.SimpleDateFormat
import java.util.*

class MembresiaRepository(context: Context) {
    private val membresiaDAO = MembresiaDAO(context)
    private val planMembresiaDAO = PlanMembresiaDAO(context)
    private val transaccionDAO = TransaccionDAO(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun asignarMembresia(
        idUsuario: Int,
        idPlan: Int,
        metodoPago: String = "Efectivo"
    ): ResultadoAsignacion {
        try {
            // Obtener el plan
            val plan = planMembresiaDAO.obtenerPlanPorId(idPlan)
                ?: return ResultadoAsignacion(false, "Plan no encontrado", null)

            // Calcular fechas
            val fechaInicio = dateFormat.format(Date())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, plan.duracionMeses)
            val fechaFin = dateFormat.format(calendar.time)

            // Cancelar membresías activas previas
            val membresiaActiva = membresiaDAO.obtenerMembresiaActivaDeUsuario(idUsuario)
            if (membresiaActiva != null) {
                membresiaDAO.actualizarEstadoMembresia(membresiaActiva.id, "Cancelada")
            }

            // Crear nueva membresía
            val nuevaMembresia = Membresia(
                idUsuario = idUsuario,
                idPlan = idPlan,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                estado = "Activa"
            )

            val idMembresia = membresiaDAO.insertarMembresia(nuevaMembresia)

            if (idMembresia > 0) {
                // Registrar transacción
                val transaccion = Transaccion(
                    idUsuario = idUsuario,
                    idMembresia = idMembresia.toInt(),
                    monto = plan.precioPlan,
                    tipo = "Cr", // Crédito (ingreso)
                    descripcion = "Pago de membresía: ${plan.nombrePlan} - $metodoPago",
                    fecha = fechaInicio
                )
                transaccionDAO.insertarTransaccion(transaccion)

                return ResultadoAsignacion(
                    true,
                    "Membresía asignada exitosamente",
                    idMembresia.toInt()
                )
            } else {
                return ResultadoAsignacion(false, "Error al crear la membresía", null)
            }
        } catch (e: Exception) {
            return ResultadoAsignacion(false, "Error: ${e.message}", null)
        }
    }

    fun renovarMembresia(idUsuario: Int): ResultadoAsignacion {
        try {
            val membresiaActual = membresiaDAO.obtenerMembresiaActivaDeUsuario(idUsuario)
                ?: return ResultadoAsignacion(false, "No hay membresía activa para renovar", null)

            val plan = planMembresiaDAO.obtenerPlanPorId(membresiaActual.idPlan)
                ?: return ResultadoAsignacion(false, "Plan no encontrado", null)

            // Marcar la membresía actual como vencida
            membresiaDAO.actualizarEstadoMembresia(membresiaActual.id, "Vencida")

            // Crear nueva membresía con el mismo plan
            return asignarMembresia(idUsuario, membresiaActual.idPlan)
        } catch (e: Exception) {
            return ResultadoAsignacion(false, "Error: ${e.message}", null)
        }
    }

    fun cancelarMembresia(idMembresia: Int): Boolean {
        return membresiaDAO.actualizarEstadoMembresia(idMembresia, "Cancelada") > 0
    }

    fun obtenerMembresiasConDetalles(): List<MembresiaDAO.MembresiaDetalle> {
        return membresiaDAO.obtenerMembresiasConDetalles()
    }

    fun obtenerMembresiasActivas(): List<MembresiaDAO.MembresiaDetalle> {
        return membresiaDAO.obtenerMembresiasConDetalles().filter { it.estado == "Activa" }
    }

    fun obtenerHistorialUsuario(idUsuario: Int): List<Membresia> {
        return membresiaDAO.obtenerMembresiasDeUsuario(idUsuario)
    }

    fun obtenerPlanesDisponibles(): List<PlanMembresia> {
        return planMembresiaDAO.obtenerTodosLosPlanes()
    }

    fun verificarMembresiasVencidas() {
        membresiaDAO.verificarYActualizarMembresiasVencidas()
    }
    data class ResultadoAsignacion(
        val exito: Boolean,
        val mensaje: String,
        val idMembresia: Int?
    )
}

