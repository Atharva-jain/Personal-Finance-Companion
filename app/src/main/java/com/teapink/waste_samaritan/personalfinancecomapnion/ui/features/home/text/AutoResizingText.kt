package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

// Place this at the bottom of your file. It calculates if the text is overflowing
// and smoothly scales the font down by 5% at a time until it perfectly fits.
@Composable
fun AutoResizingText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null
) {
    var resizedTextStyle by remember { mutableStateOf(style) }
    var shouldDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) drawContent() // Only render to the screen once we know the perfect size
        },
        softWrap = false,
        style = resizedTextStyle,
        fontWeight = fontWeight,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                // If it's too big, shrink the font size by 5% and try again
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95f
                )
            } else {

                shouldDraw = true
            }
        }
    )
}