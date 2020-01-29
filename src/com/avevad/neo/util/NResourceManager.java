package com.avevad.neo.util;

import com.avevad.neo.graphics.NImage;
import com.avevad.neo.logging.NLogDestination;
import com.avevad.neo.logging.NLogMessage;
import com.avevad.neo.logging.NLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public final class NResourceManager {
    private String locale = "EN";
    private final Map<String, String> cachedStrings = new HashMap<>();
    private final Set<String> cachedFiles = new HashSet<>();
    private final Map<String, File> stringSets = new HashMap<>();
    private final NLogger logger;

    public NResourceManager(NLogDestination logDestination) {
        logger = new NLogger(toString(), logDestination);
    }

    public NResourceManager() {
        this(null);
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public void addStringSet(String name, File dir) {
        stringSets.put(name, dir);
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
        if (cachedFiles.contains(setName + "/" + fileName + "_" + locale)) {
            if (cachedStrings.containsKey(name + "_" + locale))
                return String.format(cachedStrings.get(name + "_" + locale), objects);
            else {
                logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent id was queried");
                return name;
            }
        }
        File set = stringSets.get(setName);
        if (set == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent set was queried");
            return name;
        }
        File file = new File(set, fileName.replaceAll("\\.", "/") + "_" + locale + ".txt");
        Scanner scanner;
        try {
            scanner = new Scanner(file);
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
                logger.log(NLogMessage.NSeverity.WARNING, "Invalid string entry in file '" + file.getAbsolutePath() + "', line " + line);

                continue;
            }
            String key = entry.substring(0, delim).trim();
            String value = entry.substring(delim + 1).trim();
            value = value.replaceAll("\\\\n", "\n");
            if (key.equals(id)) ret = value;
            cachedStrings.put(setName + "/" + fileName + ":" + key + "_" + locale, value);
        }
        logger.log(NLogMessage.NSeverity.DEBUG, "Cached " + line + " line(s) from file '" + file.getAbsolutePath() + "'");
        cachedFiles.add(setName + "/" + fileName + "_" + locale);
        if (ret == null) {
            logger.log(NLogMessage.NSeverity.WARNING, "String '" + name + "' (" + locale + ") with nonexistent id was queried");
            return name;
        }
        return String.format(ret, objects);
    }

    public interface NImageFactory {
        NImage getImage(File file);
    }

    @Override
    public String toString() {
        return "NResourceManager@" + super.toString().substring(super.toString().lastIndexOf('@') + 1);
    }
}
