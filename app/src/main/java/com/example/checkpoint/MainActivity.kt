package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.checkpoint.ui.composable.AppBar
import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.theme.CheckpointTheme


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					topBar = {
						AppBar(
							title = "Explore"
						)
					}) { innerPadding ->
					ExploreScreen(modifier = Modifier.padding(innerPadding))
				}
			}
		}
	}
}

