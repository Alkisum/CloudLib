package com.alkisum.android.cloudlib.file;

import java.io.File;

/**
 * Super class for file types used during cloud operations.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class CloudFile {

    /**
     * File name.
     */
    private final String name;

    /**
     * File base name (file name without extension).
     */
    private final String baseName;

    /**
     * File object.
     */
    private File file;

    /**
     * File creation time.
     */
    private long creationTime;

    /**
     * File modified time.
     */
    private long modifiedTime;

    /**
     * CloudFile constructor.
     *
     * @param name File name
     */
    public CloudFile(final String name) {
        this.name = name;
        this.baseName = name.replaceFirst("[.][^.]+$", "");
    }

    /**
     * CloudFile constructor.
     *
     * @param name         File name
     * @param file         File object
     * @param creationTime File creation time
     * @param modifiedTime File modified time
     */
    public CloudFile(final String name, final File file,
                     final long creationTime, final long modifiedTime) {
        this.name = name;
        this.baseName = name.replaceFirst("[.][^.]+$", "");
        this.file = file;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
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
    public final String getBaseName() {
        return baseName;
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

    /**
     * @return File creation time
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @return File modified time
     */
    public long getModifiedTime() {
        return modifiedTime;
    }
}
