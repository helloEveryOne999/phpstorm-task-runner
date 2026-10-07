package com.flashexpress.oataskrunner

import com.intellij.openapi.project.Project
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.io.IOException

@Suppress("DEPRECATION")
class TerminalCommandRunner(
    private val dispatcher: TerminalExecutionDispatcher = TerminalExecutionDispatcher(),
) {
    @Throws(IOException::class)
    fun run(
        project: Project,
        command: TaskCommand,
        commandLine: String,
        target: TerminalExecutionTarget,
    ) {
        val workingDirectory = project.basePath
            ?: throw IllegalStateException("项目根目录不可用")
        val tabName = "Task: ${command.taskName} ${command.actionName}"
        val terminalManager = TerminalToolWindowManager.getInstance(project)

        dispatcher.dispatch(
            target,
            runInRecentTerminal = { runInSelectedTerminal(terminalManager, commandLine) },
            runInNewTerminal = {
                terminalManager
                    .createLocalShellWidget(workingDirectory, tabName)
                    .executeCommand(commandLine)
            },
        )
    }

    private fun runInSelectedTerminal(
        terminalManager: TerminalToolWindowManager,
        commandLine: String,
    ): Boolean {
        val toolWindow = terminalManager.toolWindow ?: return false
        val selectedContent = toolWindow.contentManager.selectedContent ?: return false
        val terminal = TerminalToolWindowManager.findWidgetByContent(selectedContent)
            ?: return false

        terminal.sendCommandToExecute(commandLine)
        return true
    }
}
