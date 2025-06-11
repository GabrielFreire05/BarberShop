package com.example.barberapp.model

import com.google.firebase.firestore.PropertyName

data class Agendamento(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("userName") @set:PropertyName("userName") var userName: String = "",
    @get:PropertyName("servicoNome") @set:PropertyName("servicoNome") var servicoNome: String = "",
    @get:PropertyName("data") @set:PropertyName("data") var data: String = "",
    @get:PropertyName("horario") @set:PropertyName("horario") var horario: String = ""
)