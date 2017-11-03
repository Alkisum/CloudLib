package com.alkisum.android.cloudlib.file.txt;

import com.alkisum.android.cloudlib.file.CloudFile;

import java.io.File;

/**
 * Class defining a TXT file.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class TxtFile extends CloudFile {

    /**
     * TXT file extension.
     */
    public static final String FILE_EXT = ".txt";

    /**
     * File content.
     */
    private final String content;

    /**
     * TxtFile constructor.
     *
     * @param name    File name
     * @param content File content
     */
    public TxtFile(final String name, final String content) {
        super(name);
        this.content = content;
    }

    /**
     * TxtFile constructor.
     *
     * @param name         File name
     * @param content      File content
     * @param file         File object
     * @param creationTime File creation time
     * @param modifiedTime File modified time
     */
    TxtFile(final String name, final String content, final File file,
            final long creationTime, final long modifiedTime) {
        super(name, file, creationTime, modifiedTime);
        this.content = content;
    }

    /**
     * @return File content
     */
    public String getContent() {
        return content;
    }
}
