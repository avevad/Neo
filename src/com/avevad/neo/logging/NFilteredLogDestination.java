package com.avevad.neo.logging;

import com.avevad.neo.logging.NLogMessage.NSeverity;
import com.avevad.neo.util.NFunction;

public class NFilteredLogDestination implements NLogDestination {
    private final NFunction<NLogMessage, Boolean> filter;
    private final NLogDestination destination;

    public NFilteredLogDestination(NLogDestination destination, NFunction<NLogMessage, Boolean> filter) {
        this.filter = filter;
        this.destination = destination;
    }

    public NFilteredLogDestination(NLogDestination destination, NSeverity minSeverity, NSeverity maxSeverity) {
        this(destination, message ->
                message.severity.ordinal() >= minSeverity.ordinal() &&
                        message.severity.ordinal() <= maxSeverity.ordinal());
    }

    public NFilteredLogDestination(NLogDestination destination, NSeverity minSeverity) {
        this(destination, minSeverity, NSeverity.FATAL);
    }


    @Override
    public void write(NLogMessage message) {
        if (filter.invoke(message)) destination.write(message);
    }
}
