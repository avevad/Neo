package com.avevad.neo.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NLogger {
    public final String name;
    public final NLogDestination destination;

    public NLogger(String name, NLogDestination destination) {
        this.name = name;
        this.destination = destination;
    }

    public void log(NLogMessage.NSeverity severity, String label, String message) {
        if (destination != null) {
            String[] lines = message.split("\n");
            for (String line : lines)
                destination.write(new NLogMessage(severity, name, label, line));
        }
    }

    public void log(NLogMessage.NSeverity severity, String label, Exception ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        log(severity, label, stringWriter.toString());
    }
}
