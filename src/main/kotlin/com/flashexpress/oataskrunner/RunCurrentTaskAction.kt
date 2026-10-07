package com.flashexpress.oataskrunner

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.io.IOException

class RunCurrentTaskAction : AnAction() {
    private val resolver = TaskActionResolver()
    private val terminalRunner = TerminalCommandRunner()

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = resolver.resolve(event) != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val resolvedAction = resolver.resolve(event)

        if (resolvedAction == null) {
            notify(project, "当前光标不在 app/tasks 下的 Task Action 方法内", NotificationType.WARNING)
            return
        }

        val dialog = TaskCommandDialog(project, resolvedAction.command)
        if (!dialog.showAndGet()) {
            return
        }

        event.getData(CommonDataKeys.EDITOR)?.document?.let {
            FileDocumentManager.getInstance().saveDocument(it)
        }

        try {
            terminalRunner.run(
                project,
                resolvedAction.command,
                dialog.commandLine,
                dialog.executionTarget,
            )
        } catch (exception: IOException) {
            notify(project, "无法启动终端命令：${exception.message}", NotificationType.ERROR)
        } catch (exception: IllegalStateException) {
            notify(project, exception.message ?: "项目根目录不可用", NotificationType.ERROR)
        }
    }

    private fun notify(
        project: com.intellij.openapi.project.Project,
        message: String,
        type: NotificationType,
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Task Action Runner")
            .createNotification(message, type)
            .notify(project)
    }
}
