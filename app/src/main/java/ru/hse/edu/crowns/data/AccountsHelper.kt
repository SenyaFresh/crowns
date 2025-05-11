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

    fun updateMoney(money: Long) {
        Firebase.firestore.collection(USERS_COLLECTION).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                if (it.getLong(KEY_MONEY) == null)
                    Firebase.firestore.collection(USERS_COLLECTION)
                        .document(Firebase.auth.currentUser!!.uid)
                        .update(KEY_MONEY, (it.getLong(KEY_MONEY) ?: 0) + money)
            }
    }


    const val USERS_COLLECTION = "users"
    const val KEY_EMAIL = "email"
    const val KEY_NICKNAME = "nickname"
    const val KEY_MONEY = "money"

}