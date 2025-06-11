package com.example.barberapp.model

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.barberapp.HorarioAdapter
import com.example.barberapp.databinding.ActivityAgendamentoBinding
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
        setSupportActionBar(binding.toolbarAgendamento)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Usa o horario atual
        binding.calendarView.minDate = System.currentTimeMillis()

        servicoId = intent.getStringExtra(EXTRA_SERVICO_ID)
        servicoNome = intent.getStringExtra(EXTRA_SERVICO_NOME)
        binding.tvNomeServicoAgendamento.text = "Agendar: $servicoNome"

        binding.rvHorarios.layoutManager = GridLayoutManager(this, 3)
        setSupportActionBar(binding.toolbarAgendamento)


        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dataSelecionada = sdf.format(Date())

        val hoje = Calendar.getInstance()
        if (hoje.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && hoje.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            carregarHorariosDisponiveis(dataSelecionada)
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK)

            if (diaDaSemana == Calendar.SATURDAY || diaDaSemana == Calendar.SUNDAY) {
                binding.rvHorarios.adapter = null
                Toast.makeText(this, "Agendamentos não disponíveis nos finais de semana.", Toast.LENGTH_SHORT).show()
            } else {
                dataSelecionada = sdf.format(calendar.time)
                carregarHorariosDisponiveis(dataSelecionada)
            }
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

                val fusoHorarioSP = TimeZone.getTimeZone("America/Sao_Paulo")
                val calendarioHoje = Calendar.getInstance(fusoHorarioSP)

                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                sdf.timeZone = fusoHorarioSP
                val dataDeHojeFormatada = sdf.format(calendarioHoje.time)

                val isHoje = (data == dataDeHojeFormatada)

                todosOsHorarios.forEach { horario ->
                    if (horariosOcupados.contains(horario.hora)) {
                        horario.isDisponivel = false
                    }

                    if (isHoje) {
                        try {
                            val horaDoSlot = horario.hora.split(":")[0].toInt()
                            val horaAtual = calendarioHoje.get(Calendar.HOUR_OF_DAY)

                            // Se a hora do slot for menor OU IGUAL à hora atual, desabilita.
                            if (horaDoSlot <= horaAtual) {
                                horario.isDisponivel = false
                            }
                        } catch (e: Exception) {
                            Log.e("BarberAppDebug", "Erro ao converter a hora do slot: ${horario.hora}", e)
                        }
                    }
                }

                binding.rvHorarios.adapter = HorarioAdapter(todosOsHorarios) { horarioClicado ->
                    confirmarAgendamento(horarioClicado)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarberAppDebug", "Falha ao executar a consulta de horários", e)
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
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar agendamento: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}