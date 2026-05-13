package com.khoiha.readrhythm.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
fun AddBookDialog(
    isSaving: Boolean,
    onCancel: () -> Unit,
    onSave: (title: String, author: String?, format: ReadingFormat, totalUnits: Int) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var selectedFormat by rememberSaveable { mutableStateOf(ReadingFormat.BOOK) }
    var totalUnitsText by rememberSaveable { mutableStateOf("") }

    val trimmedTitle = title.trim()
    val canSave = trimmedTitle.isNotEmpty() && !isSaving

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onCancel()
            }
        },
        title = {
            Text(text = "Add to library")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Author optional") },
                    singleLine = true,
                    enabled = !isSaving
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFormat == ReadingFormat.BOOK,
                        onClick = { selectedFormat = ReadingFormat.BOOK },
                        label = { Text("Book") },
                        enabled = !isSaving
                    )
                    FilterChip(
                        selected = selectedFormat == ReadingFormat.AUDIOBOOK,
                        onClick = { selectedFormat = ReadingFormat.AUDIOBOOK },
                        label = { Text("Audiobook") },
                        enabled = !isSaving
                    )
                }

                OutlinedTextField(
                    value = totalUnitsText,
                    onValueChange = { totalUnitsText = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Total units optional") },
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
                        trimmedTitle,
                        author,
                        selectedFormat,
                        totalUnitsText.toIntOrNull() ?: 0
                    )
                },
                enabled = canSave
            ) {
                Text(if (isSaving) "Saving..." else "Save")
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
