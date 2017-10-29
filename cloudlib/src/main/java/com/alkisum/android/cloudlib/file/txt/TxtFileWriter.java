package com.alkisum.android.cloudlib.file.txt;

import android.os.AsyncTask;

import com.alkisum.android.cloudlib.events.TxtFileWriterEvent;
import com.alkisum.android.cloudlib.file.CloudFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Task writing TXT content into files.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class TxtFileWriter extends AsyncTask<Void, Void, Void> {

    /**
     * Cache directory.
     */
    private final File cacheDir;

    /**
     * List of TXT files.
     */
    private final List<TxtFile> txtFiles;

    /**
     * Queue of CloudFile objects containing the TXT files to be uploaded.
     */
    private final Queue<CloudFile> cloudFiles;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * Subscriber ids allowed to process the events.
     */
    private Integer[] subscriberIds;

    /**
     * TxtFileWriter constructor.
     *
     * @param cacheDir      Cache directory
     * @param txtFiles      List of TXT files
     * @param subscriberIds Subscriber ids allowed to process the events
     */
    public TxtFileWriter(final File cacheDir,
                         final List<TxtFile> txtFiles,
                         final Integer[] subscriberIds) {
        this.cacheDir = cacheDir;
        this.txtFiles = txtFiles;
        this.subscriberIds = subscriberIds;
        this.cloudFiles = new LinkedList<>();
    }

    @Override
    protected final Void doInBackground(final Void... params) {

        try {
            for (TxtFile txtFile : txtFiles) {
                // Create temporary file, its name does not matter
                File file = File.createTempFile(txtFile.getBaseName(),
                        TxtFile.FILE_EXT, cacheDir);

                FileWriter writer = new FileWriter(file);
                writer.write(txtFile.getContent());
                writer.flush();
                writer.close();

                txtFile.setFile(file);

                cloudFiles.add(txtFile);
            }
        } catch (IOException e) {
            exception = e;
        }

        return null;
    }

    @Override
    protected final void onPostExecute(final Void param) {
        if (exception == null) {
            EventBus.getDefault().post(new TxtFileWriterEvent(subscriberIds,
                    TxtFileWriterEvent.OK, cloudFiles));
        } else {
            EventBus.getDefault().post(new TxtFileWriterEvent(subscriberIds,
                    TxtFileWriterEvent.ERROR, exception));
        }
    }
}
