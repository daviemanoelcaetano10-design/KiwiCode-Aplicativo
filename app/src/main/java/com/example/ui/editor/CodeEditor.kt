package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorTheme
import com.example.model.KiwiParseResult
import com.example.parser.KiwiHighlighter

/**
 * VisualTransformation that applies KiwiCode syntax highlighting to the text in real-time.
 */
class KiwiSyntaxVisualTransformation(
    private val theme: EditorTheme
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val highlighted = KiwiHighlighter.highlight(text.text, theme)
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}

/**
 * Production-ready Code Editor component with line numbers, syntax highlighting,
 * horizontal/vertical scrolling, error diagnostics, and quick syntax helpers.
 */
@Composable
fun CodeEditor(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    theme: EditorTheme,
    parseResult: KiwiParseResult?,
    onSnippetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val codeText = textFieldValue.text
    val lineCount = remember(codeText) {
        val count = codeText.count { it == '\n' } + 1
        maxOf(count, 1)
    }

    val visualTransformation = remember(theme) {
        KiwiSyntaxVisualTransformation(theme)
    }

    val errorLine = (parseResult as? KiwiParseResult.Error)?.line

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .testTag("code_editor_container")
    ) {
        // Main Editor Surface
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Line Numbers Gutter
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 38.dp)
                        .background(theme.gutterBackground)
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lineCount) {
                        val isErrorLine = errorLine == i
                        Box(
                            modifier = Modifier
                                .height(22.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (isErrorLine) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = "Erro na linha $i",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "$i",
                                        color = Color(0xFFFF5252),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                    )
                                }
                            } else {
                                Text(
                                    text = "$i",
                                    color = theme.lineNumberColor,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }

                // Code Input Area with Horizontal Scroll
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(horizontalScrollState)
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(min = 800.dp)
                            .testTag("code_input_field"),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.5.sp,
                            lineHeight = 22.sp,
                            color = theme.defaultTextColor
                        ),
                        cursorBrush = SolidColor(theme.cursorColor),
                        visualTransformation = visualTransformation
                    )
                }
            }
        }

        // Quick Syntax Snippet Helper Strip
        CodeSnippetHelperBar(
            onSnippetClick = onSnippetClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Handy quick snippet insertion strip for mobile typing ease.
 */
@Composable
fun CodeSnippetHelperBar(
    onSnippetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snippets = listOf(
        "toolbar {\n    title=\"My App\"\n    background-color=\"#FF0000\"\n}" to "toolbar",
        "grid-container, padding=\"7px\" {\n    \n}" to "grid-container",
        "item(icon=\"/images/biblioteca/rock-image.png\" title=\"rock music\")" to "item",
        "title=\"\"" to "title=",
        "background-color=\"#FF0000\"" to "background-color=",
        "icon=\"\"" to "icon=",
        "padding=\"7px\"" to "padding=",
        "{" to "{",
        "}" to "}",
        "(" to "(",
        ")" to ")",
        "\"" to "\"",
        "=" to "="
    )

    val scrollState = rememberScrollState()

    Surface(
        color = Color(0xFF101114),
        modifier = modifier
            .height(38.dp)
            .testTag("code_snippet_helper_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            snippets.forEach { (snippet, label) ->
                Surface(
                    onClick = { onSnippetClick(snippet) },
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1E1F23),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .testTag("snippet_$label")
                ) {
                    Text(
                        text = label,
                        color = Color(0xFFE4E4E7),
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
