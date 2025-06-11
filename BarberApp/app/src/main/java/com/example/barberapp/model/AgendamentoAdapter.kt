package com.example.barberapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barberapp.databinding.ItemAgendamentoBinding
import com.example.barberapp.model.Agendamento

class AgendamentoAdapter(
    private val agendamentos: List<Agendamento>,
    private val onCancelClickListener: (Agendamento) -> Unit
) : RecyclerView.Adapter<AgendamentoAdapter.AgendamentoViewHolder>() {

    class AgendamentoViewHolder(val binding: ItemAgendamentoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoViewHolder {
        val binding = ItemAgendamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AgendamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendamentoViewHolder, position: Int) {
        val agendamento = agendamentos[position]
        holder.binding.tvItemServicoNome.text = agendamento.servicoNome
        holder.binding.tvItemDataHora.text = "Data: ${agendamento.data} - Hora: ${agendamento.horario}"
        holder.binding.btnCancelar.setOnClickListener {
            onCancelClickListener(agendamento)
        }
    }

    override fun getItemCount() = agendamentos.size
}