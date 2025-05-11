package ru.hse.edu.crowns.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ru.hse.edu.crowns.model.game.bg.BackgroundEntity
import ru.hse.edu.crowns.model.game.profile.LeaderTableEntity

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

    fun updateMoney(money: Long) {
        Firebase.firestore.collection(USERS_COLLECTION).document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                Firebase.firestore.collection(USERS_COLLECTION)
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(KEY_MONEY, (it.getLong(KEY_MONEY) ?: 0) + money)
            }
    }

    suspend fun getLeaders(): List<LeaderTableEntity> {
        val leaders = mutableListOf<LeaderTableEntity>()
        var tries = 5
        while (tries > 0 && leaders.isEmpty()) {
            Firebase.firestore.collection(USERS_COLLECTION)
                .orderBy(KEY_SCORE)
                .get().addOnSuccessListener {
                    it.documents.take(10).reversed().mapIndexed { index, documentSnapshot ->
                        leaders.add(
                            LeaderTableEntity(
                                index + 1,
                                documentSnapshot.getString(KEY_NICKNAME) ?: "",
                                documentSnapshot.getLong(KEY_SCORE) ?: 0
                            )
                        )
                    }
                }.await()
            tries--
        }

        return leaders
    }

    var selectedBg: MutableState<BackgroundEntity?> = mutableStateOf(null)

    suspend fun getSelectedBg(): BackgroundEntity? {
        var tries = 5
        while (tries > 0 && selectedBg.value == null) {
            selectedBg.value = BackgroundEntity.fromId(
                Firebase.firestore.collection(USERS_COLLECTION)
                    .document(Firebase.auth.currentUser!!.uid)
                    .get().await().getString(KEY_SELECTED_BG).orEmpty()
            )
            tries--
        }
        return selectedBg.value
    }

    suspend fun getAvailableBgs(): List<BackgroundEntity> {
        var tries = 5
        var availableBg: List<BackgroundEntity>? = null

        while (tries > 0 && availableBg == null) {
            availableBg = Firebase.firestore.collection(USERS_COLLECTION)
                .document(Firebase.auth.currentUser!!.uid)
                .get().await().getString(KEY_AVAILABLE_BG).orEmpty().split(";").mapNotNull {
                    BackgroundEntity.fromId(it)
                }
            tries--
        }
        return availableBg.orEmpty()
    }

    fun buyBackground(bg: BackgroundEntity) {
        Firebase.firestore.collection(USERS_COLLECTION)
            .document(Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                val allBg = it.getString(KEY_AVAILABLE_BG)
                val updatedBg = if (allBg.isNullOrEmpty()) bg.id else allBg + ";" + bg.id
                Firebase.firestore.collection(USERS_COLLECTION)
                    .document(Firebase.auth.currentUser!!.uid)
                    .update(KEY_AVAILABLE_BG, updatedBg)
            }

    }

    fun selectBackground(bg: BackgroundEntity) {
        selectedBg.value = bg
        Firebase.firestore.collection(USERS_COLLECTION)
            .document(Firebase.auth.currentUser!!.uid)
            .update(KEY_SELECTED_BG, bg.id)
    }

    const val USERS_COLLECTION = "users"
    const val KEY_EMAIL = "email"
    const val KEY_NICKNAME = "nickname"
    const val KEY_MONEY = "money"
    const val KEY_SCORE = "score"
    const val KEY_SELECTED_BG = "selected_bg"
    const val KEY_AVAILABLE_BG = "available_bg"

}