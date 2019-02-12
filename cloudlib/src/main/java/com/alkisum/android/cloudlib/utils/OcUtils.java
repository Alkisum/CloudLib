package com.alkisum.android.cloudlib.utils;

import com.owncloud.android.lib.resources.files.model.RemoteFile;

/**
 * Utility class of ownCloud operations.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.2
 */
public final class OcUtils {

    /**
     * OcUtils constructor.
     */
    private OcUtils() {

    }

    /**
     * Get the file name from the remote file object.
     *
     * @param file Remote file
     * @return File name
     */
    public static String getRemoteFileName(final RemoteFile file) {
        return getFileName(file.getRemotePath());
    }

    /**
     * Get the file name from the given path.
     *
     * @param path File path
     * @return File name
     */
    public static String getFileName(final String path) {
        String[] splitPath = path.split("/");
        return splitPath[splitPath.length - 1];
    }
}
