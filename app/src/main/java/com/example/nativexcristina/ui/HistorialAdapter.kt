package com.example.nativexcristina.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nativexcristina.R
import com.example.nativexcristina.model.Historial

class HistorialAdapter(private val listaHistorial: List<Historial>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partidasText: TextView = itemView.findViewById(R.id.tvPartidas)
        val monedasText: TextView = itemView.findViewById(R.id.tvMonedas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val historial = listaHistorial[position]
        holder.partidasText.text = "Partidas: ${historial.partidasList}"
        holder.monedasText.text = "Monedas: ${historial.monedas}"
    }

    override fun getItemCount(): Int = listaHistorial.size
}

