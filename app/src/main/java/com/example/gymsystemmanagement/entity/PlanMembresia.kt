package com.example.gymsystemmanagement.entity

data class PlanMembresia (
    var id: Int = 0,
    var nombrePlan: String = "",
    var descripcionPlan: String = "",
    var duracionMeses: Int = 0,
    var precioPlan: Double = 0.0
)