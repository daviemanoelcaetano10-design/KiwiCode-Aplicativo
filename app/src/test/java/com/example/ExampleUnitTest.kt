package com.example

import com.example.model.KiwiParseResult
import com.example.parser.KiwiParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun parse_kiwi_code_success() {
    val code = """
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

    val result = KiwiParser.parse(code)
    assertTrue("Parser should succeed", result is KiwiParseResult.Success)
    val program = (result as KiwiParseResult.Success).program
    assertEquals("My App", program.toolbar?.title)
    assertEquals("#FF0000", program.toolbar?.backgroundColor)
    assertEquals(1, program.gridContainers.size)
    assertEquals(7, program.gridContainers[0].padding)
    assertEquals(4, program.gridContainers[0].items.size)
    assertEquals("rock music", program.gridContainers[0].items[0].title)
  }

  @Test
  fun parse_kiwi_code_invalid_syntax() {
    val invalidCode = """
        toolbar {
            title="My App"
    """.trimIndent()

    val result = KiwiParser.parse(invalidCode)
    assertTrue("Parser should fail on unclosed block", result is KiwiParseResult.Error)
  }
}

