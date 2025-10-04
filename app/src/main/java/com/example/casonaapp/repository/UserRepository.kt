package com.example.casonaapp.repository

import com.example.casonaapp.data.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun createUser(user: UserProfile): String? {
        return try {
            val docRef = db.collection("users").add(user).await()
            docRef.id
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    suspend fun updateUser(userId: String, user: UserProfile): Boolean {
        return try {
            db.collection("users").document(userId).set(user).await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun deleteUser(userId: String): Boolean {
        return try {
            db.collection("users").document(userId).delete().await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }
    
    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                document.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile): Boolean {
        return try {
            db.collection("users").document(userProfile.uid).set(userProfile).await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun updateDisplayName(displayName: String): Boolean {
        return try {
            val user = auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user?.updateProfile(profileUpdates)?.await()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun getAllUsers(): List<UserProfile> {
        return try {
            val users = db.collection("users").get().await()

            users.documents.map { doc ->
                doc.toObject(UserProfile::class.java)?.copy(uid = doc.id) ?: UserProfile()
            }

        } catch (e: Exception){
            println("Error al cargar los usuarios: ${e.message}")
            emptyList()
        }
    }
}
