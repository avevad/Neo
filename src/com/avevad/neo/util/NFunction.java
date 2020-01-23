package com.avevad.neo.util;

public interface NFunction<P, R> {
    R invoke(P parameter);
}
