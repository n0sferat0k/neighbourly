package com.neighbourly.app.a_device.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.neighbourly.app.PlatformBitmap


fun generateQrCode(content: String, size: Int): ImageBitmap {
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, null)

    val bufferedImage = PlatformBitmap(size, size)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bufferedImage.setPixel(
                x, y,
                if (bitMatrix[x, y])
                    Color.Black.toArgb()
                else
                    Color.White.toArgb(),
            )
        }
    }
    return bufferedImage.asImageBitmap()
}

fun parseCustomText(input: String): List<TextSegment> {
    // Regex that captures the type, id, and inner text.
    val regex = Regex("""<(item|household)\s+id="(\d+)">(.*?)</\1>""")
    val segments = mutableListOf<TextSegment>()
    var lastIndex = 0

    // Iterate over all matches
    for (match in regex.findAll(input)) {
        // Add plain text before the match (if any)
        if (match.range.first > lastIndex) {
            val plainText = input.substring(lastIndex, match.range.first)
            segments.addAll(plainText.split(" ").map { TextSegment.Plain("$it ") })
        }

        // Determine the type
        val type = when (match.groupValues[1]) {
            "item" -> LinkType.ITEM
            "household" -> LinkType.HOUSEHOLD
            else -> error("Unsupported link type")
        }

        val id = match.groupValues[2].toInt()
        val linkText = match.groupValues[3]
        segments.add(TextSegment.Link(type, id, linkText))

        lastIndex = match.range.last + 1
    }

    // Add any remaining plain text after the last match.
    if (lastIndex < input.length) {
        segments.addAll(input.substring(lastIndex).split(" ").map { TextSegment.Plain("$it ") })
    }

    return segments
}

enum class LinkType {
    ITEM, HOUSEHOLD
}

sealed class TextSegment {
    data class Plain(val text: String) : TextSegment()
    data class Link(val type: LinkType, val id: Int, val text: String) : TextSegment()
}
