package com.example.gymsystemmanagement.api

import com.example.gymsystemmanagement.entity.Producto
import retrofit2.Call
import retrofit2.http.GET

interface FakeStoreApiService {
    @GET("products")
    fun getProducts(): Call<List<Producto>>
}