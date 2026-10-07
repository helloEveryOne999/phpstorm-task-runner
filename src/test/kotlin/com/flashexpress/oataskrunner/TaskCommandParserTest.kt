package com.flashexpress.oataskrunner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaskCommandParserTest {
    private val parser = TaskCommandParser()

    @Test
    fun `uses documented app cli task and action without example arguments`() {
        val command = parser.resolve(
            metadata(
                className = "WorkflowPendingTask",
                methodName = "send_smsAction",
                docComment = """/** php app/cli.php workflow-pending send_sms 10000 */""",
            ),
        )

        assertEquals(TaskCommand("app/cli.php", "workflow-pending", "send_sms", CommandSource.DOCUMENTED), command)
        assertEquals("php app/cli.php workflow-pending send_sms", command?.baseCommand)
    }

    @Test
    fun `preserves documented bin cli entry`() {
        val command = parser.resolve(
            metadata(
                className = "TranslationTask",
                methodName = "genAction",
                docComment = """/** php bin/cli translation gen */""",
            ),
        )

        assertEquals(TaskCommand("bin/cli", "translation", "gen", CommandSource.DOCUMENTED), command)
        assertEquals("php bin/cli translation gen", command?.baseCommand)
    }

    @Test
    fun `derives kebab case task and original action name when documentation is absent`() {
        val command = parser.resolve(
            metadata(
                className = "BankFlowToolTask",
                methodName = "testMatchFlowAction",
                docComment = null,
            ),
        )

        assertEquals(TaskCommand("app/cli.php", "bank-flow-tool", "testMatchFlow", CommandSource.DERIVED), command)
    }

    @Test
    fun `rejects actions outside the task directory`() {
        val command = parser.resolve(
            metadata(relativePath = "app/modules/Pay/PaymentTask.php"),
        )

        assertNull(command)
    }

    @Test
    fun `rejects methods without the action suffix`() {
        val command = parser.resolve(
            metadata(methodName = "send_sms"),
        )

        assertNull(command)
    }

    private fun metadata(
        relativePath: String = "app/tasks/WorkflowPendingTask.php",
        className: String = "WorkflowPendingTask",
        methodName: String = "send_smsAction",
        docComment: String? = null,
    ): TaskActionMetadata {
        return TaskActionMetadata(relativePath, className, methodName, docComment)
    }
}
