package com.avevad.neo.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class JarResourceMap implements NResourceManager.ResourceMap {
    public final String prefix;

    public JarResourceMap(String prefix) {
        this.prefix = prefix;
    }

    public JarResourceMap() {
        this("/");
    }

    @Override
    public InputStream getResourceStream(String resource) throws FileNotFoundException {
        InputStream stream = JarResourceMap.class.getResourceAsStream(prefix + "/" + resource);
        if(stream == null) throw new FileNotFoundException();
        return stream;
    }
}
