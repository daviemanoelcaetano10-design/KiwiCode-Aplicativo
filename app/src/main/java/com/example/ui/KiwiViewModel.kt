package com.example.ui

import android.app.Application
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProjectRepository
import com.example.model.EditorTheme
import com.example.model.KiwiFile
import com.example.model.KiwiParseResult
import com.example.model.KiwiProject
import com.example.parser.KiwiParser
import com.example.ui.preview.PreviewOrientation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KiwiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository(application)

    private val _projects = MutableStateFlow<List<KiwiProject>>(emptyList())
    val projects: StateFlow<List<KiwiProject>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<KiwiProject>(KiwiProject.defaultProject())
    val currentProject: StateFlow<KiwiProject> = _currentProject.asStateFlow()

    private val _activeFile = MutableStateFlow<KiwiFile?>(null)
    val activeFile: StateFlow<KiwiFile?> = _activeFile.asStateFlow()

    private val _codeTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val codeTextFieldValue: StateFlow<TextFieldValue> = _codeTextFieldValue.asStateFlow()

    private val _parseResult = MutableStateFlow<KiwiParseResult?>(null)
    val parseResult: StateFlow<KiwiParseResult?> = _parseResult.asStateFlow()

    private val _orientation = MutableStateFlow(PreviewOrientation.PORTRAIT)
    val orientation: StateFlow<PreviewOrientation> = _orientation.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean> = _isExpanded.asStateFlow()

    private val _editorTheme = MutableStateFlow(EditorTheme.KiwiDark)
    val editorTheme: StateFlow<EditorTheme> = _editorTheme.asStateFlow()

    private val _autoRunEnabled = MutableStateFlow(false)
    val autoRunEnabled: StateFlow<Boolean> = _autoRunEnabled.asStateFlow()

    private val _fontSizeSp = MutableStateFlow(13.5f)
    val fontSizeSp: StateFlow<Float> = _fontSizeSp.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadInitialProject()
    }

    private fun loadInitialProject() {
        val all = repository.getAllProjects()
        _projects.value = all
        val active = repository.getActiveProject()
        _currentProject.value = active
        val file = active.activeFile ?: active.files.firstOrNull() ?: KiwiFile.defaultMainFile()
        _activeFile.value = file
        _codeTextFieldValue.value = TextFieldValue(file.content, TextRange(file.content.length))

        // Initial compile of the code to show the preview immediately
        executeCode()
    }

    fun onCodeChange(newValue: TextFieldValue) {
        _codeTextFieldValue.value = newValue

        val currentF = _activeFile.value ?: return
        val updatedF = currentF.copy(content = newValue.text, isModified = true)
        _activeFile.value = updatedF

        // Update in project
        val curProj = _currentProject.value
        val updatedFiles = curProj.files.map { if (it.id == updatedF.id) updatedF else it }
        _currentProject.value = curProj.copy(files = updatedFiles)

        if (_autoRunEnabled.value) {
            executeCode()
        }
    }

    fun executeCode() {
        val code = _codeTextFieldValue.value.text
        val result = KiwiParser.parse(code)
        _parseResult.value = result

        when (result) {
            is KiwiParseResult.Success -> {
                _snackbarMessage.value = "Código executado com sucesso!"
            }
            is KiwiParseResult.Error -> {
                _snackbarMessage.value = "Erro na linha ${result.line}: ${result.message}"
            }
        }
    }

    fun saveCurrentProject() {
        val proj = _currentProject.value
        // mark all files as saved
        val savedFiles = proj.files.map { it.copy(isModified = false) }
        val savedProj = proj.copy(files = savedFiles)
        _currentProject.value = savedProj
        _activeFile.value = savedProj.activeFile

        repository.saveProject(savedProj)
        _projects.value = repository.getAllProjects()
        _snackbarMessage.value = "Projeto '${savedProj.name}' salvo com sucesso!"
    }

    fun selectFile(file: KiwiFile) {
        val currentF = _activeFile.value
        if (currentF?.id == file.id) return

        _activeFile.value = file
        _codeTextFieldValue.value = TextFieldValue(file.content, TextRange(file.content.length))

        val curProj = _currentProject.value
        val updated = curProj.copy(activeFileId = file.id)
        _currentProject.value = updated
        repository.saveProject(updated)

        executeCode()
    }

    fun addNewFile(fileName: String) {
        val curProj = _currentProject.value
        val newFile = KiwiFile(
            name = fileName,
            content = """
                // Arquivo $fileName
                toolbar {
                    title="Nova Tela"
                    background-color="#3F51B5"
                }

                grid-container, padding="8px" {
                    item(icon="/images/biblioteca/rock-image.png" title="Item 1")
                    item(icon="/images/biblioteca/city-image.png" title="Item 2")
                }
            """.trimIndent(),
            isModified = false
        )
        val updatedFiles = curProj.files + newFile
        val updatedProj = curProj.copy(files = updatedFiles, activeFileId = newFile.id)
        _currentProject.value = updatedProj
        _activeFile.value = newFile
        _codeTextFieldValue.value = TextFieldValue(newFile.content, TextRange(newFile.content.length))

        repository.saveProject(updatedProj)
        _projects.value = repository.getAllProjects()
        executeCode()
        _snackbarMessage.value = "Arquivo '$fileName' criado!"
    }

    fun closeFile(file: KiwiFile) {
        val curProj = _currentProject.value
        if (curProj.files.size <= 1) {
            _snackbarMessage.value = "Não é possível fechar o único arquivo do projeto."
            return
        }

        val updatedFiles = curProj.files.filter { it.id != file.id }
        val nextActive = if (curProj.activeFileId == file.id) {
            updatedFiles.first()
        } else {
            updatedFiles.find { it.id == curProj.activeFileId } ?: updatedFiles.first()
        }

        val updatedProj = curProj.copy(files = updatedFiles, activeFileId = nextActive.id)
        _currentProject.value = updatedProj
        _activeFile.value = nextActive
        _codeTextFieldValue.value = TextFieldValue(nextActive.content, TextRange(nextActive.content.length))

        repository.saveProject(updatedProj)
        _projects.value = repository.getAllProjects()
        executeCode()
    }

    fun createNewProject(name: String, templateCode: String) {
        val newProj = repository.createProject(name, templateCode)
        _projects.value = repository.getAllProjects()
        openProject(newProj)
        _snackbarMessage.value = "Projeto '$name' criado!"
    }

    fun openProject(project: KiwiProject) {
        repository.setActiveProjectId(project.id)
        _currentProject.value = project
        val file = project.activeFile ?: project.files.firstOrNull() ?: KiwiFile.defaultMainFile()
        _activeFile.value = file
        _codeTextFieldValue.value = TextFieldValue(file.content, TextRange(file.content.length))
        executeCode()
        _snackbarMessage.value = "Projeto '${project.name}' aberto!"
    }

    fun deleteProject(project: KiwiProject) {
        if (repository.deleteProject(project.id)) {
            _projects.value = repository.getAllProjects()
            val active = repository.getActiveProject()
            openProject(active)
            _snackbarMessage.value = "Projeto '${project.name}' excluído."
        } else {
            _snackbarMessage.value = "Não é possível excluir o único projeto."
        }
    }

    fun insertSnippet(snippet: String) {
        val currentTF = _codeTextFieldValue.value
        val text = currentTF.text
        val selection = currentTF.selection

        val newText = text.replaceRange(selection.min, selection.max, snippet)
        val newCursorPos = selection.min + snippet.length
        val newTF = TextFieldValue(newText, TextRange(newCursorPos))

        onCodeChange(newTF)
    }

    fun setOrientation(newOrientation: PreviewOrientation) {
        _orientation.value = newOrientation
    }

    fun toggleExpandPreview() {
        _isExpanded.update { !it }
    }

    fun setEditorTheme(theme: EditorTheme) {
        _editorTheme.value = theme
    }

    fun setAutoRun(enabled: Boolean) {
        _autoRunEnabled.value = enabled
        if (enabled) {
            executeCode()
        }
    }

    fun setFontSize(size: Float) {
        _fontSizeSp.value = size
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }
}
