package ru.hse.edu.crowns.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import ru.hse.edu.common.Core
import ru.hse.edu.components.presentation.DefaultTextField
import ru.hse.edu.components.presentation.PrimaryButton
import ru.hse.edu.components.presentation.SecondaryButton
import ru.hse.edu.crowns.data.AccountsHelper.KEY_EMAIL
import ru.hse.edu.crowns.data.AccountsHelper.KEY_MONEY
import ru.hse.edu.crowns.data.AccountsHelper.KEY_NICKNAME
import ru.hse.edu.crowns.data.AccountsHelper.USERS_COLLECTION
import ru.hse.edu.crowns.ui.theme.AppTheme

@Composable
fun RegistrationScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMainScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var nickname by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        DefaultTextField(
            value = nickname,
            label = "Никнейм",
            onValueChange = { nickname = it }
        )

        DefaultTextField(
            value = email,
            label = "Почта",
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        DefaultTextField(
            value = password,
            label = "Пароль",
            onValueChange = { password = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        var signUpButtonEnabled by remember { mutableStateOf(true) }
        PrimaryButton(text = "Зарегистрироваться", activated = signUpButtonEnabled) {
            signUpButtonEnabled = false
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { authResultTask ->
                    signUpButtonEnabled = true
                    if (authResultTask.isSuccessful) {
                        val firestoreSignUpData = hashMapOf(
                            KEY_EMAIL to email,
                            KEY_NICKNAME to nickname,
                            KEY_MONEY to 0L
                        )
                        Firebase.firestore.collection(USERS_COLLECTION)
                            .document(authResultTask.result.user!!.uid)
                            .set(firestoreSignUpData)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    onNavigateToMainScreen()
                                } else {
                                    Core.toaster.showToast("Не удалось создать аккаунт.")
                                }
                            }
                        onNavigateToMainScreen()
                    } else {
                        Core.toaster.showToast("Не удалось создать аккаунт.")
                    }
                }
        }

        SecondaryButton(text = "Уже есть аккаунт", onClick = onNavigateToLogin)
    }
}


@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    AppTheme {
        RegistrationScreen({ }, { })
    }
}