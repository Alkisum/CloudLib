package com.alkisum.android.cloudops.file.json;

import org.json.JSONObject;

import java.io.File;

/**
 * Class defining a JSON file.
 *
 * @author Alkisum
 * @version 1.1
 * @since 1.0
 */
public class JsonFile {

    /**
     * JSON file extension.
     */
    public static final String FILE_EXT = ".json";

    /**
     * File name.
     */
    private final String name;

    /**
     * File base name (file name without extension).
     */
    private final String baseName;

    /**
     * JSON object contained in the file.
     */
    private final JSONObject jsonObject;

    /**
     * File object.
     */
    private File file;

    /**
     * JsonFile constructor.
     *
     * @param name       File name
     * @param jsonObject JSON object contained in the file
     */
    public JsonFile(final String name, final JSONObject jsonObject) {
        this.name = name;
        this.baseName = name.replaceFirst("[.][^.]+$", "");
        this.jsonObject = jsonObject;
    }

    /**
     * JsonFile constructor.
     *
     * @param name       File name
     * @param jsonObject JSON object contained in the file
     * @param file       File object
     */
    JsonFile(final String name, final JSONObject jsonObject,
                    final File file) {
        this.name = name;
        this.baseName = name.replaceFirst("[.][^.]+$", "");
        this.jsonObject = jsonObject;
        this.file = file;
    }

    /**
     * @return File name
     */
    public String getName() {
        return name;
    }

    /**
     * @return File base name (file name without extension).
     */
    final String getBaseName() {
        return baseName;
    }

    /**
     * @return JSON object contained in the file
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * @return File object
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file File object to set
     */
    public void setFile(final File file) {
        this.file = file;
    }
}
