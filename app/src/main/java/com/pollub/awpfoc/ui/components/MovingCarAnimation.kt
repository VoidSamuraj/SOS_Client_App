package com.pollub.awpfoc.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollub.awpfoc.R
import com.pollub.awpfoc.ui.theme.DarkBlue
import com.pollub.awpfoc.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PatrolStatus(
    modifier: Modifier,
    iconSizePx: Float,
    status: AppViewModel.Companion.ReportState
) {
    val carPositionX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    val iconSizeDp = iconSizePx / density
    var boxWidth = remember { mutableStateOf(0f) }

    val dynamicEasing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f)

    LaunchedEffect(boxWidth) {
        if (boxWidth.value > 0) {
            scope.launch {
                carPositionX.animateTo(
                    targetValue = boxWidth.value - iconSizePx,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2500,
                            easing = dynamicEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    if (status != AppViewModel.Companion.ReportState.NONE)
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 24.sp,
                color = DarkBlue,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                text = if (status == AppViewModel.Companion.ReportState.CONFIRMED) "Patrol w drodze" else "Zgłoszenie przyjęte"
            )
            Box(modifier = modifier
                .fillMaxWidth(0.3f)
                .align(Alignment.CenterHorizontally)
                .onGloballyPositioned { coordinates ->
                    boxWidth.value = coordinates.size.width.toFloat()

                }) {

                Image(
                    painter = painterResource(id = R.drawable.baseline_directions_car_24),
                    colorFilter = ColorFilter.tint(DarkBlue),
                    contentDescription = "Dynamic car",
                    modifier = Modifier
                        .let {
                            if (status == AppViewModel.Companion.ReportState.CONFIRMED)
                                it.offset {
                                    IntOffset(carPositionX.value.roundToInt(), 0)
                                }
                            else
                                it.align(Alignment.Center)
                        }
                        .size(iconSizeDp.dp)
                )
            }

        }
}