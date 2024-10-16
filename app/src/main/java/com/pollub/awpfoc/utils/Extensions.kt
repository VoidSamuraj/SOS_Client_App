package com.pollub.awpfoc.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

/**
 * Extension function to draw a shadow around a rectangular area with rounded corners in a Composable.
 *
 * This function utilizes the `ContentDrawScope` to apply a shadow effect using specified parameters.
 *
 * @param boxRadius The radius for the rounded corners of the rectangle, specified in Dp.
 * @param radiusRange The distance from the rectangle where the shadow will be drawn, specified in Dp.
 * @param color The color of the shadow, which determines its appearance.
 */
fun ContentDrawScope.drawShadow(boxRadius:Dp, radiusRange: Dp,color: Color) {
    this.drawIntoCanvas {
        val paint =
            Paint().apply {
                style = PaintingStyle.Stroke
                strokeWidth = 40f
            }

        val frameworkPaint =
            paint.asFrameworkPaint()


        this.drawIntoCanvas {
            frameworkPaint.color = color.copy(alpha = 0f).toArgb()
            frameworkPaint.setShadowLayer(
                radiusRange.toPx(), 0f, 0f, color.toArgb()
            )
            it.drawRoundRect(
                left = 0f,
                right = size.width,
                bottom = size.height,
                top = 0f,
                radiusY = boxRadius.toPx(),
                radiusX = boxRadius.toPx(),
                paint = paint
            )

        }
    }
}