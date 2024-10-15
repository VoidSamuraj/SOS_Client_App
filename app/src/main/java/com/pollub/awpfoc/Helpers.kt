package com.pollub.awpfoc

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

fun ContentDrawScope.drawNeonStroke(boxRadius:Dp, radiusRange: Dp,color: Color) {
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