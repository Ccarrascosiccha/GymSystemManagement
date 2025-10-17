package com.example.gymsystemmanagement.entity

import java.io.Serializable

data class Usuario (
    var codigo : Int,
    var dni : Int,
    var apellidoPaterno : String = "",
    var apellidoMaterno : String = "",
    var nombres : String = "",
    var celular:Int,
    var sexo: Char,
    var correo : String = "",
    var clave : String = ""
): Serializable

