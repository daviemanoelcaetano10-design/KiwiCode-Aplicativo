package com.example.model

/**
 * Base sealed interface for all KiwiCode visual UI components.
 */
sealed interface KiwiComponent {
    val line: Int
}

/**
 * Toolbar component representing the top bar of the preview app.
 *
 * Example:
 * toolbar {
 *     title="My App"
 *     background-color="#FF0000"
 * }
 */
data class ToolbarComponent(
    val title: String = "My App",
    val backgroundColor: String = "#FF0000",
    override val line: Int = 1
) : KiwiComponent

/**
 * Single item inside a grid or list container.
 *
 * Example:
 * item(icon="/images/biblioteca/rock-image.png" title="rock music")
 */
data class ItemComponent(
    val icon: String = "",
    val title: String = "Item",
    val subtitle: String? = null,
    val backgroundColor: String? = null,
    override val line: Int = 1
) : KiwiComponent

/**
 * Grid container wrapping multiple items with customizable padding.
 *
 * Example:
 * grid-container, padding="7px" {
 *     item(icon="/images/biblioteca/rock-image.png" title="rock music")
 *     item(icon="/images/biblioteca/relaxe-image.png" title="relaxe music")
 * }
 */
data class GridContainerComponent(
    val padding: Int = 7,
    val items: List<ItemComponent> = emptyList(),
    override val line: Int = 1
) : KiwiComponent

/**
 * Represents the complete parsed KiwiCode document/program.
 */
data class KiwiProgram(
    val toolbar: ToolbarComponent? = null,
    val gridContainers: List<GridContainerComponent> = emptyList(),
    val rawComponents: List<KiwiComponent> = emptyList()
)

/**
 * Result of parsing KiwiCode source code.
 */
sealed interface KiwiParseResult {
    data class Success(
        val program: KiwiProgram,
        val warnings: List<String> = emptyList()
    ) : KiwiParseResult

    data class Error(
        val line: Int,
        val column: Int = 1,
        val message: String
    ) : KiwiParseResult
}
