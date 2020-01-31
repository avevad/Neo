package com.avevad.neo.logging;

import com.avevad.neo.util.NFunction;

import java.io.PrintStream;
import java.util.Date;

public class NPrintLogDestination implements NLogDestination {
    private static final String DEFAULT_DATE_FORMAT = "%tH:%<tM:%<tS %<td.%<tm.%<ty";
    private static final NFunction<NLogMessage, String> DEFAULT_FORMAT = message -> {
        String date = String.format(DEFAULT_DATE_FORMAT, message.time);
        String label = message.logger + (message.label == null ? "" : (":" + message.label));
        String severity = String.valueOf(message.severity.toString().charAt(0));
        String file = message.stackTrace.get(0).getFileName();
        String line = message.stackTrace.get(0).getLineNumber() + "";
        //return String.format("[%s] [%s] (%s:%s) [%s] %s", severity, date, file, line, label, message.message);
        return String.format("[%s] [%s] [%s] %s", severity, date, label, message.message);
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
