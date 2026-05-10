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
import com.example.checkpoint.ui.composable.CarouselItem
import com.example.checkpoint.ui.composable.GamesCarousel
import com.example.checkpoint.ui.theme.CheckpointTheme


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val carouselItems = listOf(
			CarouselItem(R.drawable.re9, "Resident Evil Requiem"),
			CarouselItem(R.drawable.pgm, "Pragmata"),
			CarouselItem(R.drawable.re8, "Resident Evil Village"),
			CarouselItem(R.drawable.fl, "007 First Light"),
			CarouselItem(R.drawable.tmdltd, "Tomodachi Life: Living the Dream")
		)

		setContent {
			CheckpointTheme {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					topBar = {
						AppBar(
							title = "Explore"
						)
					}) { innerPadding ->
					GamesCarousel(
						title = "Popular right now",
						modifier = Modifier
							.padding(innerPadding),
						items = carouselItems
					)
				}
			}
		}
	}
}

