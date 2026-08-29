package com.example.model

import java.util.UUID

/**
 * Represents a KiwiCode workspace project with multiple files.
 */
data class KiwiProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val files: List<KiwiFile> = listOf(KiwiFile.defaultMainFile()),
    val activeFileId: String = files.firstOrNull()?.id ?: "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val activeFile: KiwiFile?
        get() = files.find { it.id == activeFileId } ?: files.firstOrNull()

    companion object {
        fun defaultProject(): KiwiProject {
            val mainFile = KiwiFile.defaultMainFile()
            return KiwiProject(
                id = "default_project_id",
                name = "Meu Projeto",
                files = listOf(mainFile),
                activeFileId = mainFile.id
            )
        }

        fun demoProject(): KiwiProject {
            val mainFile = KiwiFile(
                id = "demo_main_id",
                name = "main.kc",
                content = """
                    // KiwiCode - Demo Player
                    toolbar {
                        title="Kiwi Player"
                        background-color="#E91E63"
                    }

                    grid-container, padding="8px" {
                        item(icon="/images/biblioteca/rock-image.png" title="Hard Rock")
                        item(icon="/images/biblioteca/relaxe-image.png" title="Chill Vibes")
                        item(icon="/images/biblioteca/city-image.png" title="Urban Beats")
                        item(icon="/images/biblioteca/classic-image.png" title="Symphony")
                    }
                """.trimIndent()
            )
            val stylesFile = KiwiFile(
                id = "demo_styles_id",
                name = "components.kc",
                content = """
                    // Components definitions
                    toolbar {
                        title="Dark Studio"
                        background-color="#212121"
                    }

                    grid-container, padding="10px" {
                        item(icon="/images/biblioteca/city-image.png" title="Studio 1")
                        item(icon="/images/biblioteca/rock-image.png" title="Studio 2")
                    }
                """.trimIndent()
            )
            return KiwiProject(
                id = "demo_project_id",
                name = "Projeto Demo",
                files = listOf(mainFile, stylesFile),
                activeFileId = mainFile.id
            )
        }
    }
}
