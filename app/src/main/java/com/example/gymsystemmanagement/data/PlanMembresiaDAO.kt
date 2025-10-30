package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import com.example.gymsystemmanagement.entity.PlanMembresia

class PlanMembresiaDAO (context : Context) {
    private val dbHelper = AppDatabaseHelper(context)


    fun insertarPlanMembresia(planMembresia: PlanMembresia): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", planMembresia.nombrePlan)
            put("descripcion", planMembresia.descripcionPlan)
            put("duracionMeses", planMembresia.duracionMeses)
            put("precio", planMembresia.precioPlan)
        }
        return db.insert("PlanMembresia", null, values)
    }
    fun actualizar(planMembresia: PlanMembresia): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", planMembresia.nombrePlan)
            put("descripcion", planMembresia.descripcionPlan)
            put("duracionMeses", planMembresia.duracionMeses)
            put("precio", planMembresia.precioPlan)
        }
        val filasActualizadas = db.update(
            "PlanMembresia",
            values,
            "id = ?",
            arrayOf(planMembresia.id.toString())
        )
        db.close()
        return filasActualizadas
    }
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete(
            "PlanMembresia",
            "id = ?",
            arrayOf(id.toString())
        )
        db.close()
        return filasEliminadas
    }
    fun obtenerPlanPorId(id: Int): PlanMembresia? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM PlanMembresia WHERE id = ?", arrayOf(id.toString()))

        var plan: PlanMembresia? = null
        if (cursor.moveToFirst()) {
            plan = PlanMembresia(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombrePlan = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcionPlan = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                duracionMeses = cursor.getInt(cursor.getColumnIndexOrThrow("duracionMeses")),
                precioPlan = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            )
        }
        cursor.close()
        db.close()
        return plan
    }
    fun obtenerTodosLosPlanes(): List<PlanMembresia> {
        val db = dbHelper.readableDatabase
        val listaPlanes = mutableListOf<PlanMembresia>()
        val cursor = db.rawQuery("SELECT * FROM PlanMembresia", null)
        while (cursor.moveToNext()) {
            listaPlanes.add(
                PlanMembresia(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombrePlan = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcionPlan = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    duracionMeses = cursor.getInt(cursor.getColumnIndexOrThrow("duracionMeses")),
                    precioPlan = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                )
            )
        }
        cursor.close()
        db.close()
        return listaPlanes
    }

}