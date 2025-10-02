package com.example.casonaapp.data

import java.util.Date

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val location: String = "",
    val price: Double = 0.0,
    val availableTickets: Int = 0,
    val imageUrl: String? = null,
    val createdBy: String = "", // UID del admin que creó el evento
    val createdAt: Date = Date(),
    val active: Boolean = true
)