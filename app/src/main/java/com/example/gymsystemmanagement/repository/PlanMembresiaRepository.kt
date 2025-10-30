package com.example.gymsystemmanagement.repository

import android.content.ContentValues
import android.content.Context
import com.example.gymsystemmanagement.data.AppDatabaseHelper
import com.example.gymsystemmanagement.data.PlanMembresiaDAO
import com.example.gymsystemmanagement.entity.PlanMembresia

class PlanMembresiaRepository( context: Context) {

    private val dao = PlanMembresiaDAO(context)
    fun insertarPlanMembresia(planMembresia: PlanMembresia): Long {
        return dao.insertarPlanMembresia(planMembresia)
    }
    fun obtenerTodosLosPlanes(): List<PlanMembresia> {
        return dao.obtenerTodosLosPlanes()
    }
    fun actualizarPlanMembresia(planMembresia: PlanMembresia): Int {
        return dao.actualizar(planMembresia)
    }
    fun eliminarPlanMembresia(id: Int): Int {
        return dao.eliminar(id)
    }
    fun obtenerPlanPorId(id: Int): PlanMembresia? {
        return dao.obtenerPlanPorId(id)
    }
}