package com.example.citytour.view.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citytour.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewState: MainViewModel.ViewState, onIntent: (MainViewModel.Action) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            MainContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                viewState = viewState,
                onIntent = onIntent
            )
        },
        bottomBar = {
            Column {
                Divider(modifier = Modifier.fillMaxWidth())
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White,
                    ),
                    onClick = {
                        onIntent(MainViewModel.Action.GetEntries)
                    }) {
                    Text(text = "Fetch")
                }
            }
        }
    )
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    viewState: MainViewModel.ViewState,
    onIntent: (MainViewModel.Action) -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            text = "Memory Usage:",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(8.dp))
        val memoryUsageData = viewState.monitoringData?.memoryUsages ?: emptyList()
        val memoryChartData = memoryUsageData.mapIndexed { index, value ->
            Pair(index, value)
        }
        LineGraph(
            data = memoryChartData,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )


        Spacer(modifier = Modifier.padding(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(textDecoration = TextDecoration.Underline),
            text = "Thread Usage:",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(8.dp))
        val threadUsageData =
            viewState.monitoringData?.threadUsages?.map { it.toDouble() } ?: emptyList()
        val threadChartData = threadUsageData.mapIndexed { index, value ->
            Pair(index, value)
        }
        LineGraph(
            data = threadChartData,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun LineGraph(
    data: List<Pair<Int, Double>> = emptyList(),
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue
) {
    val spacing = 16.dp
    val graphColor = lineColor
    val axisColor = Color.Black
    val upperValue = (data.maxOfOrNull { it.second }?.plus(1))?.toFloat() ?: 1f
    val lowerValue = (data.minOfOrNull { it.second }?.toFloat() ?: 0f)
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val widthPx = size.width - with(density) { spacing.toPx() }
        val heightPx = size.height - with(density) { spacing.toPx() } * 2
        val yAxisEnd = size.height - with(density) { spacing.toPx() }
        val yAxisSpacing = 24.dp
        val xAxisEnd = with(density) { yAxisSpacing.toPx() }

        // Paint for drawing text
        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textSize = with(density) { 12.sp.toPx() }
        }

        // Draw Y-axis
        drawLine(
            start = Offset(xAxisEnd, yAxisEnd),
            end = Offset(xAxisEnd, 0f),
            color = axisColor,
            strokeWidth = 2.dp.toPx()
        )

        // Draw X-axis
        drawLine(
            start = Offset(xAxisEnd, yAxisEnd),
            end = Offset(size.width.plus(48f), yAxisEnd),
            color = axisColor,
            strokeWidth = 2.dp.toPx()
        )

        // Draw labels on Y-axis
        val ySteps = 10
        val stepValue = 10
        val stepHeight = heightPx / ySteps
        for (i in 0..ySteps) {
            val y = yAxisEnd - i * stepHeight
            val label = (i * stepValue).toString()
            drawContext.canvas.nativeCanvas.drawText(
                label,
                0f,
                y + with(density) { 4.dp.toPx() },
                paint
            )
        }

        val spacePerDataPoint = (size.width - with(density) { spacing.toPx() }) / data.size
        // Draw labels on X-axis
        val intervalCount = 10
        val intervalWidth = widthPx / intervalCount
        for (i in 0..intervalCount) {
            val x = with(density) { spacing.toPx() } + i * intervalWidth
            val label = "${i}m"
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x - with(density) { 9.dp.toPx() },
                yAxisEnd + with(density) { 16.dp.toPx() },
                paint
            )
        }

        val strokePath = androidx.compose.ui.graphics.Path().apply {
            val height = size.height - with(density) { spacing.toPx() } * 2
            data.forEachIndexed { i, pair ->
                val ratio = (pair.second.toFloat() - lowerValue) / (upperValue - lowerValue)
                val x = with(density) { yAxisSpacing.toPx() } + i * spacePerDataPoint
                val y = height - (ratio * height)

                if (i == 0) {
                    moveTo(x, y)
                } else {
                    lineTo(x, y)
                }
            }
        }

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


