package com.example.gymsystemmanagement.entity

data class MembresiaCompleta(
    val membresia: Membresia,
    val usuario: Usuario,
    val nombrePlan: String,
    val precioPlan: Double
)