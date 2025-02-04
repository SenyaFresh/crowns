package ru.hse.edu.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import ru.hse.edu.common.Core
import ru.hse.edu.common.ResultContainer


/**
 * Represents a container that can:
 * - show progress bar when [container] is [ResultContainer.Loading];
 * - show error when [container] is [ResultContainer.Error] and button to handle error;
 * - show [onSuccess] composable when [container] is [ResultContainer.Done].
 */
@Composable
fun ResultContainerComposable(
    container: ResultContainer<*>,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier,
    onLoading: @Composable () -> Unit = { OnLoadingEffect() },
    onSuccess: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (container) {
            is ResultContainer.Done -> {
                onSuccess()
            }

            is ResultContainer.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ErrorMessage(
                        message = Core.errorHandler.getUserFriendlyMessage(container.exception),
                        onClickRetry = { onTryAgain() }
                    )
                }
            }

            is ResultContainer.Loading -> {
                var showLoading by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(50)
                    showLoading = true
                }
                if (showLoading) {
                    onLoading()
                }
            }
        }
    }
}

@Composable
fun OnLoadingEffect() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}