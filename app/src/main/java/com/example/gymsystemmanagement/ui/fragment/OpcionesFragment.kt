package com.example.gymsystemmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.ui.HistorialUsuariosFragment
import com.example.gymsystemmanagement.R

class OpcionesFragment : Fragment() {

    // ========================================
    // SECCIÓN USUARIOS
    // ========================================
    private lateinit var btnMembers: LinearLayout
    private lateinit var subMenuUsuario: LinearLayout
    private lateinit var ivChevron: ImageView
    private lateinit var lnRegistrarUsuarios: LinearLayout
    private lateinit var lnVerUsuarios: LinearLayout
    private var isExpandedUsuarios = false

    // ========================================
    // SECCIÓN PLANES DE MEMBRESÍA
    // ========================================
    private lateinit var btnPlanesMembresia: LinearLayout
    private lateinit var subMenuPlanes: LinearLayout
    private lateinit var ivChevronPlanes: ImageView
    private lateinit var lnRegistrarPlan: LinearLayout
    private lateinit var lnVerPlanes: LinearLayout
    private var isExpandedPlanes = false

    // ========================================
    // SECCIÓN MEMBRESÍAS (NUEVA)
    // ========================================
    private lateinit var btnMembresias: LinearLayout
    private lateinit var subMenuMembresias: LinearLayout
    private lateinit var ivChevronMembresias: ImageView
    private lateinit var lnRegistrarMembresia: LinearLayout
    private lateinit var lnVerMembresias: LinearLayout
    private var isExpandedMembresias = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_opciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ========================================
        // INICIALIZAR VISTAS - USUARIOS
        // ========================================
        btnMembers = view.findViewById(R.id.btnMembers)
        subMenuUsuario = view.findViewById(R.id.subMenu)
        ivChevron = view.findViewById(R.id.ivChevron)
        lnRegistrarUsuarios = view.findViewById(R.id.ln_registrarUsuarios)
        lnVerUsuarios = view.findViewById(R.id.ln_verUsuarios)

        // ========================================
        // INICIALIZAR VISTAS - PLANES
        // ========================================
        btnPlanesMembresia = view.findViewById(R.id.btnPlanesMembresia)
        subMenuPlanes = view.findViewById(R.id.subMenuPlanes)
        ivChevronPlanes = view.findViewById(R.id.ivChevronPlanes)
        lnRegistrarPlan = view.findViewById(R.id.ln_registrarPlan)
        lnVerPlanes = view.findViewById(R.id.ln_verPlanes)

        // ========================================
        // INICIALIZAR VISTAS - MEMBRESÍAS
        // ========================================
        btnMembresias = view.findViewById(R.id.btnMembresias)
        subMenuMembresias = view.findViewById(R.id.subMenuMembresias)
        ivChevronMembresias = view.findViewById(R.id.ivChevronMembresias)
        lnRegistrarMembresia = view.findViewById(R.id.ln_registrarMembresia)
        lnVerMembresias = view.findViewById(R.id.ln_verMembresias)

        // ========================================
        // CONFIGURAR LISTENERS - USUARIOS
        // ========================================
        btnMembers.setOnClickListener { toggleMenuUsuarios() }

        lnRegistrarUsuarios.setOnClickListener {
            navegarConAnimacion(RegistroUsuarioFragment())
        }

        lnVerUsuarios.setOnClickListener {
            navegarConAnimacion(HistorialUsuariosFragment())
        }

        // ========================================
        // CONFIGURAR LISTENERS - PLANES
        // ========================================
        btnPlanesMembresia.setOnClickListener { toggleMenuPlanes() }

        lnRegistrarPlan.setOnClickListener {
            navegarConAnimacion(RegistroPlanMembresiaFragment())
        }

        lnVerPlanes.setOnClickListener {
            navegarConAnimacion(HistorialPlanMembresiaFragment())
        }

        // ========================================
        // CONFIGURAR LISTENERS - MEMBRESÍAS
        // ========================================
        btnMembresias.setOnClickListener { toggleMenuMembresias() }

        lnRegistrarMembresia.setOnClickListener {
            navegarConAnimacion(RegistroMembresiaFragment())
        }

        lnVerMembresias.setOnClickListener {
            navegarConAnimacion(HistorialMembresiaFragment())
        }
    }

    // ========================================
    // FUNCIÓN PARA TOGGLE - USUARIOS
    // ========================================
    private fun toggleMenuUsuarios() {
        isExpandedUsuarios = !isExpandedUsuarios
        if (isExpandedUsuarios) {
            subMenuUsuario.visibility = View.VISIBLE
            ivChevron.animate().rotation(180f).setDuration(200).start()
        } else {
            subMenuUsuario.visibility = View.GONE
            ivChevron.animate().rotation(0f).setDuration(200).start()
        }
    }

    // ========================================
    // FUNCIÓN PARA TOGGLE - PLANES
    // ========================================
    private fun toggleMenuPlanes() {
        isExpandedPlanes = !isExpandedPlanes
        if (isExpandedPlanes) {
            subMenuPlanes.visibility = View.VISIBLE
            ivChevronPlanes.animate().rotation(180f).setDuration(200).start()
        } else {
            subMenuPlanes.visibility = View.GONE
            ivChevronPlanes.animate().rotation(0f).setDuration(200).start()
        }
    }

    // ========================================
    // FUNCIÓN PARA TOGGLE - MEMBRESÍAS
    // ========================================
    private fun toggleMenuMembresias() {
        isExpandedMembresias = !isExpandedMembresias
        if (isExpandedMembresias) {
            subMenuMembresias.visibility = View.VISIBLE
            ivChevronMembresias.animate().rotation(180f).setDuration(200).start()
        } else {
            subMenuMembresias.visibility = View.GONE
            ivChevronMembresias.animate().rotation(0f).setDuration(200).start()
        }
    }

    // ========================================
    // FUNCIÓN HELPER PARA NAVEGACIÓN
    // ========================================
    private fun navegarConAnimacion(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}