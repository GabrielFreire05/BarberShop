package com.example.barberapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.barberapp.databinding.ActivityAgendamentoBinding
import com.example.barberapp.model.Agendamento
import com.example.barberapp.model.Horario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var servicoId: String? = null
    private var servicoNome: String? = null
    private var dataSelecionada: String = ""

    companion object {
        const val EXTRA_SERVICO_ID = "EXTRA_SERVICO_ID"
        const val EXTRA_SERVICO_NOME = "EXTRA_SERVICO_NOME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        servicoId = intent.getStringExtra(EXTRA_SERVICO_ID)
        servicoNome = intent.getStringExtra(EXTRA_SERVICO_NOME)
        binding.tvNomeServicoAgendamento.text = "Agendar: $servicoNome"

        binding.rvHorarios.layoutManager = GridLayoutManager(this, 3)

        // Formato da data para salvar no Firestore
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dataSelecionada = sdf.format(Date()) // Data de hoje como padrão

        // Carrega os horários para a data atual
        carregarHorariosDisponiveis(dataSelecionada)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            dataSelecionada = sdf.format(calendar.time)
            carregarHorariosDisponiveis(dataSelecionada)
        }
    }

    private fun carregarHorariosDisponiveis(data: String) {
        val todosOsHorarios = listOf(
            "09:00", "10:00", "11:00",
            "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00"
        ).map { Horario(it) }

        db.collection("agendamentos")
            .whereEqualTo("data", data)
            .get()
            .addOnSuccessListener { result ->
                val horariosOcupados = result.documents.map { it.getString("horario") }

                todosOsHorarios.forEach { horario ->
                    if (horariosOcupados.contains(horario.hora)) {
                        horario.isDisponivel = false
                    }
                }

                binding.rvHorarios.adapter = HorarioAdapter(todosOsHorarios) { horarioClicado ->
                    confirmarAgendamento(horarioClicado)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar horários.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmarAgendamento(horario: Horario) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Agendamento")
            .setMessage("Deseja agendar '${servicoNome}' para o dia $dataSelecionada às ${horario.hora}?")
            .setPositiveButton("Confirmar") { _, _ ->
                salvarAgendamento(horario)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun salvarAgendamento(horario: Horario) {
        val user = auth.currentUser
        if (user == null || servicoNome == null) {
            Toast.makeText(this, "Erro: Usuário não logado ou serviço inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        val agendamento = Agendamento(
            userId = user.uid,
            userName = user.displayName ?: "Nome não definido",
            servicoNome = servicoNome!!,
            data = dataSelecionada,
            horario = horario.hora
        )

        db.collection("agendamentos")
            .add(agendamento)
            .addOnSuccessListener {
                Toast.makeText(this, "Agendamento realizado com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Volta para a tela anterior
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar agendamento: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}