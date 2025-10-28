package com.example.gymsystemmanagement.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.ui.HistorialUsuariosFragment

class OpcionesFragment : Fragment() {

    private lateinit var lnVerUsuarios: LinearLayout
    private lateinit var lnRegistrarUsuarios: LinearLayout
    private lateinit var lnRegistrarPlan: LinearLayout
    private lateinit var lnVerPlanes: LinearLayout
    private lateinit var subMenuUsuario: LinearLayout
    private lateinit var btnMembers: LinearLayout
    private lateinit var ivChevron: ImageView
    private lateinit var btnPlanesMembresia: LinearLayout
    private lateinit var subMenuPlanes: LinearLayout
    private lateinit var ivChevronPlanes: ImageView

    private var isExpandedPlanes = false
    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_opciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias de vistas
        lnRegistrarPlan = view.findViewById(R.id.ln_registrarPlan)
        lnVerPlanes = view.findViewById(R.id.ln_verPlanes)
        btnPlanesMembresia = view.findViewById(R.id.btnPlanesMembresia)
        subMenuPlanes = view.findViewById(R.id.subMenuPlanes)
        ivChevron = view.findViewById(R.id.ivChevron)
        ivChevronPlanes = view.findViewById(R.id.ivChevronPlanes)
        btnMembers = view.findViewById(R.id.btnMembers)
        subMenuUsuario = view.findViewById(R.id.subMenu)
        lnVerUsuarios = view.findViewById(R.id.ln_verUsuarios)
        lnRegistrarUsuarios = view.findViewById(R.id.ln_registrarUsuarios)

        // Listeners para abrir Fragmentss
        lnRegistrarUsuarios.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // entrada del nuevo fragment
                    R.anim.slide_out_left,  // salida del actual
                    R.anim.slide_in_left,   // cuando vuelves atrás
                    R.anim.slide_out_right  // al cerrar el fragment
                )
                .replace(R.id.fragmentContainer, RegistroUsuarioFragment())
                .addToBackStack(null)
                .commit()

        }


        lnVerUsuarios.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // entrada del nuevo fragment
                    R.anim.slide_out_left,  // salida del actual
                    R.anim.slide_in_left,   // cuando vuelves atrás
                    R.anim.slide_out_right  // al cerrar el fragment
                )
                .replace(R.id.fragmentContainer, HistorialUsuariosFragment())
                .addToBackStack(null)
                .commit()

        }



        // Ejemplo: Si luego agregas estas Activities, descomenta estas líneas:
        /*
        lnRegistrarPlan.setOnClickListener {
            startActivity(Intent(requireContext(), RegistrarPlanActivity::class.java))
        }

        lnVerPlanes.setOnClickListener {
            startActivity(Intent(requireContext(), ListaPlanesActivity::class.java))
        }
        */

        // Toggle submenús
        btnMembers.setOnClickListener { toggleMenuUsuarios() }
        btnPlanesMembresia.setOnClickListener { toggleMenuPlanes() }
    }

    private fun toggleMenuUsuarios() {
        isExpanded = !isExpanded
        if (isExpanded) {
            subMenuUsuario.visibility = View.VISIBLE
            ivChevron.animate().rotation(180f).setDuration(200).start()
        } else {
            subMenuUsuario.visibility = View.GONE
            ivChevron.animate().rotation(0f).setDuration(200).start()
        }
    }

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
}