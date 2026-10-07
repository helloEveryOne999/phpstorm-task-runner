package com.flashexpress.oataskrunner

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.event.ActionEvent
import javax.swing.JComponent
import javax.swing.Action
import javax.swing.SwingUtilities

class TaskCommandDialog(
    project: Project,
    taskCommand: TaskCommand,
) : DialogWrapper(project) {
    private val commandField = JBTextField(taskCommand.baseCommand)
    private val argumentsField = JBTextField()
    private var requestedExecutionTarget = TerminalExecutionTarget.RECENT
    var executionTarget = TerminalExecutionTarget.RECENT
        private set

    val commandLine: String
        get() = listOf(commandField.text.trim(), argumentsField.text.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")

    init {
        title = "运行 Task Action"
        setOKButtonText("在最近终端中运行")
        init()
    }

    override fun createActions(): Array<Action> {
        return arrayOf(
            okAction,
            object : DialogWrapperAction("在新终端中运行") {
                override fun doAction(event: ActionEvent) {
                    submit(TerminalExecutionTarget.NEW, event)
                }
            },
            cancelAction,
        )
    }

    override fun doOKAction() {
        executionTarget = requestedExecutionTarget
        super.doOKAction()
    }

    override fun createCenterPanel(): JComponent {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent("命令：", commandField)
            .addLabeledComponent("附加参数：", argumentsField)
            .addComponentFillVertically(javax.swing.JPanel(), 0)
            .panel
    }

    override fun doValidate(): ValidationInfo? {
        return if (commandField.text.trim().isEmpty()) {
            ValidationInfo("命令不能为空", commandField)
        } else {
            null
        }
    }

    private fun submit(target: TerminalExecutionTarget, event: ActionEvent) {
        SwingUtilities.invokeLater {
            requestedExecutionTarget = target
            try {
                okAction.actionPerformed(event)
            } finally {
                requestedExecutionTarget = TerminalExecutionTarget.RECENT
            }
        }
    }
}
