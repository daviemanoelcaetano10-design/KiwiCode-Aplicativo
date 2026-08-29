package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Color theme palette configuration for the code editor and syntax highlighter.
 */
data class EditorTheme(
    val name: String,
    val background: Color,
    val gutterBackground: Color,
    val lineNumberColor: Color,
    val cursorColor: Color,
    val selectionColor: Color,
    val defaultTextColor: Color,
    val keywordColor: Color,
    val propertyColor: Color,
    val stringColor: Color,
    val numberColor: Color,
    val commentColor: Color,
    val colorLiteralBadgeColor: Color,
    val punctuationColor: Color
) {
    companion object {
        val KiwiDark = EditorTheme(
            name = "Kiwi High Density (Padrão)",
            background = Color(0xFF0D0D0D),
            gutterBackground = Color(0xFF101114),
            lineNumberColor = Color(0xFF52525B),
            cursorColor = Color(0xFF4ADE80),
            selectionColor = Color(0xFF1E3A8A),
            defaultTextColor = Color(0xFFE4E4E7),
            keywordColor = Color(0xFFC084FC),
            propertyColor = Color(0xFF93C5FD),
            stringColor = Color(0xFFFDBA74),
            numberColor = Color(0xFFFDE047),
            commentColor = Color(0xFF71717A),
            colorLiteralBadgeColor = Color(0xFF86EFAC),
            punctuationColor = Color(0xFFD4D4D8)
        )

        val Monokai = EditorTheme(
            name = "Monokai Pro",
            background = Color(0xFF1E1F29),
            gutterBackground = Color(0xFF282A36),
            lineNumberColor = Color(0xFF6272A4),
            cursorColor = Color(0xFFF8F8F0),
            selectionColor = Color(0xFF44475A),
            defaultTextColor = Color(0xFFF8F8F2),
            keywordColor = Color(0xFFFF6188),
            propertyColor = Color(0xFF78DCE8),
            stringColor = Color(0xFFA9DC76),
            numberColor = Color(0xFFFC9867),
            commentColor = Color(0xFF727072),
            colorLiteralBadgeColor = Color(0xFFFFD866),
            punctuationColor = Color(0xFFE2E2DC)
        )

        val Dracula = EditorTheme(
            name = "Dracula",
            background = Color(0xFF282A36),
            gutterBackground = Color(0xFF21222C),
            lineNumberColor = Color(0xFF6272A4),
            cursorColor = Color(0xFFA77BCA),
            selectionColor = Color(0xFF44475A),
            defaultTextColor = Color(0xFFF8F8F2),
            keywordColor = Color(0xFFFF79C6),
            propertyColor = Color(0xFF50FA7B),
            stringColor = Color(0xFFF1FA8C),
            numberColor = Color(0xFFBD93F9),
            commentColor = Color(0xFF6272A4),
            colorLiteralBadgeColor = Color(0xFFFF5555),
            punctuationColor = Color(0xFF8BE9FD)
        )

        val Cyberpunk = EditorTheme(
            name = "Cyberpunk 2077",
            background = Color(0xFF0F111A),
            gutterBackground = Color(0xFF090B10),
            lineNumberColor = Color(0xFF4C566A),
            cursorColor = Color(0xFF00E5FF),
            selectionColor = Color(0xFF00695C),
            defaultTextColor = Color(0xFFECEFF4),
            keywordColor = Color(0xFFFF007F),
            propertyColor = Color(0xFF00E5FF),
            stringColor = Color(0xFFFFD600),
            numberColor = Color(0xFFFF9100),
            commentColor = Color(0xFF546E7A),
            colorLiteralBadgeColor = Color(0xFF00E676),
            punctuationColor = Color(0xFFB0BEC5)
        )

        val themes = listOf(KiwiDark, Monokai, Dracula, Cyberpunk)
    }
}
