package com.avevad.neo.logging;

import java.io.Serializable;

public class NLogMessage implements Serializable {
    public final long time;
    public final NSeverity severity;
    public final String logger;
    public final String label;
    public final String message;

    public NLogMessage(NSeverity severity, String logger, String label, String message, long time) {
        this.time = time;
        this.severity = severity;
        this.logger = logger;
        this.label = label;
        this.message = message;
    }

    public NLogMessage(NSeverity severity, String logger, String label, String message) {
        this(severity, logger, label, message, System.currentTimeMillis());
    }

    public enum NSeverity {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }
}
