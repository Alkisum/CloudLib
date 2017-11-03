package com.alkisum.android.cloudlib.file.json;

import com.alkisum.android.cloudlib.file.CloudFile;

import org.json.JSONObject;

import java.io.File;

/**
 * Class defining a JSON file.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.0
 */
public class JsonFile extends CloudFile {

    /**
     * JSON file extension.
     */
    public static final String FILE_EXT = ".json";

    /**
     * JSON object contained in the file.
     */
    private final JSONObject jsonObject;

    /**
     * JsonFile constructor.
     *
     * @param name       File name
     * @param jsonObject JSON object contained in the file
     */
    public JsonFile(final String name, final JSONObject jsonObject) {
        super(name);
        this.jsonObject = jsonObject;
    }

    /**
     * JsonFile constructor.
     *
     * @param name         File name
     * @param jsonObject   JSON object contained in the file
     * @param file         File object
     * @param creationTime File creation time
     * @param modifiedTime File modified time
     */
    JsonFile(final String name, final JSONObject jsonObject, final File file,
             final long creationTime, final long modifiedTime) {
        super(name, file, creationTime, modifiedTime);
        this.jsonObject = jsonObject;
    }

    /**
     * @return JSON object contained in the file
     */
    public final JSONObject getJsonObject() {
        return jsonObject;
    }
}
