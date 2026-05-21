package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.checkpoint.ui.screens.GamesGridScreen
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.example.checkpoint.ui.screens.carouselItems

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				GamesGridScreen("Popular right now", carouselItems, {}, modifier = Modifier)
			}
		}
	}
}
