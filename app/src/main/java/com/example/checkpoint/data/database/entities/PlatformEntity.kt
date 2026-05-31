package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Gaming platform (ex. PC, PS5, Switch).
 * Data is retrieved from IGDB and cached locally.
 */
@Entity(tableName = "platforms")
data class PlatformEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "igdb_id") val igdbId: Int,

	@ColumnInfo(name = "name") val name: String,

	@ColumnInfo(name = "abbreviation") val abbreviation: String? = null,

	@ColumnInfo(name = "logo_url") val logoUrl: String? = null
)
