package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KiwiProject
import com.example.ui.dialogs.AboutDialog
import com.example.ui.dialogs.NewFileDialog
import com.example.ui.dialogs.NewProjectDialog
import com.example.ui.dialogs.OpenProjectDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.ThemeDialog
import com.example.ui.editor.CodeEditor
import com.example.ui.editor.EditorTabs
import com.example.ui.preview.PreviewRenderer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiwiCodeScreen(
    viewModel: KiwiViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val projects by viewModel.projects.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val codeTextFieldValue by viewModel.codeTextFieldValue.collectAsState()
    val parseResult by viewModel.parseResult.collectAsState()
    val orientation by viewModel.orientation.collectAsState()
    val isExpanded by viewModel.isExpanded.collectAsState()
    val editorTheme by viewModel.editorTheme.collectAsState()
    val autoRunEnabled by viewModel.autoRunEnabled.collectAsState()
    val fontSizeSp by viewModel.fontSizeSp.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    // Dialogs state
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showOpenProjectDialog by remember { mutableStateOf(false) }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF151619),
                drawerContentColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                KiwiNavigationDrawerContent(
                    projects = projects,
                    activeProjectId = currentProject.id,
                    onSelectProject = { proj ->
                        viewModel.openProject(proj)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onNewProject = {
                        coroutineScope.launch { drawerState.close() }
                        showNewProjectDialog = true
                    },
                    onImportProject = {
                        coroutineScope.launch { drawerState.close() }
                        showOpenProjectDialog = true
                    },
                    onSettings = {
                        coroutineScope.launch { drawerState.close() }
                        showSettingsDialog = true
                    },
                    onAbout = {
                        coroutineScope.launch { drawerState.close() }
                        showAboutDialog = true
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF4ADE80)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = Color(0xFF101114),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "KiwiCode",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentProject.name,
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("btn_navigation_drawer")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Lateral",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            // Run button (▶)
                            IconButton(
                                onClick = { viewModel.executeCode() },
                                modifier = Modifier.testTag("btn_toolbar_run")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Executar Código",
                                    tint = Color(0xFF4ADE80),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Save button (💾 floppy disk icon)
                            IconButton(
                                onClick = { viewModel.saveCurrentProject() },
                                modifier = Modifier.testTag("btn_toolbar_save")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Salvar Projeto",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Overflow menu (⋮)
                            Box {
                                IconButton(
                                    onClick = { showOverflowMenu = true },
                                    modifier = Modifier.testTag("btn_toolbar_overflow")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Menu de Opções",
                                        tint = Color.White
                                    )
                                }

                                DropdownMenu(
                                    expanded = showOverflowMenu,
                                    onDismissRequest = { showOverflowMenu = false },
                                    modifier = Modifier
                                        .background(Color(0xFF1E1F23))
                                        .testTag("toolbar_dropdown_menu")
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Novo Projeto", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4ADE80))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            showNewProjectDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_item_new_project")
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Abrir Projeto", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF60A5FA))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            showOpenProjectDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_item_open_project")
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Salvar", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFFFDE047))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            viewModel.saveCurrentProject()
                                        },
                                        modifier = Modifier.testTag("menu_item_save")
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Configurações", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF9CA3AF))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            showSettingsDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_item_settings")
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Tema", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFFC084FC))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            showThemeDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_item_theme")
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Sobre o KiwiCode", color = Color.White) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4ADE80))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            showAboutDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_item_about")
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF101114),
                            navigationIconContentColor = Color.White,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                        modifier = Modifier.testTag("main_toolbar")
                    )
                    HorizontalDivider(color = Color(0xFF27272A), thickness = 1.dp)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color(0xFF101114),
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isLandscapeScreen = maxWidth > maxHeight

                if (isLandscapeScreen) {
                    // Landscape adaptive layout: Side-by-side Editor and Preview
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Pane: Tabs and Code Editor
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                        ) {
                            EditorTabs(
                                files = currentProject.files,
                                activeFileId = currentProject.activeFileId,
                                onTabSelected = { viewModel.selectFile(it) },
                                onCloseTab = { viewModel.closeFile(it) },
                                onAddNewFileClick = { showNewFileDialog = true }
                            )

                            CodeEditor(
                                textFieldValue = codeTextFieldValue,
                                onValueChange = { viewModel.onCodeChange(it) },
                                theme = editorTheme,
                                parseResult = parseResult,
                                onSnippetClick = { viewModel.insertSnippet(it) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF27272A))
                        )

                        // Right Pane: Preview Section
                        PreviewRenderer(
                            parseResult = parseResult,
                            orientation = orientation,
                            isExpanded = isExpanded,
                            onOrientationChange = { viewModel.setOrientation(it) },
                            onToggleExpand = { viewModel.toggleExpandPreview() },
                            onItemClick = { item ->
                                viewModel.showMessage("Card clicado: ${item.title}")
                            },
                            onFabClick = {
                                viewModel.showMessage("Botão flutuante (+) clicado")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    // Portrait screen layout: Editor on top, Preview below
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (!isExpanded) {
                            // Editor Area
                            Column(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .fillMaxWidth()
                            ) {
                                EditorTabs(
                                    files = currentProject.files,
                                    activeFileId = currentProject.activeFileId,
                                    onTabSelected = { viewModel.selectFile(it) },
                                    onCloseTab = { viewModel.closeFile(it) },
                                    onAddNewFileClick = { showNewFileDialog = true }
                                )

                                CodeEditor(
                                    textFieldValue = codeTextFieldValue,
                                    onValueChange = { viewModel.onCodeChange(it) },
                                    theme = editorTheme,
                                    parseResult = parseResult,
                                    onSnippetClick = { viewModel.insertSnippet(it) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Horizontal Divider
                            HorizontalDivider(color = Color(0xFF27272A), thickness = 1.dp)
                        }

                        // Preview Area (takes full height if expanded)
                        PreviewRenderer(
                            parseResult = parseResult,
                            orientation = orientation,
                            isExpanded = isExpanded,
                            onOrientationChange = { viewModel.setOrientation(it) },
                            onToggleExpand = { viewModel.toggleExpandPreview() },
                            onItemClick = { item ->
                                viewModel.showMessage("Card clicado: ${item.title}")
                            },
                            onFabClick = {
                                viewModel.showMessage("Botão flutuante (+) clicado")
                            },
                            modifier = if (isExpanded) {
                                Modifier.fillMaxSize()
                            } else {
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog instances
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { name, template ->
                showNewProjectDialog = false
                viewModel.createNewProject(name, template)
            }
        )
    }

    if (showOpenProjectDialog) {
        OpenProjectDialog(
            projects = projects,
            activeProjectId = currentProject.id,
            onSelectProject = { proj ->
                showOpenProjectDialog = false
                viewModel.openProject(proj)
            },
            onDeleteProject = { proj ->
                viewModel.deleteProject(proj)
            },
            onDismiss = { showOpenProjectDialog = false }
        )
    }

    if (showNewFileDialog) {
        NewFileDialog(
            onDismiss = { showNewFileDialog = false },
            onCreate = { fileName ->
                showNewFileDialog = false
                viewModel.addNewFile(fileName)
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            autoRunEnabled = autoRunEnabled,
            onToggleAutoRun = { viewModel.setAutoRun(it) },
            fontSizeSp = fontSizeSp,
            onFontSizeChange = { viewModel.setFontSize(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = editorTheme,
            onSelectTheme = { viewModel.setEditorTheme(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

/**
 * Navigation Drawer contents with Projects list and App Settings.
 */
@Composable
private fun KiwiNavigationDrawerContent(
    projects: List<KiwiProject>,
    activeProjectId: String,
    onSelectProject: (KiwiProject) -> Unit,
    onNewProject: () -> Unit,
    onImportProject: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("navigation_drawer_content")
    ) {
        // Drawer Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color(0xFF101114),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "KiwiCode",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mobile IDE & Preview",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider(color = Color(0xFF27272A), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

        // Projects Section
        Text(
            text = "PROJETOS",
            color = Color(0xFF4ADE80),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

        for (proj in projects) {
            val isSelected = proj.id == activeProjectId
            NavigationDrawerItem(
                label = {
                    Text(
                        text = proj.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFF4ADE80) else Color(0xFF9CA3AF)
                    )
                },
                selected = isSelected,
                onClick = { onSelectProject(proj) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF1E1F23),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color(0xFF9CA3AF),
                    unselectedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        HorizontalDivider(color = Color(0xFF27272A), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

        // Actions & Options
        NavigationDrawerItem(
            label = { Text("Novo Projeto") },
            icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4ADE80)) },
            selected = false,
            onClick = onNewProject,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text("Importar Projeto") },
            icon = { Icon(Icons.Default.Upload, contentDescription = null, tint = Color(0xFF60A5FA)) },
            selected = false,
            onClick = onImportProject,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text("Configurações") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF9CA3AF)) },
            selected = false,
            onClick = onSettings,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text("Sobre") },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4ADE80)) },
            selected = false,
            onClick = onAbout,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}
