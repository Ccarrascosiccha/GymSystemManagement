package com.example.gymsystemmanagement.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymsystemmanagement.R
import com.example.gymsystemmanagement.entity.MembresiaCompleta
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MembresiaAdapter(
    private var membresias: List<MembresiaCompleta>,
    private val onDetallesClick: (MembresiaCompleta) -> Unit,
    private val onCancelarClick: (MembresiaCompleta) -> Unit,
    private val onRenovarClick: (MembresiaCompleta) -> Unit
) : RecyclerView.Adapter<MembresiaAdapter.MembresiaViewHolder>() {

    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    inner class MembresiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIdMembresia: TextView = view.findViewById(R.id.tvIdMembresia)
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvPlan: TextView = view.findViewById(R.id.tvPlan)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val tvFechaInicio: TextView = view.findViewById(R.id.tvFechaInicio)
        val tvFechaFin: TextView = view.findViewById(R.id.tvFechaFin)
        val btnVerDetalles: MaterialButton = view.findViewById(R.id.btnVerDetalles)
        val btnCancelar: MaterialButton = view.findViewById(R.id.btnCancelar)
        val btnRenovar: MaterialButton = view.findViewById(R.id.btnRenovar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembresiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membresia, parent, false)
        return MembresiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembresiaViewHolder, position: Int) {
        val item = membresias[position]
        val membresia = item.membresia
        val usuario = item.usuario

        // Datos de la membresía
        holder.tvIdMembresia.text = membresia.id.toString()

        // Nombre completo del usuario
        val nombreCompleto = "${usuario.nombres} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"
        holder.tvNombreUsuario.text = nombreCompleto

        // DNI del usuario
        holder.tvDni.text = usuario.dni.toString()

        // Estado de la membresía
        holder.tvEstado.text = membresia.estado
        configurarEstado(holder.tvEstado, membresia.estado)

        // Plan y precio
        holder.tvPlan.text = item.nombrePlan
        holder.tvPrecio.text = currencyFormat.format(item.precioPlan)

        // Fechas formateadas
        holder.tvFechaInicio.text = formatearFecha(membresia.fechaInicio)
        holder.tvFechaFin.text = formatearFecha(membresia.fechaFin)

        // Configurar visibilidad de botones según estado
        when (membresia.estado) {
            "Activa" -> {
                holder.btnCancelar.visibility = View.VISIBLE
                holder.btnRenovar.visibility = View.GONE
            }
            "Vencida", "Cancelada" -> {
                holder.btnCancelar.visibility = View.GONE
                holder.btnRenovar.visibility = View.VISIBLE
            }
        }

        // Listeners
        holder.btnVerDetalles.setOnClickListener {
            onDetallesClick(item)
        }

        holder.btnCancelar.setOnClickListener {
            onCancelarClick(item)
        }

        holder.btnRenovar.setOnClickListener {
            onRenovarClick(item)
        }
    }

    override fun getItemCount(): Int = membresias.size

    fun actualizarLista(nuevasMembresias: List<MembresiaCompleta>) {
        membresias = nuevasMembresias
        notifyDataSetChanged()
    }

    private fun formatearFecha(fechaBD: String): String {
        return try {
            val fecha = dbDateFormat.parse(fechaBD)
            displayFormat.format(fecha!!)
        } catch (e: Exception) {
            fechaBD
        }
    }

    private fun configurarEstado(textView: TextView, estado: String) {
        when (estado) {
            "Activa" -> {
                textView.setBackgroundResource(R.drawable.bg_estado_activo)
                textView.setTextColor(Color.WHITE)
            }
            "Vencida" -> {
                textView.setBackgroundResource(R.drawable.bg_estado_vencido)
                textView.setTextColor(Color.WHITE)
            }
            "Cancelada" -> {
                textView.setBackgroundResource(R.drawable.bg_estado_cancelado)
                textView.setTextColor(Color.WHITE)
            }
        }
    }
}