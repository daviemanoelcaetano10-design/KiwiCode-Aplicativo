package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.KiwiParseResult
import com.example.parser.KiwiParser
import com.example.ui.preview.PreviewOrientation
import com.example.ui.preview.PreviewRenderer
import com.example.ui.theme.KiwiCodeTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleCode = """
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
    val parseResult = KiwiParser.parse(sampleCode)

    composeTestRule.setContent {
      KiwiCodeTheme {
        PreviewRenderer(
          parseResult = parseResult,
          orientation = PreviewOrientation.PORTRAIT,
          isExpanded = false,
          onOrientationChange = {},
          onToggleExpand = {},
          onItemClick = {},
          onFabClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

