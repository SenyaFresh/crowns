package ru.hse.edu.crowns.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AccountsHelper {

    suspend fun getUsername(): String {
        return Firebase.firestore.collection(USERS_COLLECTION)
            .document(Firebase.auth.currentUser!!.uid)
            .get().await().getString(KEY_NICKNAME) ?: ""
    }

    fun updateUsername(username: String): Task<Void> {
        return Firebase.firestore.collection(USERS_COLLECTION)
            .document(Firebase.auth.currentUser!!.uid)
            .update(KEY_NICKNAME, username)
    }

    suspend fun getMoney(): Long {
        return Firebase.firestore.collection(USERS_COLLECTION)
            .document(Firebase.auth.currentUser!!.uid)
            .get().await().getLong(KEY_MONEY) ?: 0L
    }

    fun updateScore(score: Long) {
        Firebase.firestore.collection(USERS_COLLECTION).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                Firebase.firestore.collection(USERS_COLLECTION)
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(KEY_MONEY, (it.getLong(KEY_MONEY) ?: 0) + score)
            }
        Firebase.firestore.collection(USERS_COLLECTION).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                Firebase.firestore.collection(USERS_COLLECTION)
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(KEY_SCORE, (it.getLong(KEY_SCORE) ?: 0) + score)
            }
    }


    const val USERS_COLLECTION = "users"
    const val KEY_EMAIL = "email"
    const val KEY_NICKNAME = "nickname"
    const val KEY_MONEY = "money"
    const val KEY_SCORE = "score"

}