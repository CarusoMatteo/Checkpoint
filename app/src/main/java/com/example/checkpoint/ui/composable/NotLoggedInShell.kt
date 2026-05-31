package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotLoggedInShell(
	modifier: Modifier = Modifier,
	content: @Composable (MutableState<Boolean>) -> Unit
) {
	val showProgressIndicator = remember { mutableStateOf(false) }

	Column(
		modifier = modifier
	) {
		if (showProgressIndicator.value) {
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
		}
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(top = if (!showProgressIndicator.value) 4.dp else 0.dp),
			verticalArrangement = Arrangement.Center,
		) {
			content(showProgressIndicator)
		}
	}
}

@Preview
@Composable
private fun NotLoggedInShellPreview() {
	NotLoggedInShell() { }
}