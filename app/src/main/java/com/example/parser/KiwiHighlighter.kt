package com.example.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.model.EditorTheme

/**
 * High-performance syntax highlighter for KiwiCode editor.
 */
object KiwiHighlighter {

    private val KEYWORDS = setOf(
        "toolbar",
        "grid-container",
        "grid_container",
        "item"
    )

    private val PROPERTIES = setOf(
        "title",
        "background-color",
        "background_color",
        "background",
        "padding",
        "icon",
        "image",
        "subtitle",
        "color",
        "bgcolor"
    )

    fun highlight(code: String, theme: EditorTheme): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            if (code.isEmpty()) return@buildAnnotatedString

            // 1. Comments: // ...
            val commentRegex = Regex("""//.*$""", RegexOption.MULTILINE)
            for (match in commentRegex.findAll(code)) {
                addStyle(
                    SpanStyle(
                        color = theme.commentColor,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // Block comments /* ... */
            val blockCommentRegex = Regex("""/\*[\s\S]*?\*/""")
            for (match in blockCommentRegex.findAll(code)) {
                addStyle(
                    SpanStyle(
                        color = theme.commentColor,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 2. Strings: "..."
            val stringRegex = Regex(""""([^"\\]|\\.)*"""")
            for (match in stringRegex.findAll(code)) {
                val strValue = match.value
                // Check if string is a hex color value e.g. "#FF0000"
                val hexColorRegex = Regex("""^"#[0-9a-fA-F]{3,8}"$""")
                if (hexColorRegex.matches(strValue)) {
                    addStyle(
                        SpanStyle(
                            color = theme.colorLiteralBadgeColor,
                            fontWeight = FontWeight.Bold
                        ),
                        match.range.first,
                        match.range.last + 1
                    )
                } else if (strValue.contains(Regex("""\d+px"""))) {
                    // String containing dimensions like "7px"
                    addStyle(
                        SpanStyle(
                            color = theme.numberColor,
                            fontWeight = FontWeight.Medium
                        ),
                        match.range.first,
                        match.range.last + 1
                    )
                } else {
                    addStyle(
                        SpanStyle(color = theme.stringColor),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }

            // 3. Keywords
            val wordRegex = Regex("""\b[a-zA-Z_-]+\b""")
            for (match in wordRegex.findAll(code)) {
                val word = match.value
                val isInsideComment = isRangeOverlapping(match.range, commentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, blockCommentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, stringRegex.findAll(code))

                if (!isInsideComment) {
                    when {
                        KEYWORDS.contains(word) -> {
                            addStyle(
                                SpanStyle(
                                    color = theme.keywordColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                match.range.first,
                                match.range.last + 1
                            )
                        }
                        PROPERTIES.contains(word) -> {
                            addStyle(
                                SpanStyle(
                                    color = theme.propertyColor,
                                    fontWeight = FontWeight.Medium
                                ),
                                match.range.first,
                                match.range.last + 1
                            )
                        }
                    }
                }
            }

            // 4. Numbers and dimensions like 7px, 8px, 10
            val numberRegex = Regex("""\b\d+(px|dp|pt)?\b""")
            for (match in numberRegex.findAll(code)) {
                val isInsideComment = isRangeOverlapping(match.range, commentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, blockCommentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, stringRegex.findAll(code))

                if (!isInsideComment) {
                    addStyle(
                        SpanStyle(
                            color = theme.numberColor,
                            fontWeight = FontWeight.Bold
                        ),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }

            // 5. Punctuation brackets and operators: { } ( ) = ,
            val punctRegex = Regex("""[{}\(\)=,]""")
            for (match in punctRegex.findAll(code)) {
                val isInsideComment = isRangeOverlapping(match.range, commentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, blockCommentRegex.findAll(code)) ||
                        isRangeOverlapping(match.range, stringRegex.findAll(code))

                if (!isInsideComment) {
                    addStyle(
                        SpanStyle(color = theme.punctuationColor),
                        match.range.first,
                        match.range.last + 1
                    )
                }
            }
        }
    }

    private fun isRangeOverlapping(target: IntRange, sequences: Sequence<MatchResult>): Boolean {
        for (m in sequences) {
            if (target.first >= m.range.first && target.last <= m.range.last) {
                return true
            }
        }
        return false
    }
}
