package com.example.barberapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barberapp.databinding.ActivityMeusAgendamentosBinding
import com.example.barberapp.model.Agendamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MeusAgendamentosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeusAgendamentosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeusAgendamentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.rvMeusAgendamentos.layoutManager = LinearLayoutManager(this)
        carregarMeusAgendamentos()
    }

    private fun carregarMeusAgendamentos() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("agendamentos")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val agendamentosList = result.documents.map { document ->
                    val agendamento = document.toObject(Agendamento::class.java)!!
                    agendamento.id = document.id
                    agendamento
                }
                binding.rvMeusAgendamentos.adapter = AgendamentoAdapter(agendamentosList) { agendamentoParaCancelar ->
                    confirmarCancelamento(agendamentoParaCancelar)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmarCancelamento(agendamento: Agendamento) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Agendamento")
            .setMessage("Tem certeza que deseja cancelar o agendamento de '${agendamento.servicoNome}'?")
            .setPositiveButton("Sim, cancelar") { _, _ ->
                cancelarAgendamento(agendamento)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun cancelarAgendamento(agendamento: Agendamento) {
        db.collection("agendamentos").document(agendamento.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Agendamento cancelado.", Toast.LENGTH_SHORT).show()
                carregarMeusAgendamentos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cancelar.", Toast.LENGTH_SHORT).show()
            }
    }
}