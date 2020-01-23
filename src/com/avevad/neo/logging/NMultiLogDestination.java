package com.avevad.neo.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NMultiLogDestination implements NLogDestination {
    public Set<NLogDestination> destinations = new HashSet<>();

    public NMultiLogDestination(NLogDestination... destinations) {
        this.destinations.addAll(Arrays.asList(destinations));
    }

    @Override
    public void write(NLogMessage message) {
        for (NLogDestination destination : destinations) destination.write(message);
    }
}
