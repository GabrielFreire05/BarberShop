package com.example.barberapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barberapp.databinding.ActivityServicosBinding
import com.example.barberapp.model.Servico
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServicosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbarServicos)
        supportActionBar?.title = "Nossos Serviços"

        binding.rvServicos.layoutManager = LinearLayoutManager(this)
        fetchServicos()
    }

    private fun fetchServicos() {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("servicos")
            .get()
            .addOnSuccessListener { result ->
                val servicosList = result.map { document ->
                    val servico = document.toObject(Servico::class.java)
                    servico.id = document.id
                    servico
                }
                binding.rvServicos.adapter = ServicoAdapter(servicosList)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Erro ao buscar serviços: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Cria o menu na barra de topo
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Lida com cliques nos itens do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_meus_agendamentos -> {
                startActivity(Intent(this, MeusAgendamentosActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}