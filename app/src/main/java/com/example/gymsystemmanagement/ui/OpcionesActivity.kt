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
    private lateinit var dlayMenu : DrawerLayout
    private lateinit var nvMenu : NavigationView
    private lateinit var ivMenu : ImageView


        private lateinit var ivChevron: ImageView
        private lateinit var subMenu: LinearLayout
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
        ivChevron = findViewById(R.id.ivChevron)
        subMenu = findViewById(R.id.subMenu)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Marca "Opciones" como seleccionado cuando estás en esta pantalla
        bottom.selectedItemId = R.id.bottomNav

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itInicio -> {
                    // Ir al Dashboard
                    startActivity(
                        Intent(this, DashboardActivity::class.java).apply {
                            // evita duplicar la activity en el back stack
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    )
                    finish() // cierra Opciones para no apilar
                    true
                }
                R.id.itOpciones -> true   // ya estás aquí
                R.id.itasd -> {
                    // Ejemplo de otro destino si lo usas
                    // startActivity(Intent(this, MembersActivity::class.java))
                    true
                }
                else -> false
            }
        }
        fun toggleMenu() {
            isExpanded = !isExpanded

            if (isExpanded) {
                // Animar expansión
                subMenu.visibility = View.VISIBLE
                ivChevron.animate().rotation(180f).setDuration(200).start()
            } else {
                // Animar colapso
                subMenu.visibility = View.GONE
                ivChevron.animate().rotation(0f).setDuration(200).start()
            }
        }
        ivChevron.setOnClickListener {
            toggleMenu()
        }

    }
}