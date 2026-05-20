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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoiha.readrhythm.R
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
            Text(text = stringResource(R.string.add_session_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(copy.lengthLabelRes)) },
                    supportingText = { Text(stringResource(copy.lengthHelperRes)) },
                    singleLine = true,
                    enabled = !isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = progressText,
                    onValueChange = { progressText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(copy.progressLabelRes)) },
                    supportingText = { Text(stringResource(copy.progressHelperRes)) },
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
                Text(
                    if (isSaving) {
                        stringResource(R.string.common_saving)
                    } else {
                        stringResource(R.string.add_session_save)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                enabled = !isSaving
            ) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

private data class SessionDialogCopy(
    val lengthLabelRes: Int,
    val lengthHelperRes: Int,
    val progressLabelRes: Int,
    val progressHelperRes: Int
)

private fun sessionDialogCopy(format: ReadingFormat): SessionDialogCopy {
    return when (format) {
        ReadingFormat.BOOK -> SessionDialogCopy(
            lengthLabelRes = R.string.add_session_book_length_label,
            lengthHelperRes = R.string.add_session_book_length_helper,
            progressLabelRes = R.string.add_session_book_progress_label,
            progressHelperRes = R.string.add_session_book_progress_helper
        )
        ReadingFormat.AUDIOBOOK -> SessionDialogCopy(
            lengthLabelRes = R.string.add_session_audio_length_label,
            lengthHelperRes = R.string.add_session_audio_length_helper,
            progressLabelRes = R.string.add_session_audio_progress_label,
            progressHelperRes = R.string.add_session_audio_progress_helper
        )
    }
}
