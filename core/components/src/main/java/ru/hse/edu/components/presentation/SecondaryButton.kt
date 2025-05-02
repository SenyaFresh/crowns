package ru.hse.edu.components.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}