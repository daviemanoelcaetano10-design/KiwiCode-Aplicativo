package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.KiwiFile
import com.example.model.KiwiProject
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local persistence repository for KiwiCode projects and source files.
 */
class ProjectRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("kiwicode_projects", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROJECTS = "saved_projects"
        private const val KEY_ACTIVE_PROJECT_ID = "active_project_id"
    }

    init {
        // Initialize default and demo projects if first launch
        if (!prefs.contains(KEY_PROJECTS)) {
            val defaultList = listOf(KiwiProject.defaultProject(), KiwiProject.demoProject())
            saveAllProjects(defaultList)
            setActiveProjectId(defaultList.first().id)
        }
    }

    fun getAllProjects(): List<KiwiProject> {
        val jsonString = prefs.getString(KEY_PROJECTS, null) ?: return listOf(KiwiProject.defaultProject())
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<KiwiProject>()
            for (i in 0 until jsonArray.length()) {
                val projObj = jsonArray.getJSONObject(i)
                val id = projObj.optString("id", "")
                val name = projObj.optString("name", "Sem Título")
                val activeFileId = projObj.optString("activeFileId", "")
                val createdAt = projObj.optLong("createdAt", System.currentTimeMillis())
                val updatedAt = projObj.optLong("updatedAt", System.currentTimeMillis())

                val filesArray = projObj.optJSONArray("files") ?: JSONArray()
                val files = mutableListOf<KiwiFile>()
                for (j in 0 until filesArray.length()) {
                    val fileObj = filesArray.getJSONObject(j)
                    files.add(
                        KiwiFile(
                            id = fileObj.optString("id", ""),
                            name = fileObj.optString("name", "file.kc"),
                            content = fileObj.optString("content", ""),
                            isModified = fileObj.optBoolean("isModified", false)
                        )
                    )
                }

                val finalFiles = if (files.isEmpty()) listOf(KiwiFile.defaultMainFile()) else files
                val finalActiveFileId = if (finalFiles.any { it.id == activeFileId }) activeFileId else finalFiles.first().id

                list.add(
                    KiwiProject(
                        id = id,
                        name = name,
                        files = finalFiles,
                        activeFileId = finalActiveFileId,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }
            if (list.isEmpty()) listOf(KiwiProject.defaultProject()) else list
        } catch (e: Exception) {
            listOf(KiwiProject.defaultProject())
        }
    }

    fun getActiveProjectId(): String {
        return prefs.getString(KEY_ACTIVE_PROJECT_ID, null) ?: getAllProjects().first().id
    }

    fun setActiveProjectId(projectId: String) {
        prefs.edit().putString(KEY_ACTIVE_PROJECT_ID, projectId).apply()
    }

    fun getActiveProject(): KiwiProject {
        val projects = getAllProjects()
        val activeId = getActiveProjectId()
        return projects.find { it.id == activeId } ?: projects.firstOrNull() ?: KiwiProject.defaultProject()
    }

    fun saveProject(project: KiwiProject) {
        val projects = getAllProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == project.id }
        val updatedProject = project.copy(updatedAt = System.currentTimeMillis())
        if (index >= 0) {
            projects[index] = updatedProject
        } else {
            projects.add(updatedProject)
        }
        saveAllProjects(projects)
        setActiveProjectId(updatedProject.id)
    }

    fun createProject(name: String, initialTemplate: String? = null): KiwiProject {
        val mainFile = KiwiFile(
            name = "main.kc",
            content = initialTemplate ?: KiwiFile.defaultMainFile().content
        )
        val newProj = KiwiProject(
            name = name,
            files = listOf(mainFile),
            activeFileId = mainFile.id
        )
        saveProject(newProj)
        return newProj
    }

    fun deleteProject(projectId: String): Boolean {
        val projects = getAllProjects().toMutableList()
        if (projects.size <= 1) {
            // Don't delete last project
            return false
        }
        val removed = projects.removeAll { it.id == projectId }
        if (removed) {
            saveAllProjects(projects)
            if (getActiveProjectId() == projectId) {
                setActiveProjectId(projects.first().id)
            }
        }
        return removed
    }

    private fun saveAllProjects(projects: List<KiwiProject>) {
        val jsonArray = JSONArray()
        for (p in projects) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("name", p.name)
            pObj.put("activeFileId", p.activeFileId)
            pObj.put("createdAt", p.createdAt)
            pObj.put("updatedAt", p.updatedAt)

            val fArray = JSONArray()
            for (f in p.files) {
                val fObj = JSONObject()
                fObj.put("id", f.id)
                fObj.put("name", f.name)
                fObj.put("content", f.content)
                fObj.put("isModified", f.isModified)
                fArray.put(fObj)
            }
            pObj.put("files", fArray)
            jsonArray.put(pObj)
        }
        prefs.edit().putString(KEY_PROJECTS, jsonArray.toString()).apply()
    }
}
