package com.best.agent.tools;

import org.junit.jupiter.api.Test;

class TerminalOperationToolTest {

    @Test
    void executeCommand() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        System.out.println(terminalOperationTool.executeCommand("dir"));
    }
}