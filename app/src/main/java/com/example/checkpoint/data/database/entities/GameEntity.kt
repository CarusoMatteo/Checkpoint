package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a locally saved game.
 * Contains only the igdb_id as a reference to IGDB;
 * All metadata (name, cover, etc.) is retrieved via the API.
 */
@Entity(tableName = "games")
data class GameEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "igdb_id") val igdbId: Int
)
