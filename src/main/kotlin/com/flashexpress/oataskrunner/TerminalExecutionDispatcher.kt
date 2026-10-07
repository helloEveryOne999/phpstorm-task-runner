package com.flashexpress.oataskrunner

enum class TerminalExecutionTarget {
    RECENT,
    NEW,
}

class TerminalExecutionDispatcher {
    fun dispatch(
        target: TerminalExecutionTarget,
        runInRecentTerminal: () -> Boolean,
        runInNewTerminal: () -> Unit,
    ) {
        if (target == TerminalExecutionTarget.RECENT && runInRecentTerminal()) {
            return
        }

        runInNewTerminal()
    }
}
