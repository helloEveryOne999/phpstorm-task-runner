package com.flashexpress.oataskrunner

import kotlin.test.Test
import kotlin.test.assertEquals

class TerminalExecutionDispatcherTest {
    private val dispatcher = TerminalExecutionDispatcher()

    @Test
    fun `runs in a new terminal when explicitly requested`() {
        var recentAttempts = 0
        var newRuns = 0

        dispatcher.dispatch(
            TerminalExecutionTarget.NEW,
            { recentAttempts += 1; true },
            { newRuns += 1 },
        )

        assertEquals(0, recentAttempts)
        assertEquals(1, newRuns)
    }

    @Test
    fun `runs in the recent terminal when it is available`() {
        var newRuns = 0

        dispatcher.dispatch(TerminalExecutionTarget.RECENT, { true }, { newRuns += 1 })

        assertEquals(0, newRuns)
    }

    @Test
    fun `creates a terminal when the recent terminal is unavailable`() {
        var newRuns = 0

        dispatcher.dispatch(TerminalExecutionTarget.RECENT, { false }, { newRuns += 1 })

        assertEquals(1, newRuns)
    }
}
