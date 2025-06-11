package com.example.barberapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.databinding.ItemHorarioBinding
import com.example.barberapp.model.Horario

class HorarioAdapter(
    private val horarios: List<Horario>,
    private val onHorarioClickListener: (Horario) -> Unit
) : RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {

    class HorarioViewHolder(val binding: ItemHorarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemHorarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = horarios[position]
        holder.binding.btnHorario.text = horario.hora
        holder.binding.btnHorario.isEnabled = horario.isDisponivel

        if (!horario.isDisponivel) {
            holder.binding.btnHorario.setBackgroundColor(Color.LTGRAY)
            holder.binding.btnHorario.text = "${horario.hora} (Indisponível)"
        }

        holder.binding.btnHorario.setOnClickListener {
            if (horario.isDisponivel) {
                onHorarioClickListener(horario)
            }
        }
    }

    override fun getItemCount() = horarios.size
}