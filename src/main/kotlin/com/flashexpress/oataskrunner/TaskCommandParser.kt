package com.flashexpress.oataskrunner

data class TaskActionMetadata(
    val relativePath: String,
    val className: String,
    val methodName: String,
    val docComment: String?,
)

enum class CommandSource {
    DOCUMENTED,
    DERIVED,
}

data class TaskCommand(
    val cliEntry: String,
    val taskName: String,
    val actionName: String,
    val source: CommandSource,
) {
    val baseCommand: String
        get() = "php $cliEntry $taskName $actionName"
}

class TaskCommandParser {
    fun resolve(metadata: TaskActionMetadata): TaskCommand? {
        if (!isTaskAction(metadata)) {
            return null
        }

        val documentedCommand = metadata.docComment
            ?.let { documentedCommandPattern.find(it) }
            ?.destructured
            ?.let { (cliEntry, taskName, actionName) ->
                TaskCommand(cliEntry, taskName, actionName, CommandSource.DOCUMENTED)
            }

        return documentedCommand ?: TaskCommand(
            DEFAULT_CLI_ENTRY,
            toKebabCase(metadata.className.removeSuffix(TASK_SUFFIX)),
            metadata.methodName.removeSuffix(ACTION_SUFFIX),
            CommandSource.DERIVED,
        )
    }

    private fun isTaskAction(metadata: TaskActionMetadata): Boolean {
        return metadata.relativePath.startsWith(TASK_DIRECTORY)
            && metadata.relativePath.endsWith(TASK_FILE_SUFFIX)
            && metadata.className.endsWith(TASK_SUFFIX)
            && metadata.methodName.endsWith(ACTION_SUFFIX)
    }

    private fun toKebabCase(value: String): String {
        return value
            .replace(acronymBoundary, "$1-$2")
            .replace(wordBoundary, "$1-$2")
            .replace('_', '-')
            .lowercase()
    }

    private companion object {
        const val TASK_DIRECTORY = "app/tasks/"
        const val TASK_FILE_SUFFIX = "Task.php"
        const val TASK_SUFFIX = "Task"
        const val ACTION_SUFFIX = "Action"
        const val DEFAULT_CLI_ENTRY = "app/cli.php"

        val documentedCommandPattern = Regex(
            """\bphp\s+(app/cli\.php|bin/cli)\s+([^\s*]+)\s+([^\s*]+)""",
        )
        val acronymBoundary = Regex("([A-Z]+)([A-Z][a-z])")
        val wordBoundary = Regex("([a-z0-9])([A-Z])")
    }
}
