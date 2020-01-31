package com.avevad.neo.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NLogMessage implements Serializable {
    public final long time;
    public final NSeverity severity;
    public final String logger;
    public final String label;
    public final String message;
    public final List<StackTraceElement> stackTrace;

    public NLogMessage(NSeverity severity, String logger, String label, String message, StackTraceElement[] stackTrace, long time) {
        this.time = time;
        this.severity = severity;
        this.logger = logger;
        this.label = label;
        this.stackTrace = Collections.unmodifiableList(Arrays.asList(stackTrace));
        this.message = message;
    }

    public NLogMessage(NSeverity severity, String logger, String label, StackTraceElement[] stackTrace, String message) {
        this(severity, logger, label, message, stackTrace, System.currentTimeMillis());
    }

    public enum NSeverity {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }
}
