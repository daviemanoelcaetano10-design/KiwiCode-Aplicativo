package com.example.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GridContainerComponent
import com.example.model.ItemComponent
import com.example.model.KiwiParseResult
import com.example.model.KiwiProgram
import com.example.model.ToolbarComponent

enum class PreviewOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Preview section containing the control bar and the live phone simulation canvas.
 */
@Composable
fun PreviewRenderer(
    parseResult: KiwiParseResult?,
    orientation: PreviewOrientation,
    isExpanded: Boolean,
    onOrientationChange: (PreviewOrientation) -> Unit,
    onToggleExpand: () -> Unit,
    onItemClick: (ItemComponent) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
            .testTag("preview_renderer_container")
    ) {
        // Preview Header Bar
        PreviewHeaderBar(
            orientation = orientation,
            isExpanded = isExpanded,
            onOrientationChange = onOrientationChange,
            onToggleExpand = onToggleExpand,
            parseResult = parseResult
        )

        // Live Error Banner if any
        AnimatedVisibility(
            visible = parseResult is KiwiParseResult.Error,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            if (parseResult is KiwiParseResult.Error) {
                Surface(
                    color = Color(0xFF381014),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(8.dp))
                        .testTag("error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Erro de Sintaxe",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Erro na linha ${parseResult.line}:",
                                color = Color(0xFFFF8A80),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = parseResult.message,
                                color = Color(0xFFFFCDD2),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Phone Device Simulation Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val program = (parseResult as? KiwiParseResult.Success)?.program ?: KiwiProgram(
                toolbar = ToolbarComponent(title = "My App", backgroundColor = "#FF0000"),
                gridContainers = listOf(
                    GridContainerComponent(
                        padding = 7,
                        items = listOf(
                            ItemComponent(icon = "/images/biblioteca/rock-image.png", title = "rock music"),
                            ItemComponent(icon = "/images/biblioteca/relaxe-image.png", title = "relaxe music"),
                            ItemComponent(icon = "/images/biblioteca/city-image.png", title = "New York music"),
                            ItemComponent(icon = "/images/biblioteca/classic-image.png", title = "classic music")
                        )
                    )
                )
            )

            PhoneDeviceFrame(
                orientation = orientation,
                program = program,
                onItemClick = onItemClick,
                onFabClick = onFabClick
            )
        }
    }
}

/**
 * Top control bar for the preview area.
 */
@Composable
private fun PreviewHeaderBar(
    orientation: PreviewOrientation,
    isExpanded: Boolean,
    onOrientationChange: (PreviewOrientation) -> Unit,
    onToggleExpand: () -> Unit,
    parseResult: KiwiParseResult?
) {
    Surface(
        color = Color(0xFF1E1F23),
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .testTag("preview_header_bar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // Title & Status Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            when (parseResult) {
                                is KiwiParseResult.Success -> Color(0xFF4ADE80)
                                is KiwiParseResult.Error -> Color(0xFFFF5252)
                                null -> Color(0xFFFDE047)
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PREVIEW",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (orientation == PreviewOrientation.PORTRAIT) "PORTRAIT" else "LANDSCAPE",
                    color = Color(0xFF71717A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }

            // Right Action Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Portrait toggle
                IconButton(
                    onClick = { onOrientationChange(PreviewOrientation.PORTRAIT) },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("btn_preview_portrait")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Modo celular portrait",
                        tint = if (orientation == PreviewOrientation.PORTRAIT) Color.White else Color(0xFF71717A),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Landscape toggle
                IconButton(
                    onClick = { onOrientationChange(PreviewOrientation.LANDSCAPE) },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("btn_preview_landscape")
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "Modo celular landscape",
                        tint = if (orientation == PreviewOrientation.LANDSCAPE) Color.White else Color(0xFF71717A),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Expand / Reduce button
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("btn_preview_expand")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isExpanded) "Reduzir preview" else "Expandir preview",
                        tint = if (isExpanded) Color(0xFF4ADE80) else Color(0xFF71717A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Realistic modern smartphone frame hosting the rendered KiwiCode UI.
 */
@Composable
private fun PhoneDeviceFrame(
    orientation: PreviewOrientation,
    program: KiwiProgram,
    onItemClick: (ItemComponent) -> Unit,
    onFabClick: () -> Unit
) {
    val isPortrait = orientation == PreviewOrientation.PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(if (isPortrait) 9f / 16f else 16f / 9f, matchHeightConstraintsFirst = true)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1E2129))
                .border(2.dp, Color(0xFF2C3240), RoundedCornerShape(28.dp))
                .padding(5.dp)
                .testTag("phone_device_screen")
        ) {
            // Inner Phone Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Simulated Android Status Bar
                    SimulatedStatusBar()

                    // Rendered KiwiCode App
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        RenderedKiwiApp(
                            program = program,
                            isPortrait = isPortrait,
                            onItemClick = onItemClick
                        )

                        // Floating Action Button (+) in bottom right
                        FloatingActionButton(
                            onClick = onFabClick,
                            containerColor = parseHexColor(program.toolbar?.backgroundColor ?: "#FF0000"),
                            contentColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(56.dp)
                                .testTag("preview_fab_add")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar item",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Android system status bar mock for high fidelity presentation.
 */
@Composable
private fun SimulatedStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color(0x99000000))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "9:41",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Camera pinhole in the center
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A0A0A))
                .border(0.5.dp, Color(0xFF2A2A2A), CircleShape)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.BatteryFull,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Renders the parsed KiwiCode elements: Toolbar, GridContainer, Items.
 */
@Composable
private fun RenderedKiwiApp(
    program: KiwiProgram,
    isPortrait: Boolean,
    onItemClick: (ItemComponent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        // 1. Toolbar Component
        val tb = program.toolbar ?: ToolbarComponent(title = "My App", backgroundColor = "#FF0000")
        val tbColor = parseHexColor(tb.backgroundColor)

        Surface(
            color = tbColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("preview_app_toolbar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tb.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = { /* menu action */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções do App",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 2. Grid Container with Item Cards
        val grid = program.gridContainers.firstOrNull() ?: GridContainerComponent(
            padding = 7,
            items = emptyList()
        )

        val columns = if (isPortrait) 2 else 3

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues((grid.padding.coerceIn(2, 32)).dp),
            horizontalArrangement = Arrangement.spacedBy((grid.padding.coerceIn(2, 32)).dp),
            verticalArrangement = Arrangement.spacedBy((grid.padding.coerceIn(2, 32)).dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("preview_grid_container")
        ) {
            items(grid.items) { item ->
                KiwiItemCard(
                    item = item,
                    toolbarColor = tbColor,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

/**
 * High-fidelity card representing an item() in KiwiCode.
 * Features:
 * - Red background (or matching toolbar color/custom bg)
 * - Generously rounded outer corners (26.dp)
 * - Top image with rounded corners
 * - Centered bold white title
 */
@Composable
private fun KiwiItemCard(
    item: ItemComponent,
    toolbarColor: Color,
    onClick: () -> Unit
) {
    val cardBackground = item.backgroundColor?.let { parseHexColor(it) } ?: toolbarColor

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
            .testTag("card_${item.title.replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                RenderItemImage(iconPath = item.icon, title = item.title)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // White Centered Title
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Optional Subtitle
            if (!item.subtitle.isNullOrBlank()) {
                Text(
                    text = item.subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

/**
 * Resolves local image drawables or beautiful vector fallbacks for items.
 */
@Composable
private fun RenderItemImage(iconPath: String, title: String) {
    val lower = (iconPath + " " + title).lowercase()

    val drawableRes = when {
        lower.contains("rock") -> R.drawable.rock_image
        lower.contains("relaxe") || lower.contains("relax") || lower.contains("beach") -> R.drawable.relaxe_image
        lower.contains("city") || lower.contains("york") || lower.contains("urban") -> R.drawable.city_image
        lower.contains("classic") || lower.contains("piano") || lower.contains("violin") -> R.drawable.classic_image
        else -> null
    }

    if (drawableRes != null) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Dynamic decorative fallback with gradient and music icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2C3E50),
                            Color(0xFF4CA1AF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Helper to safely parse color strings like #FF0000, #E91E63, #6200EE, etc.
 */
fun parseHexColor(colorStr: String): Color {
    val clean = colorStr.trim().removePrefix("#")
    return try {
        when (clean.length) {
            3 -> {
                // #RGB -> #RRGGBB
                val r = clean[0].toString().repeat(2).toInt(16)
                val g = clean[1].toString().repeat(2).toInt(16)
                val b = clean[2].toString().repeat(2).toInt(16)
                Color(r, g, b)
            }
            6 -> {
                val colorInt = clean.toLong(16)
                Color((0xFF000000 or colorInt).toInt())
            }
            8 -> {
                val colorInt = clean.toLong(16)
                Color(colorInt.toInt())
            }
            else -> Color(0xFFD32F2F) // default red
        }
    } catch (e: Exception) {
        Color(0xFFD32F2F)
    }
}
