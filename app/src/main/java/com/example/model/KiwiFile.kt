package com.example.model

import java.util.UUID

/**
 * Represents a single source code file in KiwiCode editor.
 */
data class KiwiFile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val content: String,
    val isModified: Boolean = false
) {
    companion object {
        fun defaultMainFile(): KiwiFile {
            val defaultCode = """
                // KiwiCode - Music Library App
                toolbar {
                    title="My App"
                    background-color="#FF0000"
                }

                grid-container, padding="7px" {
                    item(icon="/images/biblioteca/rock-image.png" title="rock music")
                    item(icon="/images/biblioteca/relaxe-image.png" title="relaxe music")
                    item(icon="/images/biblioteca/city-image.png" title="New York music")
                    item(icon="/images/biblioteca/classic-image.png" title="classic music")
                }
            """.trimIndent()
            return KiwiFile(
                id = "main_file_id",
                name = "main.kc",
                content = defaultCode,
                isModified = false
            )
        }
    }
}
