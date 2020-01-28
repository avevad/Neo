package com.avevad.neo.net.socket.messaging;

import java.io.Serializable;

final class NResponsePacket implements Serializable {
    public final long id;
    public final Serializable response;

    NResponsePacket(long id, Serializable response) {
        this.id = id;
        this.response = response;
    }
}
