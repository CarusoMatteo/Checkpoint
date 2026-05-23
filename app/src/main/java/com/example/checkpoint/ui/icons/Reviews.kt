package com.example.checkpoint.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
val Reviews: ImageVector
	get() {
		if (reviews != null) {
			return reviews!!
		}
		reviews =
			ImageVector.Builder(
				name = "reviews",
				defaultWidth = 24.dp,
				defaultHeight = 24.dp,
				viewportWidth = 24f,
				viewportHeight = 24f,
			)
				.apply {
					path(
						fill = SolidColor(Color.Black),
						fillAlpha = 1f,
						stroke = null,
						strokeAlpha = 1f,
						strokeLineWidth = 1f,
						strokeLineCap = StrokeCap.Butt,
						strokeLineJoin = StrokeJoin.Bevel,
						strokeLineMiter = 1f,
						pathFillType = PathFillType.NonZero,
					) {
						moveTo(12f, 12.48f)
						lineToRelative(1.9f, 1.15f)
						quadToRelative(0.28f, 0.17f, 0.55f, -0.01f)
						reflectiveQuadToRelative(0.2f, -0.51f)
						lineToRelative(-0.5f, -2.18f)
						lineToRelative(1.7f, -1.47f)
						quadTo(16.1f, 9.23f, 16f, 8.91f)
						reflectiveQuadTo(15.55f, 8.57f)
						lineTo(13.33f, 8.4f)
						lineTo(12.45f, 6.35f)
						quadTo(12.33f, 6.05f, 12f, 6.05f)
						reflectiveQuadToRelative(-0.45f, 0.3f)
						lineTo(10.68f, 8.4f)
						lineTo(8.45f, 8.57f)
						quadTo(8.1f, 8.6f, 8f, 8.91f)
						reflectiveQuadTo(8.15f, 9.45f)
						lineToRelative(1.7f, 1.47f)
						lineTo(9.35f, 13.1f)
						quadToRelative(-0.07f, 0.32f, 0.2f, 0.51f)
						reflectiveQuadToRelative(0.55f, 0.01f)
						lineTo(12f, 12.48f)
						close()
						moveTo(6f, 18f)
						lineTo(3.7f, 20.3f)
						quadTo(3.23f, 20.78f, 2.61f, 20.51f)
						reflectiveQuadTo(2f, 19.58f)
						verticalLineTo(4f)
						quadTo(2f, 3.17f, 2.59f, 2.59f)
						reflectiveQuadTo(4f, 2f)
						horizontalLineTo(20f)
						quadToRelative(0.83f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(22f, 4f)
						verticalLineTo(16f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(20f, 18f)
						horizontalLineTo(6f)
						close()
						moveTo(5.15f, 16f)
						horizontalLineTo(20f)
						verticalLineTo(4f)
						horizontalLineTo(4f)
						verticalLineTo(17.13f)
						lineTo(5.15f, 16f)
						close()
						moveTo(4f, 16f)
						verticalLineTo(4f)
						verticalLineTo(16f)
						close()
					}
				}
				.build()
		return reviews!!
	}

private var reviews: ImageVector? = null
