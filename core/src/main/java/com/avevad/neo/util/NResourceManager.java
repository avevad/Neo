package com.avevad.neo.util;

import com.avevad.neo.graphics.NImage;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.logging.NLogDestination;
import com.avevad.neo.logging.NLogMessage;
import com.avevad.neo.logging.NLogger;

import java.io.*;
import java.util.*;

public final class NResourceManager {
    private final NLogger logger;

    public NResourceManager(NLogDestination logDestination) {
        logger = new NLogger(toString(), logDestination);
    }

    public NResourceManager() {
        this(null);
    }

    @Override
    public String toString() {
        return "NResourceManager@" + super.toString().substring(super.toString().lastIndexOf('@') + 1);
    }


    ///////////////////////////////////////////////////////////////////////////
    // STRINGS
    ///////////////////////////////////////////////////////////////////////////

    private String locale = "EN";
    private final Map<String, String> cachedStrings = new HashMap<>();
    private final Set<String> cachedStringFiles = new HashSet<>();
    private final Map<String, ResourceMap> stringSets = new HashMap<>();

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public void addStringSet(String name, File directory) {
        addStringSet(name, resource -> new FileInputStream(new File(directory, resource)));
    }

    public void addStringSet(String name, ResourceMap resourceMap) {
        stringSets.put(name, resourceMap);
    }

    public String string(String name, Object... objects) {
        if (name == null || locale == null) return name;
        int div1 = name.indexOf('/');
        int div2 = name.indexOf(':');
        if (div1 == -1 || div2 == -1) {
            logger.log(NLogMessage.NSeverity.WARNING, "Invalid string '" + name + "' (" + locale + ") was queried");
            return name;
        }
        String setName = name.substring(0, div1);
        String fileName = name.substring(div1 + 1, div2);
        String id = name.substring(div2 + 1);
        if (cachedStringFiles.contains(setName + "/" + fileName + "_" + locale)) {
            if (cachedStrings.containsKey(name + "_" + locale))
                return String.format(cachedStrings.get(name + "_" + locale), objects);
            else {
                logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent id was queried");
                return name;
            }
        }
        ResourceMap set = stringSets.get(setName);
        if (set == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent set was queried");
            return name;
        }
        String realFilename = fileName.replaceAll("\\.", "/") + "_" + locale + ".txt";
        Scanner scanner;
        try {
            scanner = new Scanner(set.getResourceStream(realFilename));
        } catch (FileNotFoundException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent file was queried");
            return name;
        }
        int line = 0;
        String ret = null;
        while (scanner.hasNextLine()) {
            line++;
            String entry = scanner.nextLine();
            int delim = entry.indexOf('=');
            if (delim == -1) {
                logger.log(NLogMessage.NSeverity.WARNING, "Invalid string entry in file '" + realFilename + "', line " + line);
                continue;
            }
            String key = entry.substring(0, delim).trim();
            String value = entry.substring(delim + 1).trim();
            value = value.replaceAll("\\\\n", "\n");
            if (key.equals(id)) ret = value;
            cachedStrings.put(setName + "/" + fileName + ":" + key + "_" + locale, value);
        }
        logger.log(NLogMessage.NSeverity.DEBUG, "Cached " + line + " line(s) from file '" + realFilename + "'");
        cachedStringFiles.add(setName + "/" + fileName + "_" + locale);
        if (ret == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent id was queried");
            return name;
        }
        return String.format(ret, objects);
    }

    ///////////////////////////////////////////////////////////////////////////
    // IMAGES
    ///////////////////////////////////////////////////////////////////////////

    private NImage.NImageIO<? extends NImage> imageIO;
    private final Map<String, NImage> cachedImages = new HashMap<>();
    private final Map<String, Atlas> cachedAtlases = new HashMap<>();
    private final Map<String, ResourceMap> imageSets = new HashMap<>();

    public void setImageIO(NImage.NImageIO<? extends NImage> imageIO) {
        this.imageIO = imageIO;
    }

    public void addImageSet(String name, File directory) {
        addImageSet(name, resource -> new FileInputStream(new File(directory, resource)));
    }

    public void addImageSet(String name, ResourceMap resourceMap) {
        imageSets.put(name, resourceMap);
    }

    public NImage image(String name) {
        if (name == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "Invalid image '" + name + "' was queried");
            return null;
        }
        int div1 = name.indexOf('/');
        int div2 = name.indexOf(':');
        if (div1 == -1 || div2 == -1) {
            logger.log(NLogMessage.NSeverity.WARNING, "Invalid image '" + name + "' was queried");
            return null;
        }
        String setName = name.substring(0, div1);
        String fileName = name.substring(div1 + 1, div2);
        String id = name.substring(div2 + 1);
        NImage image = cachedImages.get(name);
        if (image != null) return image;
        Atlas atlas = cachedAtlases.get(setName + "/" + fileName);
        if (atlas != null) {
            NRectangle bounds = atlas.bounds.get(id);
            if (bounds == null) {
                logger.log(NLogMessage.NSeverity.WARNING, "Image '" + name + "' with nonexistent id was queried");
                return null;
            } else {
                image = atlas.image.copyReadonly(bounds.x, bounds.y, bounds.w, bounds.h);
                cachedImages.put(name, image);
                logger.log(NLogMessage.NSeverity.DEBUG, "Cached image '" + name + "' from atlas '" + (setName + "/" + fileName) + "'");
                return image;
            }
        }
        ResourceMap set = imageSets.get(setName);
        if (set == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "Image '" + name + "' with nonexistent set was queried");
            return null;
        }
        String atlasName = fileName.replaceAll("\\.", "/");
        Scanner scanner;
        try {
            scanner = new Scanner(set.getResourceStream(atlasName + ".atlas"));
        } catch (FileNotFoundException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "Image '" + name + "' with nonexistent atlas file was queried");
            return null;
        }
        String suffix = scanner.nextLine();
        InputStream in;
        try {
            in = set.getResourceStream(atlasName + suffix);
        } catch (FileNotFoundException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "Image '" + name + "' with nonexistent image file was queried");
            return null;
        }
        try {
            image = imageIO.loadImage(in);
        } catch (IOException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "Image '" + name + "' with invalid image file was queried");
            return null;
        }
        Map<String, NRectangle> bounds = new HashMap<>();
        atlas = new Atlas(image, bounds);
        int line = 1;
        while (scanner.hasNextLine()) {
            line++;
            String entry = scanner.nextLine();
            String[] tokens = entry.split("\\s+");
            if (tokens.length != 5) {
                logger.log(NLogMessage.NSeverity.WARNING, "Invalid atlas entry in file '" + atlasName + ".atlas', line " + line);
                continue;
            }
            String key = tokens[0];
            int x, y, w, h;
            try {
                x = Integer.parseInt(tokens[1]);
                y = Integer.parseInt(tokens[2]);
                w = Integer.parseInt(tokens[3]);
                h = Integer.parseInt(tokens[4]);
            } catch (NumberFormatException ex) {
                logger.log(NLogMessage.NSeverity.WARNING, "Invalid atlas entry in file '" + atlasName + ".atlas', line " + line);
                continue;
            }
            bounds.put(key, new NRectangle(x, y, w, h));
        }
        cachedAtlases.put(setName + "/" + fileName, atlas);
        logger.log(NLogMessage.NSeverity.DEBUG, "Cached " + (line - 1) + " atlas entries from file '" + atlasName + ".atlas'");
        return image(name);
    }

    private static final class Atlas {
        public final NImage image;
        public final Map<String, NRectangle> bounds;

        private Atlas(NImage image, Map<String, NRectangle> bounds) {
            this.image = image;
            this.bounds = bounds;
        }
    }


    public interface ResourceMap {
        InputStream getResourceStream(String resource) throws FileNotFoundException;
    }
}
