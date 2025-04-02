package com.example.nativex.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nativex.R
import com.example.nativex.model.Historial

class HistorialAdapter(private val historialList: List<Historial>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partidasTextView: TextView = view.findViewById(R.id.tvPartidas)
        val monedasTextView: TextView = view.findViewById(R.id.tvMonedas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val historial = historialList[position]
        holder.partidasTextView.text = "Partidas: ${historial.partidasList}"
        holder.monedasTextView.text = "Monedas: ${historial.monedas}"
    }

    override fun getItemCount(): Int = historialList.size
}
