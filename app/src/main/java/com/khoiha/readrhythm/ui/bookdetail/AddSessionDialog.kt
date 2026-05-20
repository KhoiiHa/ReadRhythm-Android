package com.khoiha.readrhythm.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.data.local.ReadingFormat

@Composable
fun AddSessionDialog(
    format: ReadingFormat,
    isSaving: Boolean,
    onCancel: () -> Unit,
    onSave: (minutes: Int, progressAmount: Int) -> Unit
) {
    var minutesText by rememberSaveable { mutableStateOf("") }
    var progressText by rememberSaveable { mutableStateOf("") }

    val copy = sessionDialogCopy(format)
    val minutes = minutesText.toIntOrNull() ?: 0
    val canSave = minutes > 0 && !isSaving

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onCancel()
            }
        },
        title = {
            Text(text = "Add session")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(copy.lengthLabel) },
                    supportingText = { Text(copy.lengthHelper) },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = progressText,
                    onValueChange = { progressText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(copy.progressLabel) },
                    supportingText = { Text(copy.progressHelper) },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        minutes,
                        progressText.toIntOrNull() ?: 0
                    )
                },
                enabled = canSave
            ) {
                Text(if (isSaving) "Saving..." else "Save session")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        }
    )
}

private data class SessionDialogCopy(
    val lengthLabel: String,
    val lengthHelper: String,
    val progressLabel: String,
    val progressHelper: String
)

private fun sessionDialogCopy(format: ReadingFormat): SessionDialogCopy {
    return when (format) {
        ReadingFormat.BOOK -> SessionDialogCopy(
            lengthLabel = "Session length",
            lengthHelper = "How long you spent reading.",
            progressLabel = "Pages read",
            progressHelper = "How many pages this session moved you forward."
        )
        ReadingFormat.AUDIOBOOK -> SessionDialogCopy(
            lengthLabel = "Listening time",
            lengthHelper = "How long you spent listening.",
            progressLabel = "Listening progress",
            progressHelper = "How many minutes this audiobook moved forward."
        )
    }
}
