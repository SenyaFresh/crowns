package ru.hse.edu.crowns.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AccountsHelper {

    private val auth = Firebase.auth

    const val USERS_COLLECTION = "users"
    const val KEY_EMAIL = "email"
    const val KEY_NICKNAME = "nickname"
}