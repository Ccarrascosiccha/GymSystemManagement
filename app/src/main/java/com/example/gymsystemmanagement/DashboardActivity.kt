package com.example.gymsystemmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.adapter.MiembrosAdapter
import com.example.gymsystemmanagement.entity.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var rvMembers: RecyclerView
    private lateinit var fabAddMember: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Ajuste para que no se superponga con la barra de estado o navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabAddMember = findViewById(R.id.fabAddMember)
        // Inicialización del RecyclerView de "Latest Members"
        rvMembers = findViewById(R.id.rvMiembros)
        rvMembers.setHasFixedSize(true)
        rvMembers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Datos simulados (podrás reemplazarlos con los que vengan de tu BD)
        val usuarios = listOf(
            Usuario(1, 12345678, "Carrasco", "Siccha", "Carlos Daniel", 999999999, 'M', "carlos@gmail.com", "123a"),
            Usuario(2, 87654321, "Ramírez", "Torres", "Lucía", 988888888, 'F', "lucia@gmail.com", "123a"),
            Usuario(3, 11223344, "Gonzales", "Pérez", "Jorge", 977777777, 'M', "jorge@gmail.com", "123a"),
            Usuario(4, 99887766, "Lopez", "Vega", "María", 966666666, 'F', "maria@gmail.com", "123a")
        )

        // Asignación del adapter
        rvMembers.adapter = MiembrosAdapter(usuarios)

        fabAddMember.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
