package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KiwiFile

/**
 * Editor top tab bar containing open .kc files and the "+" new file button.
 */
@Composable
fun EditorTabs(
    files: List<KiwiFile>,
    activeFileId: String,
    onTabSelected: (KiwiFile) -> Unit,
    onCloseTab: (KiwiFile) -> Unit,
    onAddNewFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        color = Color(0xFF151619),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag("editor_tab_bar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 2.dp, vertical = 2.dp)
        ) {
            files.forEach { file ->
                val isActive = file.id == activeFileId

                Box(
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(
                            if (isActive) Color(0xFF1E1F23) else Color(0xFF151619)
                        )
                        .clickable { onTabSelected(file) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("tab_${file.name}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // File Icon / Language badge
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = if (isActive) Color(0xFF60A5FA) else Color(0xFF71717A),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // File Name
                        Text(
                            text = file.name,
                            color = if (isActive) Color(0xFF60A5FA) else Color(0xFF9CA3AF),
                            fontSize = 12.5.sp,
                            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )

                        // Unsaved changes dot
                        if (file.isModified) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(Color(0xFFFDBA74))
                            )
                        }

                        // Close button (only if more than 1 file)
                        if (files.size > 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onCloseTab(file) }
                                    .padding(2.dp)
                                    .testTag("close_tab_${file.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar aba ${file.name}",
                                    tint = if (isActive) Color(0xFF9CA3AF) else Color(0xFF52525B),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }

                    // Active underline pill indicator
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color(0xFF60A5FA))
                        )
                    }
                }
            }

            // New file "+" button
            IconButton(
                onClick = onAddNewFileClick,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E1F23))
                    .testTag("add_new_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Novo Arquivo",
                    tint = Color(0xFF4ADE80),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
