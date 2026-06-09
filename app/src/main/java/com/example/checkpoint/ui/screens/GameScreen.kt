package com.example.checkpoint.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LabeledChipRow
import com.example.checkpoint.ui.composable.LabeledText
import com.example.checkpoint.ui.composable.LabeledTextWithAction
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ReviewList
import com.example.checkpoint.ui.composable.ReviewRating
import com.example.checkpoint.ui.composable.SmallSplitButtons
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun GameScreen(
	navController: NavHostController,
	viewModel: GameScreenViewModel,
) {
	val state by viewModel.state.collectAsState()
	val scrollState = rememberScrollState()
	val ctx = LocalContext.current

	AppShell(
		navController = navController,
		title = "Game",
		selectedNavigationItem = NavigationItem.Explore,
		appBarActions = {
			IconButton(onClick = {
				if (state.isLoggedIn) {
					if (state.isSaved) viewModel.actions.onRemoveGame()
					else viewModel.actions.onSaveGame()
				} else {
					Toast.makeText(
						ctx, "Log in to follow the games.", Toast.LENGTH_SHORT
					).show()
				}
			}) {
				Icon(
					imageVector = if (state.isSaved) Icons.Rounded.Notifications
					else Icons.Rounded.NotificationsNone,
					contentDescription = if (state.isSaved) "Unfollow game" else "Follow game"
				)
			}
		}) { innerPadding ->

		when {
			state.isLoading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) { CircularProgressIndicator() }
			}

			state.error != null -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = state.error ?: "Error", color = MaterialTheme.colorScheme.error
					)
				}
			}

			state.game != null -> {
				val game = state.game!!
				Column(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize()
						.verticalScroll(scrollState)
				) {
					GameHeader(
						game = game,
						isSaved = state.isSaved,
						averageRating = state.averageRating,
						userLists = state.userLists,
						listsContainingGame = state.listsContainingGame,
						onPrimaryClick = {
							if (state.isLoggedIn) {
								viewModel.actions.onAddGameToBacklog()
							} else {
								Toast.makeText(ctx, "Login to add to backlogs.", Toast.LENGTH_SHORT)
									.show()
							}
						},
						onSecondaryClick = {
							if (!state.isLoggedIn) {
								Toast.makeText(ctx, "Log in to manage lists.", Toast.LENGTH_SHORT)
									.show()
							}
						},
						onConfirmListsClick = { listIds ->
							viewModel.actions.onSynchronizeLists(listIds)
						},
						onCreateListClick = { name -> viewModel.actions.onCreateNewList(name) },
						isLoggedIn = state.isLoggedIn,
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp)
							.padding(bottom = 8.dp)
					)

					if (!game.summary.isNullOrBlank()) {
						LabeledText(
							title = "Description",
							contentText = game.summary,
							modifier = Modifier
								.padding(vertical = 8.dp, horizontal = 16.dp)
								.fillMaxWidth()
						)
					}

					game.firstReleaseDate?.let { epochSeconds ->
						val releaseInstant = Instant.ofEpochSecond(epochSeconds)
						val localDate = releaseInstant.atZone(ZoneId.systemDefault()).toLocalDate()
						val formattedDate =
							localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

						val now = Instant.now()
						if (releaseInstant.isAfter(now)) {
							LabeledTextWithAction(
								title = "Release date",
								contentText = formattedDate,
								actionText = "Add to calendar",
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 8.dp, horizontal = 16.dp)
							) {
								addEventToCalendar(
									ctx = ctx, title = game.name, startTime = game.firstReleaseDate
								)
							}
						} else {
							LabeledText(
								title = "Release date",
								contentText = formattedDate,
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 8.dp, horizontal = 16.dp)
							)
						}
					}

					if (game.genres.isNotEmpty()) {
						LabeledChipRow(
							title = "Genres",
							chips = game.genres.map { ChipContent(it) },
							modifier = Modifier.padding(vertical = 8.dp)
						)
					}

					if (state.franchiseGames.isNotEmpty()) {
						LazyGamesCarousel(
							title = "From the series",
							games = state.franchiseGames,
							hasStartingDivider = true,
							onGameClick = { clickedIgdbId ->
								navController.navigate(NavigationRoute.GameScreen(clickedIgdbId))
							})
					}

					if (state.similarGames.isNotEmpty()) {
						LazyGamesCarousel(
							title = "Similar games",
							games = state.similarGames,
							hasStartingDivider = true,
							onGameClick = { clickedIgdbId ->
								navController.navigate(NavigationRoute.GameScreen(clickedIgdbId))
							})
					}

					ReviewList(
						title = "Reviews",
						reviews = state.reviews,
						users = state.reviewUsers,
						modifier = Modifier.padding(horizontal = 16.dp),
						hasStartingDivider = true,
						hasWriteReviewButton = state.isLoggedIn && state.userReview == null,
						onReviewSubmit = { rating, body, completion ->
							viewModel.actions.onWriteReview(rating, body, completion)
						})
				}
			}
		}
	}
}

@Composable
private fun GameHeader(
	game: Game,
	isSaved: Boolean,
	averageRating: Double?,
	userLists: List<GameListEntity>,
	listsContainingGame: Set<Int>,
	onPrimaryClick: () -> Unit,
	onSecondaryClick: () -> Unit,
	onConfirmListsClick: (List<Int>) -> Unit,
	onCreateListClick: (String) -> Unit,
	isLoggedIn: Boolean,
	modifier: Modifier = Modifier
) {
	var showDialog by remember { mutableStateOf(false) }

	Row(modifier = modifier) {
		AsyncImage(
			modifier = Modifier
				.clip(MaterialTheme.shapes.extraLarge)
				.width(width = 100.dp),
			contentScale = ContentScale.FillWidth,
			model = game.coverUrl,
			contentDescription = game.name,
		)

		Column(
			modifier = Modifier.padding(start = 16.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.headlineSmall,
				modifier = Modifier.basicMarquee()
			)
			if (game.developer != null) {
				Text(
					text = game.developer, style = MaterialTheme.typography.titleMedium
				)
			}

			val finalRating =
				averageRating?.toFloat() ?: (game.totalRating?.div(20))?.toFloat() ?: 0f
			ReviewRating(
				rating = finalRating, modifier = Modifier.fillMaxWidth()
			)

			SmallSplitButtons(onPrimaryClick = onPrimaryClick, onSecondaryClick = {
				onSecondaryClick()
				if (isLoggedIn) {
					showDialog = true
				}
			}, primaryIcon = {
				Icon(
					imageVector = Icons.Rounded.AddCircleOutline, contentDescription = null
				)
			}, primaryLabel = if (isSaved) "Saved" else "Add to Backlog", secondaryIcon = {
				Icon(
					imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null
				)
			})
		}
	}

	// window
	if (showDialog) {
		SaveToListsDialog(
			userLists = userLists,
			listsContainingGame = listsContainingGame,
			onDismissRequest = { showDialog = false },
			onConfirm = { selectedListIds ->
				onConfirmListsClick(selectedListIds)
				showDialog = false
			},
			onCreateListClick = onCreateListClick
		)
	}
}

@Composable
private fun SaveToListsDialog(
	userLists: List<GameListEntity>,
	listsContainingGame: Set<Int>,
	onDismissRequest: () -> Unit,
	onConfirm: (List<Int>) -> Unit,
	onCreateListClick: (String) -> Unit
) {
	var searchQuery by remember { mutableStateOf("") }

	// Synchronize status with db
	var selectedIds by remember { mutableStateOf(setOf<Int>()) }
	LaunchedEffect(listsContainingGame) {
		selectedIds = listsContainingGame
	}

	var showCreateListDialog by remember { mutableStateOf(false) }
	var newListName by remember { mutableStateOf("") }

	val filteredLists = userLists.filter { it.name.contains(searchQuery, ignoreCase = true) }

	Dialog(onDismissRequest = onDismissRequest) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = MaterialTheme.shapes.extraLarge
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Text(
					text = "Save to...",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(bottom = 12.dp)
				)

				// Search
				OutlinedTextField(
					value = searchQuery,
					onValueChange = { searchQuery = it },
					modifier = Modifier.fillMaxWidth(),
					placeholder = { Text("Search list") },
					leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
					singleLine = true,
					shape = MaterialTheme.shapes.extraLarge
				)

				Spacer(modifier = Modifier.height(12.dp))

				// LazyColumn Lists with Checkbox
				LazyColumn(
					modifier = Modifier
						.fillMaxWidth()
						.heightIn(max = 240.dp)
				) {
					if (filteredLists.isEmpty()) {
						item {
							Text(
								text = "No lists found",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								modifier = Modifier.padding(vertical = 8.dp)
							)
						}
					} else {
						items(filteredLists) { list ->
							val isChecked = selectedIds.contains(list.id)
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.clickable {
										selectedIds = if (isChecked) {
											selectedIds - list.id
										} else {
											selectedIds + list.id
										}
									}
									.padding(vertical = 4.dp),
								verticalAlignment = Alignment.CenterVertically) {
								Checkbox(
									checked = isChecked, onCheckedChange = { checked ->
										selectedIds = if (checked) {
											selectedIds + list.id
										} else {
											selectedIds - list.id
										}
									})
								Spacer(modifier = Modifier.width(8.dp))
								Text(
									text = list.name, style = MaterialTheme.typography.bodyLarge
								)
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(8.dp))


				TextButton(
					onClick = { showCreateListDialog = true }, modifier = Modifier.fillMaxWidth()
				) {
					Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add List")
					Spacer(modifier = Modifier.width(8.dp))
					Text("Add list")
					Spacer(modifier = Modifier.weight(1f))
				}

				Spacer(modifier = Modifier.height(16.dp))

				// actions
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End,
					verticalAlignment = Alignment.CenterVertically
				) {
					TextButton(onClick = onDismissRequest) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					TextButton(
						onClick = { onConfirm(selectedIds.toList()) }) {
						Text("OK")
					}
				}
			}
		}
	}

	// Second window for List creation
	if (showCreateListDialog) {
		AlertDialog(
			onDismissRequest = { showCreateListDialog = false },
			title = { Text("Create New List") },
			text = {
				OutlinedTextField(
					value = newListName,
					onValueChange = { newListName = it },
					label = { Text("List Name") },
					singleLine = true,
					shape = MaterialTheme.shapes.medium
				)
			},
			confirmButton = {
				TextButton(
					onClick = {
						if (newListName.isNotBlank()) {
							onCreateListClick(newListName)
							newListName = ""
							showCreateListDialog = false
						}
					}) { Text("Create") }
			},
			dismissButton = {
				TextButton(onClick = { showCreateListDialog = false }) { Text("Cancel") }
			})
	}
}

private fun addEventToCalendar(
	ctx: Context, title: String, startTime: Long
) {
	val intent = Intent(Intent.ACTION_INSERT).apply {
		data = CalendarContract.Events.CONTENT_URI
		putExtra(CalendarContract.Events.TITLE, title)
		putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
		Log.i(
			"GameScreen",
			"Adding calendar event with start time: $startTime (${Instant.ofEpochSecond(startTime)})"
		)
		// TODO: Fix starting time
		// putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
	}

	if (intent.resolveActivity(ctx.packageManager) != null) {
		ctx.startActivity(intent)
	}
}