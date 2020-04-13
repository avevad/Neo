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
        String fullPath = prefix + "/" + resource;
        InputStream stream = JarResourceMap.class.getResourceAsStream(fullPath);
        if (stream == null) throw new FileNotFoundException("resource '" + fullPath + "' not found");
        return stream;
    }
}
