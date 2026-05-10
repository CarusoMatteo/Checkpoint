package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	modifier: Modifier = Modifier,
	showBackIconButton: Boolean = false,
	actions: @Composable RowScope.() -> Unit = { }
) {
	TopAppBar(
		title = { Text(text = title) },
		modifier = modifier,
		navigationIcon = {
			if (showBackIconButton)
				IconButton(onClick = { /*TODO: Navigate back*/ }) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Back"
					)
				}
		},
		actions = actions
	)
}