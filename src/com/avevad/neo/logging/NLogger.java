package com.avevad.neo.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NLogger {
    private final String LOGGER_CLASS_FILE_NAME = "NLogger.java";

    public final String name;
    public final NLogDestination destination;

    public NLogger(String name, NLogDestination destination) {
        this.name = name;
        this.destination = destination;
    }

    public void log(NLogMessage.NSeverity severity, String label, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        ArrayList<StackTraceElement> stackTraceList = new ArrayList<>(Arrays.asList(stackTrace));
        Collections.reverse(stackTraceList);
        while (stackTraceList.size() > 0 && LOGGER_CLASS_FILE_NAME.equals(stackTraceList.get(0).getFileName()))
            stackTraceList.remove(0);
        if (destination != null) {
            String[] lines = message.split("\n");
            for (String line : lines)
                destination.write(new NLogMessage(severity, name, label, stackTraceList.toArray(new StackTraceElement[0]), line));
        }
    }

    public void log(NLogMessage.NSeverity severity, String message) {
        log(severity, null, message);
    }

    public void log(NLogMessage.NSeverity severity, String label, Exception ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        log(severity, label, stringWriter.toString());
    }

    public void log(NLogMessage.NSeverity severity, Exception ex) {
        log(severity, null, ex);
    }
}
