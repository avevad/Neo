package com.avevad.neo.net.socket.messaging;

import java.io.Serializable;

final class NCommandPacket implements Serializable {
    private static long instancesCount = 0;
    public final NSocketCommand<?> command;
    public final long id;

    public NCommandPacket(NSocketCommand<?> command) {
        this.command = command;
        id = instancesCount++;
    }
}
