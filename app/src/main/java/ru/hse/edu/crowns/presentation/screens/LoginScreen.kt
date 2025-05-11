package ru.hse.edu.crowns.presentation.screens

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
import ru.hse.edu.common.Core
import ru.hse.edu.components.presentation.DefaultTextField
import ru.hse.edu.components.presentation.PrimaryButton
import ru.hse.edu.components.presentation.SecondaryButton
import ru.hse.edu.crowns.ui.theme.AppTheme

@Composable
fun LoginScreen(
    onNavigateToRegistration: () -> Unit,
    onNavigateToMainScreen: () -> Unit
) {
    if (Firebase.auth.currentUser != null) {
        onNavigateToMainScreen()
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Text(
            text = "Вход в аккаунт",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        var signInButtonEnabled by remember { mutableStateOf(true) }
        PrimaryButton(text = "Войти", activated = signInButtonEnabled) {
            signInButtonEnabled = false
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                signInButtonEnabled = true
                if (it.isSuccessful) {
                    onNavigateToMainScreen()
                } else {
                    Core.toaster.showToast("Неверный логин или пароль")
                }
            }
        }

        SecondaryButton(text = "Создать аккаунт", onClick = onNavigateToRegistration )
    }
}


@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen({ }, { })
    }
}