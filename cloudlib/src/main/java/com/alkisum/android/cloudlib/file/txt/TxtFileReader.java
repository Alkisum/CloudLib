package com.alkisum.android.cloudlib.file.txt;

import android.os.AsyncTask;

import com.alkisum.android.cloudlib.events.TxtFileReaderEvent;
import com.alkisum.android.cloudlib.file.CloudFile;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task reading data from files and converting it to TXT file objects.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class TxtFileReader extends AsyncTask<Void, Void, List<TxtFile>> {

    /**
     * List of files to read.
     */
    private final List<CloudFile> files;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * Subscriber ids allowed to process the events.
     */
    private final Integer[] subscriberIds;

    /**
     * TxtFileReader constructor.
     *
     * @param files         List of files to read
     * @param subscriberIds Subscriber ids allowed to process the events
     */
    public TxtFileReader(final List<CloudFile> files,
                         final Integer[] subscriberIds) {
        this.files = files;
        this.subscriberIds = subscriberIds;
    }

    @Override
    protected final List<TxtFile> doInBackground(final Void... params) {
        List<TxtFile> txtFiles = new ArrayList<>();
        BufferedReader br = null;
        try {
            for (CloudFile file : files) {
                br = new BufferedReader(new FileReader(file.getFile()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                    if (line != null) {
                        sb.append(System.getProperty("line.separator"));
                    }
                }
                String content = sb.toString();
                TxtFile txtFile = new TxtFile(
                        file.getName(),
                        content,
                        file.getFile(),
                        file.getCreationTime(),
                        file.getModifiedTime());
                txtFiles.add(txtFile);
            }
        } catch (IOException e) {
            exception = e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                exception = e;
            }
        }
        return txtFiles;
    }

    @Override
    protected final void onPostExecute(final List<TxtFile> txtFiles) {
        if (exception == null) {
            EventBus.getDefault().post(new TxtFileReaderEvent(subscriberIds,
                    TxtFileReaderEvent.OK, txtFiles));
        } else {
            EventBus.getDefault().post(new TxtFileReaderEvent(subscriberIds,
                    TxtFileReaderEvent.ERROR, exception));
        }
    }
}
