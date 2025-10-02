package com.example.casonaapp.repository

import com.example.casonaapp.data.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getEvents(): List<Event> {
        return try {
            val result = db.collection("events")
                .whereEqualTo("active", true)
                .get()
                .await()

            result.documents.map { doc ->
                doc.toObject(Event::class.java)?.copy(id = doc.id) ?: Event()
            }.filter { it.title.isNotBlank() } // Filtra eventos vacíos
        } catch (e: Exception) {
            println("ERROR al cargar eventos: ${e.message}") // Agrega este log
            emptyList()
        }
    }

    suspend fun createEvent(event: Event): String? {
        return try {
            val docRef = db.collection("events").add(event).await()
            docRef.id
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    suspend fun updateEvent(eventId: String, event: Event): Boolean {
        return try {
            db.collection("events").document(eventId).set(event).await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            db.collection("events").document(eventId).delete().await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun getEventById(eventId: String): Event? {
        return try {
            val document = db.collection("events").document(eventId).get().await()
            if (document.exists()) {
                document.toObject(Event::class.java)?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            println("ERROR al cargar evento $eventId: ${e.message}")
            null
        }
    }
}