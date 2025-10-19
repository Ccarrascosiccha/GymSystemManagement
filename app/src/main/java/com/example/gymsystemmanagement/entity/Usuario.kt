package com.example.gymsystemmanagement.entity

import java.io.Serializable

data class Usuario(
    var id: Int = 0,
    var dni: Int = 0,
    var apellidoPaterno: String = "",
    var apellidoMaterno: String = "",
    var nombres: String = "",
    var celular: String = "",
    var sexo: String = "",
    var correo: String = "",
    var direccion: String = "",
    var fechaRegistro: String = "",
    var rol: String = "Miembro",
    var clave: String = "",
    var estado: String = "Activo"
) :Serializable

