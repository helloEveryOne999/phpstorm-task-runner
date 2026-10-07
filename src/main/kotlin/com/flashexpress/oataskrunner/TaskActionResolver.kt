package com.flashexpress.oataskrunner

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.Method
import java.nio.file.Path

data class ResolvedTaskAction(
    val command: TaskCommand,
    val file: VirtualFile,
)

class TaskActionResolver(
    private val commandParser: TaskCommandParser = TaskCommandParser(),
) {
    fun resolve(event: AnActionEvent): ResolvedTaskAction? {
        val project = event.project ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val method = PsiTreeUtil.getParentOfType(element, Method::class.java, false) ?: return null
        val phpClass = method.containingClass ?: return null
        val virtualFile = psiFile.virtualFile ?: return null
        val relativePath = relativePath(project, virtualFile) ?: return null

        val command = commandParser.resolve(
            TaskActionMetadata(
                relativePath = relativePath,
                className = phpClass.name,
                methodName = method.name,
                docComment = method.docComment?.text,
            ),
        ) ?: return null

        return ResolvedTaskAction(command, virtualFile)
    }

    private fun relativePath(project: Project, file: VirtualFile): String? {
        val basePath = project.basePath ?: return null
        val projectPath = Path.of(basePath).normalize()
        val filePath = Path.of(file.path).normalize()

        if (!filePath.startsWith(projectPath)) {
            return null
        }

        return projectPath.relativize(filePath).toString().replace('\\', '/')
    }
}
