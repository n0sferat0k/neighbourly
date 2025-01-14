package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirstOrNull
import kotlin.math.max

/**
 * Layout that will add spacing between the top of the layout and [title]'s first baseline, and
 * [title]'s last baseline and [text]'s first baseline.
 *
 * If [title] and/or [text] do not have any baselines, the spacing will just be applied from the
 * edge of their layouts instead as a best effort implementation.
 */
@Composable
internal fun ColumnScope.AlertDialogBaselineLayout(
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?
) {
    Layout(
        {
            title?.let { title ->
                Box(TitlePadding.layoutId("title").align(Alignment.Start)) {
                    title()
                }
            }
            text?.let { text ->
                Box(TextPadding.layoutId("text").align(Alignment.Start)) {
                    text()
                }
            }
        },
        Modifier.weight(1f, false)
    ) { measurables, constraints ->
        // Measure with loose constraints for height as we don't want the text to take up more
        // space than it needs
        val titlePlaceable = measurables.fastFirstOrNull { it.layoutId == "title" }?.measure(
            constraints.copy(minHeight = 0)
        )
        val textPlaceable = measurables.fastFirstOrNull { it.layoutId == "text" }?.measure(
            constraints.copy(minHeight = 0)
        )

        val layoutWidth = max(titlePlaceable?.width ?: 0, textPlaceable?.width ?: 0)

        val firstTitleBaseline = titlePlaceable?.get(FirstBaseline)?.let { baseline ->
            if (baseline == AlignmentLine.Unspecified) null else baseline
        } ?: 0
        val lastTitleBaseline = titlePlaceable?.get(LastBaseline)?.let { baseline ->
            if (baseline == AlignmentLine.Unspecified) null else baseline
        } ?: 0

        val titleOffset = TitleBaselineDistanceFromTop.roundToPx()

        // Place the title so that its first baseline is titleOffset from the top
        val titlePositionY = titleOffset - firstTitleBaseline

        val firstTextBaseline = textPlaceable?.get(FirstBaseline)?.let { baseline ->
            if (baseline == AlignmentLine.Unspecified) null else baseline
        } ?: 0

        val textOffset = if (titlePlaceable == null) {
            TextBaselineDistanceFromTop.roundToPx()
        } else {
            TextBaselineDistanceFromTitle.roundToPx()
        }

        // Combined height of title and spacing above
        val titleHeightWithSpacing = titlePlaceable?.let { it.height + titlePositionY } ?: 0

        // Align the bottom baseline of the text with the bottom baseline of the title, and then
        // add the offset
        val textPositionY = if (titlePlaceable == null) {
            // If there is no title, just place the text offset from the top of the dialog
            textOffset - firstTextBaseline
        } else {
            if (lastTitleBaseline == 0) {
                // If `title` has no baseline, just place the text's baseline textOffset from the
                // bottom of the title
                titleHeightWithSpacing - firstTextBaseline + textOffset
            } else {
                // Otherwise place the text's baseline textOffset from the title's last baseline
                (titlePositionY + lastTitleBaseline) - firstTextBaseline + textOffset
            }
        }

        // Combined height of text and spacing above
        val textHeightWithSpacing = textPlaceable?.let {
            if (lastTitleBaseline == 0) {
                textPlaceable.height + textOffset - firstTextBaseline
            } else {
                textPlaceable.height + textOffset - firstTextBaseline -
                        ((titlePlaceable?.height ?: 0) - lastTitleBaseline)
            }
        } ?: 0

        val layoutHeight = titleHeightWithSpacing + textHeightWithSpacing

        layout(layoutWidth, layoutHeight) {
            titlePlaceable?.place(0, titlePositionY)
            textPlaceable?.place(0, textPositionY)
        }
    }
}

private val TitlePadding = Modifier.padding(start = 24.dp, end = 24.dp)
private val TextPadding = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp)

// Baseline distance from the first line of the title to the top of the dialog
private val TitleBaselineDistanceFromTop = 40.sp

// Baseline distance from the first line of the text to the last line of the title
private val TextBaselineDistanceFromTitle = 36.sp

// For dialogs with no title, baseline distance from the first line of the text to the top of the
// dialog
private val TextBaselineDistanceFromTop = 38.sp