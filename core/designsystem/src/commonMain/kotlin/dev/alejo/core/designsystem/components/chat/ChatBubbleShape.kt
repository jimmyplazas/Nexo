package dev.alejo.core.designsystem.components.chat

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

enum class TrianglePosition {
    LEFT,
    RIGHT
}

class ChatBubbleShape(
    private val curveSize: Dp = 16.dp,
    private val cornerRadius: Dp = 8.dp,
    private val curvePosition: TrianglePosition
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curveSizePx = with(density) { curveSize.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }

        val path = when (curvePosition) {
            TrianglePosition.LEFT -> {
                val bodyPath = Path().apply {
                    addRoundRect(
                        roundRect = RoundRect(
                            left = curveSizePx,
                            top = 0f,
                            right = size.width,
                            bottom = size.height,
                            cornerRadius = CornerRadius(
                                x = cornerRadiusPx,
                                y = cornerRadiusPx
                            )
                        )
                    )
                }

                val trianglePath = Path().apply {
                    moveTo(0f, size.height)

                    cubicTo(
                        x1 = 0f,
                        y1 = size.height,
                        x2 = curveSizePx,
                        y2 = size.height,
                        x3 = curveSizePx,
                        y3 = size.height - curveSizePx
                    )

                    lineTo(curveSizePx + cornerRadiusPx, size.height)
                    close()
                }

                Path.combine(PathOperation.Union, bodyPath, trianglePath)
            }

            TrianglePosition.RIGHT -> {
                val bodyPath = Path().apply {
                    addRoundRect(
                        roundRect = RoundRect(
                            left = 0f,
                            top = 0f,
                            right = size.width - curveSizePx,
                            bottom = size.height,
                            cornerRadius = CornerRadius(
                                x = cornerRadiusPx,
                                y = cornerRadiusPx
                            )
                        )
                    )
                }

                val trianglePath = Path().apply {
                    moveTo(size.width, size.height)

                    cubicTo(
                        x1 = size.width,
                        y1 = size.height,
                        x2 = size.width - curveSizePx,
                        y2 = size.height,
                        x3 = size.width - curveSizePx,
                        y3 = size.height - curveSizePx
                    )

                    lineTo(size.width - curveSizePx - cornerRadiusPx, size.height)
                    close()
                }

                Path.combine(PathOperation.Union, bodyPath, trianglePath)
            }

        }
        return Outline.Generic(path)
    }

}