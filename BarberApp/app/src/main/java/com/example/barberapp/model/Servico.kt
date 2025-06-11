package com.example.barberapp.model

import com.google.firebase.firestore.PropertyName

data class Servico(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("nome") @set:PropertyName("nome") var nome: String = "",
    @get:PropertyName("descricao") @set:PropertyName("descricao") var descricao: String = "",
    @get:PropertyName("preco") @set:PropertyName("preco") var preco: Double = 0.0,
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = ""
)