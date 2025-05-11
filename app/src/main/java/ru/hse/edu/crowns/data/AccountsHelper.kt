package ru.hse.edu.crowns.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AccountsHelper {

    suspend fun getUsername(): String {
        return Firebase.firestore.collection(USERS_COLLECTION).document(Firebase.auth.currentUser!!.uid)
            .get().await().getString(KEY_NICKNAME) ?: ""
    }

    const val USERS_COLLECTION = "users"
    const val KEY_EMAIL = "email"
    const val KEY_NICKNAME = "nickname"
}