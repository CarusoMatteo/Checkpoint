package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Genre of a game (ex., RPG, Action, Strategy).
 * Cached locally after fetching from IGDB.
 */
@Entity(tableName = "genres")
data class GenreEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "igdb_id") val igdbId: Int,

	@ColumnInfo(name = "name") val name: String
)