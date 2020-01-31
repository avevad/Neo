package com.avevad.neo.net.socket.messaging;

import java.io.Serializable;

final class NMessagePacket implements Serializable {
    public final NSocketMessage message;

    NMessagePacket(NSocketMessage message) {
        this.message = message;
    }
}
