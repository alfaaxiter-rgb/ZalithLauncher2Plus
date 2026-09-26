/*
 * ALFAA neon slider components.
 */
package com.movtery.zalithlauncher.ui.components

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import com.movtery.zalithlauncher.utils.math.addBigDecimal
import com.movtery.zalithlauncher.utils.math.subtractBigDecimal
import java.text.DecimalFormat
import kotlin.math.roundToInt

private val NeonBlack = Color(0xFF05080D)
private val NeonPanel = Color(0xFF101923)
private val NeonTrack = Color(0xFF16483F)
private val NeonGreen = Color(0xFF20E0B2)
private val NeonCyan = Color(0xFF43C7FF)
private val NeonText = Color(0xFFE8FFF8)
private val NeonMuted = Color(0xFF829A98)

@Composable
fun SimpleTextSlider(
    modifier: Modifier = Modifier,
    shorter: Boolean = false,
    value: Float,
    decimalFormat: String = "#0.00",
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit,
    toInt: Boolean = false,
    suffix: String? = null,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    onTextClick: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    fineTuningControl: Boolean = false,
    fineTuningStep: Float = 0.5f,
    appendContent: @Composable () -> Unit = {}
) {
    val formatter = remember(decimalFormat) { DecimalFormat(decimalFormat) }
    fun textFor(v: Float) = if (toInt) v.toInt().toString() else formatter.format(v)
    fun change(v: Float, finished: Boolean) {
        onValueChange(v.coerceIn(valueRange))
        if (finished) onValueChangeFinished?.invoke()
    }

    LaunchedEffect(value, valueRange) {
        if (value !in valueRange) change(value.coerceIn(valueRange), true)
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        NeonSlider(
            modifier = Modifier.weight(1f),
            value = value,
            valueRange = valueRange,
            enabled = enabled,
            steps = steps,
            onValueChange = { change(it, false) },
            onValueChangeFinished = onValueChangeFinished
        )

        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(NeonPanel, RoundedCornerShape(10.dp))
                .clickable(enabled = enabled && onTextClick != null) { onTextClick?.invoke() }
                .padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = textFor(value),
                style = TextStyle(
                    color = if (enabled) NeonText else NeonMuted.copy(alpha = DisabledAlpha),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
            suffix?.let {
                BasicText(
                    text = it,
                    style = TextStyle(
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (fineTuningControl) {
                Spacer(Modifier.width(7.dp))
                NeonStepButton("‹", enabled) {
                    val n = value.subtractBigDecimal(fineTuningStep)
                    change(if (n <= valueRange.start) valueRange.start else n, true)
                }
                Spacer(Modifier.width(4.dp))
                NeonStepButton("›", enabled) {
                    val n = value.addBigDecimal(fineTuningStep)
                    change(if (n >= valueRange.endInclusive) valueRange.endInclusive else n, true)
                }
            }
        }
        appendContent()
    }
}

@Composable
private fun NeonStepButton(icon: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(
                if (enabled) NeonGreen.copy(alpha = .14f) else NeonTrack.copy(alpha = .35f),
                RoundedCornerShape(6.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = icon,
            style = TextStyle(
                color = if (enabled) NeonGreen else NeonMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            )
        )
    }
}

@Composable
private fun NeonSlider(
    modifier: Modifier = Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    steps: Int,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?
) {
    val range = valueRange.endInclusive - valueRange.start

    fun valueAt(x: Float, width: Float): Float {
        if (width <= 0f || range <= 0f) return valueRange.start
        var result = (valueRange.start + (x / width).coerceIn(0f, 1f) * range)
        if (steps > 0) {
            val step = range / (steps + 1)
            result = valueRange.start +
                (((result - valueRange.start) / step).roundToInt() * step)
        }
        return result.coerceIn(valueRange.start, valueRange.endInclusive)
    }

    Box(
        modifier = modifier
            .height(28.dp)
            .pointerInput(valueRange, enabled, steps) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { onValueChange(valueAt(it.x, size.width.toFloat())) },
                    onDrag = { change, _ ->
                        change.consume()
                        onValueChange(valueAt(change.position.x, size.width.toFloat()))
                    },
                    onDragEnd = { onValueChangeFinished?.invoke() }
                )
            }
    ) {
        Canvas(Modifier.fillMaxWidth().height(28.dp)) {
            val centerY = size.height / 2f
            val startX = 4.dp.toPx()
            val endX = size.width - 4.dp.toPx()
            val fraction = if (range <= 0f) 0f
            else ((value - valueRange.start) / range).coerceIn(0f, 1f)
            val x = startX + (endX - startX) * fraction

            drawLine(NeonTrack, Offset(startX, centerY), Offset(endX, centerY), 6.dp.toPx(), StrokeCap.Round)
            drawLine(NeonGreen, Offset(startX, centerY), Offset(x, centerY), 6.dp.toPx(), StrokeCap.Round)
            drawCircle(NeonCyan, 7.dp.toPx(), Offset(x, centerY))
            drawCircle(NeonBlack, 3.dp.toPx(), Offset(x, centerY))
        }
    }
}

@Composable
fun IndicatorSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0
) {
    NeonSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        enabled = enabled,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished
    )
}
