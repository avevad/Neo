package com.avevad.neo.logging;

import java.util.HashSet;
import java.util.Set;

public class NMultiLogDestination implements NLogDestination {
    public Set<NLogDestination> destinations = new HashSet<>();

    @Override
    public void write(NLogMessage message) {
        for (NLogDestination destination : destinations) destination.write(message);
    }
}
