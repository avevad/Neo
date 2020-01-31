package com.avevad.neo.net.socket.messaging;

import java.io.Serializable;

final class NExceptionPacket implements Serializable {
    public final long id;
    public final RuntimeException ex;

    NExceptionPacket(long id, RuntimeException ex) {
        this.id = id;
        this.ex = ex;
    }
}
