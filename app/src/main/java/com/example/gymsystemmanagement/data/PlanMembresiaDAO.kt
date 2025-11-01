package com.example.gymsystemmanagement.data

import android.content.ContentValues
import android.content.Context
import com.example.gymsystemmanagement.entity.PlanMembresia

class PlanMembresiaDAO(context: Context) {
    val dbHelper = AppDatabaseHelper(context)

    fun insertarPlanMembresia(planMembresia: PlanMembresia): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", planMembresia.nombre)
            put("descripcion", planMembresia.descripcion)
            put("duracionMeses", planMembresia.duracionMeses)
            put("precio", planMembresia.precio)
        }
        return db.insert("PlanMembresia", null, values)
    }

    fun actualizar(planMembresia: PlanMembresia): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", planMembresia.nombre)
            put("descripcion", planMembresia.descripcion)
            put("duracionMeses", planMembresia.duracionMeses)
            put("precio", planMembresia.precio)
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
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                duracionMeses = cursor.getInt(cursor.getColumnIndexOrThrow("duracionMeses")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
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
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    duracionMeses = cursor.getInt(cursor.getColumnIndexOrThrow("duracionMeses")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                )
            )
        }
        cursor.close()
        db.close()
        return listaPlanes
    }

    /**
     * ✅ NUEVA FUNCIÓN: Obtiene el ID de un plan buscando por su nombre
     *
     * @param nombre El nombre del plan (ej: "Plan Mensual")
     * @return El ID del plan encontrado, o 0 si no existe
     */
    fun obtenerIdPorNombre(nombre: String): Int {
        val db = dbHelper.readableDatabase

        // Query SQL: SELECT id FROM PlanMembresia WHERE nombre = ?
        val cursor = db.rawQuery(
            "SELECT id FROM PlanMembresia WHERE nombre = ?",
            arrayOf(nombre)
        )

        var idPlan = 0  // Si no encuentra nada, devuelve 0

        if (cursor.moveToFirst()) {
            // Si encuentra un registro, obtiene el ID
            idPlan = cursor.getInt(0)  // Columna 0 = id
        }

        cursor.close()
        db.close()

        return idPlan
    }

    /**
     * ✅ BONUS: Obtiene solo los nombres de los planes (para el AutoCompleteTextView)
     *
     * @return Lista de nombres de planes
     */
    fun obtenerNombresPlanes(): List<String> {
        val db = dbHelper.readableDatabase
        val nombres = mutableListOf<String>()

        // Query SQL: SELECT nombre FROM PlanMembresia ORDER BY duracionMeses
        val cursor = db.rawQuery(
            "SELECT nombre FROM PlanMembresia ORDER BY duracionMeses ASC",
            null
        )

        while (cursor.moveToNext()) {
            nombres.add(cursor.getString(0))
        }

        cursor.close()
        db.close()

        return nombres
    }
}