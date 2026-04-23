package com.example.roadapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun SimpleVerticalScrollbar(
    modifier: Modifier = Modifier,
    listState: LazyGridState
) {
    val layoutInfo = listState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo

    if (totalItems > 0 && visibleItems.isNotEmpty()) {
        val firstVisibleIndex = visibleItems.first().index
        val visibleCount = visibleItems.size

        val thumbSizeFraction = (visibleCount.toFloat() / totalItems.toFloat()).coerceIn(0.1f, 1f)

        val scrollOffset = if (totalItems > visibleCount) {
            (firstVisibleIndex.toFloat() / (totalItems - visibleCount).toFloat()) * 2f - 1f
        } else {
            -1f
        }

        Box(
            modifier = modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(thumbSizeFraction)
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        this.transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f)
                        this.translationY = (scrollOffset + 1f) / 2f * (size.height / thumbSizeFraction - size.height)
                    }
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        }
    }
}
