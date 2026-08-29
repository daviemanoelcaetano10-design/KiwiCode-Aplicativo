package com.example.parser

import com.example.model.GridContainerComponent
import com.example.model.ItemComponent
import com.example.model.KiwiComponent
import com.example.model.KiwiParseResult
import com.example.model.KiwiProgram
import com.example.model.ToolbarComponent

/**
 * Parser for the KiwiCode domain specific declarative UI language.
 *
 * Example input:
 * toolbar {
 *     title="My App"
 *     background-color="#FF0000"
 * }
 *
 * grid-container, padding="7px" {
 *     item(icon="/images/biblioteca/rock-image.png" title="rock music")
 *     item(icon="/images/biblioteca/relaxe-image.png" title="relaxe music")
 * }
 */
object KiwiParser {

    fun parse(source: String): KiwiParseResult {
        if (source.isBlank()) {
            return KiwiParseResult.Success(KiwiProgram())
        }

        val lines = source.lines()
        var currentLineIndex = 0

        var toolbarComponent: ToolbarComponent? = null
        val gridContainers = mutableListOf<GridContainerComponent>()
        val rawComponents = mutableListOf<KiwiComponent>()
        val warnings = mutableListOf<String>()

        while (currentLineIndex < lines.size) {
            val rawLine = lines[currentLineIndex]
            val lineNumber = currentLineIndex + 1
            val trimmedLine = stripComments(rawLine).trim()

            if (trimmedLine.isEmpty()) {
                currentLineIndex++
                continue
            }

            when {
                // Toolbar block declaration
                trimmedLine.startsWith("toolbar") -> {
                    val blockResult = parseToolbarBlock(lines, currentLineIndex)
                    when (blockResult) {
                        is BlockParseResult.Success<*> -> {
                            val tb = blockResult.value as ToolbarComponent
                            toolbarComponent = tb
                            rawComponents.add(tb)
                            currentLineIndex = blockResult.nextLineIndex
                        }
                        is BlockParseResult.Error -> {
                            return KiwiParseResult.Error(
                                line = blockResult.line,
                                message = blockResult.message
                            )
                        }
                    }
                }

                // Grid Container block declaration
                trimmedLine.startsWith("grid-container") || trimmedLine.startsWith("grid_container") -> {
                    val blockResult = parseGridContainerBlock(lines, currentLineIndex)
                    when (blockResult) {
                        is BlockParseResult.Success<*> -> {
                            val gc = blockResult.value as GridContainerComponent
                            gridContainers.add(gc)
                            rawComponents.add(gc)
                            currentLineIndex = blockResult.nextLineIndex
                        }
                        is BlockParseResult.Error -> {
                            return KiwiParseResult.Error(
                                line = blockResult.line,
                                message = blockResult.message
                            )
                        }
                    }
                }

                // Standalone item outside grid (wrapped in a default grid container if found)
                trimmedLine.startsWith("item(") -> {
                    val itemResult = parseItemLine(trimmedLine, lineNumber)
                    when (itemResult) {
                        is ItemParseResult.Success -> {
                            val item = itemResult.item
                            val existing = gridContainers.lastOrNull()
                            if (existing != null) {
                                val updated = existing.copy(items = existing.items + item)
                                gridContainers[gridContainers.size - 1] = updated
                            } else {
                                val newGrid = GridContainerComponent(
                                    padding = 8,
                                    items = listOf(item),
                                    line = lineNumber
                                )
                                gridContainers.add(newGrid)
                                rawComponents.add(newGrid)
                            }
                            currentLineIndex++
                        }
                        is ItemParseResult.Error -> {
                            return KiwiParseResult.Error(
                                line = lineNumber,
                                message = itemResult.message
                            )
                        }
                    }
                }

                else -> {
                    return KiwiParseResult.Error(
                        line = lineNumber,
                        message = "Componente ou comando desconhecido '$trimmedLine'. Esperado: 'toolbar', 'grid-container' ou 'item'."
                    )
                }
            }
        }

        return KiwiParseResult.Success(
            program = KiwiProgram(
                toolbar = toolbarComponent,
                gridContainers = gridContainers,
                rawComponents = rawComponents
            ),
            warnings = warnings
        )
    }

    private fun parseToolbarBlock(lines: List<String>, startLineIndex: Int): BlockParseResult {
        val startLineNum = startLineIndex + 1
        var lineIndex = startLineIndex
        val header = stripComments(lines[lineIndex]).trim()

        var title = "My App"
        var backgroundColor = "#FF0000"

        var hasOpeningBrace = header.contains("{")
        if (!hasOpeningBrace) {
            lineIndex++
            while (lineIndex < lines.size) {
                val nextL = stripComments(lines[lineIndex]).trim()
                if (nextL.isEmpty()) {
                    lineIndex++
                    continue
                }
                if (nextL.startsWith("{")) {
                    hasOpeningBrace = true
                    break
                } else {
                    return BlockParseResult.Error(
                        line = lineIndex + 1,
                        message = "Esperado '{' após declaração de 'toolbar'."
                    )
                }
            }
        }

        if (!hasOpeningBrace) {
            return BlockParseResult.Error(
                line = startLineNum,
                message = "Bloco 'toolbar' não foi aberto com '{'."
            )
        }

        lineIndex++
        var closed = false

        while (lineIndex < lines.size) {
            val raw = lines[lineIndex]
            val lineNum = lineIndex + 1
            val clean = stripComments(raw).trim()

            if (clean.isEmpty()) {
                lineIndex++
                continue
            }

            if (clean.contains("}")) {
                closed = true
                lineIndex++
                break
            }

            // Parse properties: title="..." background-color="..."
            val props = extractProperties(clean, lineNum)
            if (props.isEmpty() && !clean.contains("=")) {
                return BlockParseResult.Error(
                    line = lineNum,
                    message = "Sintaxe inválida em 'toolbar'. Esperado: chave=\"valor\""
                )
            }

            for ((key, value) in props) {
                when (key.lowercase()) {
                    "title", "titulo" -> title = value
                    "background-color", "background_color", "background", "bgcolor", "color" -> backgroundColor = value
                    else -> {
                        // Tolerant or warning
                    }
                }
            }

            lineIndex++
        }

        if (!closed) {
            return BlockParseResult.Error(
                line = lines.size,
                message = "Bloco 'toolbar' iniciado na linha $startLineNum não foi fechado com '}'."
            )
        }

        return BlockParseResult.Success(
            value = ToolbarComponent(
                title = title,
                backgroundColor = backgroundColor,
                line = startLineNum
            ),
            nextLineIndex = lineIndex
        )
    }

    private fun parseGridContainerBlock(lines: List<String>, startLineIndex: Int): BlockParseResult {
        val startLineNum = startLineIndex + 1
        var lineIndex = startLineIndex
        val header = stripComments(lines[lineIndex]).trim()

        var padding = 7
        // Extract padding from header e.g. "grid-container, padding="7px" {"
        val headerProps = extractProperties(header, startLineNum)
        for ((key, value) in headerProps) {
            if (key.lowercase().contains("padding")) {
                val num = value.replace(Regex("[^0-9]"), "").toIntOrNull()
                if (num != null) padding = num
            }
        }

        var hasOpeningBrace = header.contains("{")
        if (!hasOpeningBrace) {
            lineIndex++
            while (lineIndex < lines.size) {
                val nextL = stripComments(lines[lineIndex]).trim()
                if (nextL.isEmpty()) {
                    lineIndex++
                    continue
                }
                if (nextL.startsWith("{")) {
                    hasOpeningBrace = true
                    break
                } else {
                    return BlockParseResult.Error(
                        line = lineIndex + 1,
                        message = "Esperado '{' após declaração de 'grid-container'."
                    )
                }
            }
        }

        if (!hasOpeningBrace) {
            return BlockParseResult.Error(
                line = startLineNum,
                message = "Bloco 'grid-container' não foi aberto com '{'."
            )
        }

        lineIndex++
        val items = mutableListOf<ItemComponent>()
        var closed = false

        while (lineIndex < lines.size) {
            val raw = lines[lineIndex]
            val lineNum = lineIndex + 1
            val clean = stripComments(raw).trim()

            if (clean.isEmpty()) {
                lineIndex++
                continue
            }

            if (clean.contains("}")) {
                closed = true
                lineIndex++
                break
            }

            if (clean.startsWith("item")) {
                val itemRes = parseItemLine(clean, lineNum)
                when (itemRes) {
                    is ItemParseResult.Success -> {
                        items.add(itemRes.item)
                    }
                    is ItemParseResult.Error -> {
                        return BlockParseResult.Error(
                            line = lineNum,
                            message = itemRes.message
                        )
                    }
                }
            } else {
                return BlockParseResult.Error(
                    line = lineNum,
                    message = "Elemento inválido dentro de 'grid-container'. Esperado 'item(...)'. Encontrado: '$clean'"
                )
            }

            lineIndex++
        }

        if (!closed) {
            return BlockParseResult.Error(
                line = lines.size,
                message = "Bloco 'grid-container' iniciado na linha $startLineNum não foi fechado com '}'."
            )
        }

        return BlockParseResult.Success(
            value = GridContainerComponent(
                padding = padding,
                items = items,
                line = startLineNum
            ),
            nextLineIndex = lineIndex
        )
    }

    private fun parseItemLine(line: String, lineNum: Int): ItemParseResult {
        // Expected format: item(icon="..." title="...")
        val openParen = line.indexOf('(')
        val closeParen = line.lastIndexOf(')')

        if (openParen == -1 || closeParen == -1 || closeParen < openParen) {
            return ItemParseResult.Error(
                line = lineNum,
                message = "Sintaxe incorreta para 'item'. Use: item(icon=\"...\" title=\"...\")"
            )
        }

        val inner = line.substring(openParen + 1, closeParen).trim()
        val props = extractProperties(inner, lineNum)

        val icon = props["icon"] ?: props["image"] ?: props["icone"] ?: ""
        val title = props["title"] ?: props["titulo"] ?: props["name"] ?: ""
        val subtitle = props["subtitle"] ?: props["subtitulo"]
        val backgroundColor = props["background-color"] ?: props["background"]

        if (title.isBlank() && icon.isBlank()) {
            return ItemParseResult.Error(
                line = lineNum,
                message = "Propriedade 'title' ou 'icon' não encontrada em item()."
            )
        }

        return ItemParseResult.Success(
            item = ItemComponent(
                icon = icon,
                title = if (title.isNotBlank()) title else "Item",
                subtitle = subtitle,
                backgroundColor = backgroundColor,
                line = lineNum
            )
        )
    }

    /**
     * Extracts key-value pairs from strings like:
     * title="My App" background-color="#FF0000" padding="7px"
     */
    fun extractProperties(text: String, lineNum: Int = 1): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val regex = Regex("""([a-zA-Z0-9_-]+)\s*=\s*"([^"]*)"""")
        val matches = regex.findAll(text)
        for (match in matches) {
            val key = match.groupValues[1]
            val value = match.groupValues[2]
            result[key] = value
        }
        return result
    }

    private fun stripComments(line: String): String {
        val commentIdx = line.indexOf("//")
        return if (commentIdx != -1) line.substring(0, commentIdx) else line
    }

    private sealed interface BlockParseResult {
        data class Success<T>(val value: T, val nextLineIndex: Int) : BlockParseResult
        data class Error(val line: Int, val message: String) : BlockParseResult
    }

    private sealed interface ItemParseResult {
        data class Success(val item: ItemComponent) : ItemParseResult
        data class Error(val line: Int, val message: String) : ItemParseResult
    }
}
