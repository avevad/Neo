package com.avevad.neo.logging;

import com.avevad.neo.util.NFunction;

import java.io.PrintStream;
import java.util.Date;

public class NPrintLogDestination implements NLogDestination {
    public static final NFunction<NLogMessage, String> DEFAULT_FORMAT = message -> {
        String date = new Date(message.time).toString();
        String label = message.logger + (message.label == null ? "" : (":" + message.label));
        String severity = String.valueOf(message.severity.toString().charAt(0));
        return String.format("[%s] [%s] [%s] %s", date, label, severity, message.message);
    };

    public final PrintStream out;
    public final NFunction<NLogMessage, String> format;

    public NPrintLogDestination(PrintStream out, NFunction<NLogMessage, String> format) {
        this.out = out;
        this.format = format;
    }


    public NPrintLogDestination(PrintStream out) {
        this(out, DEFAULT_FORMAT);
    }

    @Override
    public void write(NLogMessage message) {
        out.println(format.invoke(message));
    }
}
