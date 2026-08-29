package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorTheme
import com.example.model.KiwiProject

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf("music") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Novo Projeto KiwiCode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Digite o nome do seu novo projeto:", color = Color(0xFFB0BEC5), fontSize = 13.5.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    placeholder = { Text("Ex: Meu Novo App", color = Color(0xFF607D8B)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF69F0AE),
                        unfocusedBorderColor = Color(0xFF37474F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF69F0AE)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_new_project_name")
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Modelo inicial:", color = Color(0xFFB0BEC5), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TemplateChip(
                        title = "Music App",
                        selected = selectedTemplate == "music",
                        onClick = { selectedTemplate = "music" },
                        modifier = Modifier.weight(1f)
                    )
                    TemplateChip(
                        title = "Em Branco",
                        selected = selectedTemplate == "blank",
                        onClick = { selectedTemplate = "blank" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = projectName.trim().ifEmpty { "Novo Projeto" }
                    val templateCode = if (selectedTemplate == "music") {
                        """
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
                    } else {
                        """
                            toolbar {
                                title="$finalName"
                                background-color="#6200EE"
                            }

                            grid-container, padding="8px" {
                                item(icon="/images/biblioteca/rock-image.png" title="Item 1")
                                item(icon="/images/biblioteca/city-image.png" title="Item 2")
                            }
                        """.trimIndent()
                    }
                    onCreate(finalName, templateCode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69F0AE), contentColor = Color(0xFF101114)),
                modifier = Modifier.testTag("btn_confirm_new_project")
            ) {
                Text("Criar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF90A4AE))
            }
        }
    )
}

@Composable
private fun TemplateChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFF263238) else Color(0xFF13151A),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) Color(0xFF69F0AE) else Color(0xFF2E3846)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                color = if (selected) Color.White else Color(0xFFB0BEC5),
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun OpenProjectDialog(
    projects: List<KiwiProject>,
    activeProjectId: String,
    onSelectProject: (KiwiProject) -> Unit,
    onDeleteProject: (KiwiProject) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir Projeto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                items(projects) { proj ->
                    val isActive = proj.id == activeProjectId
                    Surface(
                        onClick = { onSelectProject(proj) },
                        color = if (isActive) Color(0xFF263238) else Color(0xFF151820),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (isActive) Color(0xFF69F0AE) else Color(0xFF242A36),
                                RoundedCornerShape(8.dp)
                            )
                            .testTag("project_item_${proj.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = proj.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${proj.files.size} arquivo(s) • ${proj.files.joinToString { it.name }}",
                                    color = Color(0xFF90A4AE),
                                    fontSize = 11.5.sp
                                )
                            }

                            if (projects.size > 1 && !isActive) {
                                IconButton(
                                    onClick = { onDeleteProject(proj) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir projeto",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
            ) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun NewFileDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Novo Arquivo KiwiCode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Nome do arquivo (extensão .kc será adicionada):", color = Color(0xFFB0BEC5), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    placeholder = { Text("Ex: styles, components", color = Color(0xFF607D8B)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF69F0AE),
                        unfocusedBorderColor = Color(0xFF37474F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_new_file_name")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var clean = fileName.trim()
                    if (clean.isEmpty()) clean = "novo"
                    if (!clean.endsWith(".kc")) clean = "$clean.kc"
                    onCreate(clean)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69F0AE), contentColor = Color(0xFF101114)),
                modifier = Modifier.testTag("btn_confirm_new_file")
            ) {
                Text("Criar Arquivo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF90A4AE))
            }
        }
    )
}

@Composable
fun SettingsDialog(
    autoRunEnabled: Boolean,
    onToggleAutoRun: (Boolean) -> Unit,
    fontSizeSp: Float,
    onFontSizeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configurações do IDE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Auto Run Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Executar Código", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Atualiza o preview em tempo real ao digitar", color = Color(0xFF90A4AE), fontSize = 12.sp)
                    }
                    Switch(
                        checked = autoRunEnabled,
                        onCheckedChange = onToggleAutoRun,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF69F0AE),
                            checkedTrackColor = Color(0xFF2E7D32)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Font Size Selector
                Text("Tamanho da Fonte do Editor", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(12f to "Pequeno (12)", 14f to "Normal (14)", 16f to "Grande (16)").forEach { (size, label) ->
                        val isSelected = fontSizeSp == size
                        Surface(
                            onClick = { onFontSizeChange(size) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF263238) else Color(0xFF13151A),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF69F0AE) else Color(0xFF2E3846)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFFB0BEC5),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69F0AE), contentColor = Color(0xFF101114))
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ThemeDialog(
    currentTheme: EditorTheme,
    onSelectTheme: (EditorTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tema do Editor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                EditorTheme.themes.forEach { theme ->
                    val isSelected = currentTheme.name == theme.name
                    Surface(
                        onClick = {
                            onSelectTheme(theme)
                            onDismiss()
                        },
                        color = if (isSelected) Color(0xFF263238) else Color(0xFF151820),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF69F0AE) else Color(0xFF242A36),
                                RoundedCornerShape(8.dp)
                            )
                            .testTag("theme_${theme.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(theme.keywordColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = theme.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color(0xFF90A4AE))
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2129),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF69F0AE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sobre o KiwiCode", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "KiwiCode IDE v1.0",
                    color = Color(0xFF69F0AE),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Um ambiente de desenvolvimento mobile moderno para a linguagem declarativa KiwiCode.",
                    color = Color(0xFFB0BEC5),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Guia de Sintaxe KiwiCode:",
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFF12141A),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "toolbar {\n  title=\"Meu App\"\n  background-color=\"#FF0000\"\n}\n\ngrid-container, padding=\"7px\" {\n  item(icon=\"...\" title=\"...\")\n}",
                        color = Color(0xFF69F0AE),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69F0AE), contentColor = Color(0xFF101114))
            ) {
                Text("Entendi", fontWeight = FontWeight.Bold)
            }
        }
    )
}
