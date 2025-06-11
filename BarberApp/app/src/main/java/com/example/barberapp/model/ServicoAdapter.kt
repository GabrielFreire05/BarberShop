package com.example.barberapp

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barberapp.databinding.ItemServicoBinding
import com.example.barberapp.model.AgendamentoActivity
import com.example.barberapp.model.Servico

class ServicoAdapter(private val servicos: List<Servico>) : RecyclerView.Adapter<ServicoAdapter.ServicoViewHolder>() {

    class ServicoViewHolder(val binding: ItemServicoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicoViewHolder {
        val binding = ItemServicoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServicoViewHolder(binding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ServicoViewHolder, position: Int) {
        val servico = servicos[position]
        with(holder.binding) {
            tvNomeServico.text = servico.nome
            tvDescricaoServico.text = servico.descricao
            tvPrecoServico.text = String.format("R$ %.2f", servico.preco)

            Glide.with(root.context)
                .load(servico.imageUrl)
                .placeholder(R.color.blue_light)
                .into(ivServico)

            // Ação de clique para ir para a tela de agendamento
            root.setOnClickListener {
                val intent = Intent(root.context, AgendamentoActivity::class.java).apply {
                    putExtra(AgendamentoActivity.EXTRA_SERVICO_ID, servico.id)
                    putExtra(AgendamentoActivity.EXTRA_SERVICO_NOME, servico.nome)
                }
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = servicos.size
}