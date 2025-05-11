package ru.hse.edu.crowns.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.hse.edu.components.presentation.PrimaryButton
import ru.hse.edu.components.presentation.SecondaryButton
import ru.hse.edu.crowns.data.AccountsHelper
import ru.hse.edu.crowns.presentation.game.GameSessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSignIn: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var money by remember { mutableLongStateOf(0L) }
    var dialogState by remember { mutableStateOf(DialogState.NOT_SHOWN) }

    if (dialogState != DialogState.NOT_SHOWN) {
        BasicAlertDialog(onDismissRequest = {
            dialogState = DialogState.NOT_SHOWN
        }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val title = remember(dialogState) {
                        when (dialogState) {
                            DialogState.CHANGE_NAME -> "Смена никнейма"
                            DialogState.GET_LEADERS -> "Таблица лидеров"
                            else -> "Магазин фонов"
                        }
                    }
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    when (dialogState) {
                        DialogState.CHANGE_NAME -> {
                            var newNickname by remember { mutableStateOf(nickname) }

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                maxLines = 1,
                                value = newNickname,
                                onValueChange = { newNickname = it },
                                label = { Text("Новый никнейм") }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                SecondaryButton(text = "Отмена", modifier = Modifier.weight(1f)) {
                                    dialogState = DialogState.NOT_SHOWN
                                }
                                Spacer(modifier = Modifier.weight(0.1f))
                                PrimaryButton(
                                    text = "Ок",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    nickname = ""
                                    AccountsHelper.updateUsername(newNickname)
                                        .addOnSuccessListener {
                                            nickname = newNickname
                                        }
                                    dialogState = DialogState.NOT_SHOWN
                                }
                            }
                        }

                        else -> {}
                    }

                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(color = MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        LaunchedEffect(Unit) {
            nickname = AccountsHelper.getUsername()
        }

        LaunchedEffect(Unit) {
            money = AccountsHelper.getMoney()
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            text = "Профиль",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            modifier = Modifier,
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = nickname.ifEmpty { "Загрузка..." },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (nickname.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 160.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(nickname.isNotEmpty()) {
                    IconButton(
                        modifier = Modifier
                            .height(38.dp)
                            .aspectRatio(1.6f),
                        onClick = { dialogState = DialogState.CHANGE_NAME },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, null)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    modifier = Modifier
                        .height(38.dp)
                        .aspectRatio(1.6f),
                    onClick = {
                        Firebase.auth.signOut()
                        onNavigateToSignIn()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, null)
                }
            }
        }

        Card(
            modifier = Modifier,
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Монеток: ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$money\uD83E\uDE99",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (money == 0L) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

enum class DialogState {
    NOT_SHOWN,
    CHANGE_NAME,
    GET_LEADERS,
    BUY_BACKGROUND
}