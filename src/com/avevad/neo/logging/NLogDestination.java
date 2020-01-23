package com.avevad.neo.logging;

public interface NLogDestination {
    void write(NLogMessage message);
}
