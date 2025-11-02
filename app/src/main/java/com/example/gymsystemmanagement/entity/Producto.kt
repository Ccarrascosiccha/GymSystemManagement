package com.example.gymsystemmanagement.entity

data class Producto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating?     // rating puede venir como objeto
)