package com.example.gymsystemmanagement.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "gym.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dni INTEGER NOT NULL UNIQUE,
                apellidoPaterno TEXT NOT NULL,
                apellidoMaterno TEXT NOT NULL,
                nombres TEXT NOT NULL,
                celular TEXT,
                sexo TEXT  ,
                correo TEXT UNIQUE,
                direccion TEXT,
                fechaRegistro TEXT DEFAULT (datetime('now')),
                rol TEXT CHECK (rol IN ('Admin','Empleado','Miembro')) DEFAULT 'Miembro',
                clave TEXT,
                    estado TEXT CHECK (estado IN ('Activo','Inactivo')) DEFAULT 'Activo'
            );
        """)
        db.execSQL("""
            CREATE TABLE PlanMembresia (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                duracionMeses INTEGER NOT NULL,
                precio REAL NOT NULL
            );
        """)
        db.execSQL("""
            CREATE TABLE Membresia (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idUsuario INTEGER NOT NULL,
                idPlan INTEGER NOT NULL,
                fechaInicio TEXT NOT NULL,
                fechaFin TEXT NOT NULL,
                estado TEXT CHECK (estado IN ('Activa','Vencida','Cancelada')) DEFAULT 'Activa',
                FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
                FOREIGN KEY (idPlan) REFERENCES PlanMembresia(id) ON DELETE CASCADE
            );
        """)
        db.execSQL("""
            CREATE TABLE Transaccion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idUsuario INTEGER,
                idMembresia INTEGER,
                monto REAL NOT NULL,
                tipo TEXT CHECK (tipo IN ('Cr','Db')) DEFAULT 'Cr',
                descripcion TEXT,
                fecha TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (idUsuario) REFERENCES Usuario(id),
                FOREIGN KEY (idMembresia) REFERENCES Membresia(id)
            );
        """)
        db.execSQL("""
            CREATE TABLE OtrosIngresos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                concepto TEXT NOT NULL,
                monto REAL NOT NULL,
                fecha TEXT DEFAULT (datetime('now'))
            );
        """)
        db.execSQL("""
            CREATE TABLE Gasto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion TEXT NOT NULL,
                categoria TEXT,
                monto REAL NOT NULL,
                fecha TEXT DEFAULT (datetime('now'))
            );
        """)
        db.execSQL("""
            CREATE TABLE Asistencia (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idUsuario INTEGER NOT NULL,
                fechaHora TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (idUsuario) REFERENCES Usuario(id)
            );
        """)
        db.execSQL("""
            CREATE TABLE Producto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                categoria TEXT NOT NULL,
                precioUnitario REAL NOT NULL,
                descripcion TEXT,
                cantidadActual INTEGER NOT NULL DEFAULT 0,
                stockMinimo INTEGER DEFAULT 5,
                estado TEXT CHECK (estado IN ('Operativo','En reparación','Fuera de servicio')) DEFAULT 'Operativo',
                fechaAdquisicion TEXT,
                costo REAL,
                imagen TEXT
            );
        """)
        db.execSQL("""
            CREATE TABLE VentaDetalle (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idTransaccion INTEGER NOT NULL,
                idProducto INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (idTransaccion) REFERENCES Transaccion(id) ON DELETE CASCADE,
                FOREIGN KEY (idProducto) REFERENCES Producto(id) ON DELETE CASCADE
            );
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS VentaDetalle")
        db.execSQL("DROP TABLE IF EXISTS Producto")
        db.execSQL("DROP TABLE IF EXISTS Asistencia")
        db.execSQL("DROP TABLE IF EXISTS Gasto")
        db.execSQL("DROP TABLE IF EXISTS OtrosIngresos")
        db.execSQL("DROP TABLE IF EXISTS Transaccion")
        db.execSQL("DROP TABLE IF EXISTS Membresia")
        db.execSQL("DROP TABLE IF EXISTS PlanMembresia")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        onCreate(db)
    }
}
