package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun PasswordOutlinedTextField(
	state: TextFieldState,
	label: @Composable TextFieldLabelScope.() -> Unit,
	modifier: Modifier = Modifier
) {
	var showPassword by remember { mutableStateOf(false) }

	OutlinedSecureTextField(
		state = state,
		label = label,
		textObfuscationMode = if (showPassword) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
		modifier = modifier,
		trailingIcon = {
			Row {
				if (state.text.isNotEmpty()) {
					IconButton(onClick = { state.clearText() }) {
						Icon(Icons.Rounded.Clear, contentDescription = "Clear password")
					}
				}
				IconButton(onClick = { showPassword = !showPassword }) {
					Icon(
						imageVector = if (showPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
						contentDescription = "Toggle password visibility"
					)
				}
			}
		}
	)
}