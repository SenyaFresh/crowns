package ru.hse.edu.components.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        label = label?.let { { Text(text = label, color = MaterialTheme.colorScheme.onSurface) } },
        singleLine = true,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}