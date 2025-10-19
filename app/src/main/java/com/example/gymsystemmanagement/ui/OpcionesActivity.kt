package com.example.gymsystemmanagement.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymsystemmanagement.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class OpcionesActivity : AppCompatActivity() {
        private lateinit var ln_verUsuarios: LinearLayout
        private lateinit var ln_registrarUsuarios: LinearLayout
        private lateinit var ln_registrarPlan: LinearLayout
        private lateinit var ln_verPlanes: LinearLayout
        private lateinit var subMenuUsuario: LinearLayout
        private lateinit var btnMembers: LinearLayout
        private lateinit var ivChevron: ImageView
    private lateinit var btnPlanesMembresia: LinearLayout
    private lateinit var subMenuPlanes: LinearLayout
    private lateinit var ivChevronPlanes: ImageView
    private var isExpandedPlanes = false
    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
        ln_registrarPlan=findViewById(R.id.ln_registrarPlan)
        ln_verPlanes=findViewById(R.id.ln_verPlanes)
        btnPlanesMembresia = findViewById(R.id.btnPlanesMembresia)
        subMenuPlanes = findViewById(R.id.subMenuPlanes)
        ivChevron = findViewById(R.id.ivChevron)
        ivChevronPlanes = findViewById(R.id.ivChevronPlanes)
        btnMembers = findViewById(R.id.btnMembers)
        subMenuUsuario = findViewById(R.id.subMenu)
        ln_verUsuarios=findViewById(R.id.ln_verUsuarios)
        ln_registrarUsuarios=findViewById(R.id.ln_registrarUsuarios)

        ln_registrarUsuarios.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
        ln_verUsuarios.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

//        lnRegistrarPlan.setOnClickListener {
//            startActivity(Intent(this, RegistrarPlanActivity::class.java))
//        }
//
//        lnVerPlanes.setOnClickListener {
//            startActivity(Intent(this, ListaPlanesActivity::class.java))
//        }


        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Marca "Opciones" como seleccionado cuando estás en esta pantalla
        bottom.selectedItemId = R.id.bottomNav

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itInicio -> {
                    startActivity(
                        Intent(this, DashboardActivity::class.java).apply {
                            // evita duplicar la activity en el back stack
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    )
                    finish()
                    true
                }
                R.id.itOpciones -> true
                R.id.itasd -> {
                    true
                }
                else -> false
            }
        }

        fun toggleMenuPlanes() {
            isExpandedPlanes = !isExpandedPlanes
            if (isExpandedPlanes) {
                subMenuPlanes.visibility = View.VISIBLE
                ivChevronPlanes.animate().rotation(180f).setDuration(200).start()
            } else {
                subMenuPlanes.visibility = View.GONE
                ivChevronPlanes.animate().rotation(0f).setDuration(200).start()
            }
        }

        fun toggleMenu() {
            isExpanded = !isExpanded
            if (isExpanded) {
                subMenuUsuario.visibility = View.VISIBLE
                ivChevron.animate().rotation(180f).setDuration(200).start()
            } else {
                // Animar colapso
                subMenuUsuario.visibility = View.GONE
                ivChevron.animate().rotation(0f).setDuration(200).start()
            }
        }
        btnMembers.setOnClickListener {
            toggleMenu()
        }
        btnPlanesMembresia.setOnClickListener {
            toggleMenuPlanes()
        }
    }
}