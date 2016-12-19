package top.zbeboy.isy.service.util;

import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.parallel.InputStreamSupplier;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by zbeboy on 2016/12/19.
 */
public class ScatterSample {
    ParallelScatterZipCreator scatterZipCreator = new ParallelScatterZipCreator();
    ScatterZipOutputStream dirs = ScatterZipOutputStream.fileBased(File.createTempFile("scatter-dirs", "tmp"));

    public ScatterSample() throws IOException {
    }

    public void addEntry(ZipArchiveEntry zipArchiveEntry, InputStreamSupplier streamSupplier) throws IOException {
        if (zipArchiveEntry.isDirectory() && !zipArchiveEntry.isUnixSymlink())
            dirs.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, streamSupplier));
        else
            scatterZipCreator.addArchiveEntry(zipArchiveEntry, streamSupplier);
    }

    public void writeTo(ZipArchiveOutputStream zipArchiveOutputStream)
            throws IOException, ExecutionException, InterruptedException {
        dirs.writeTo(zipArchiveOutputStream);
        dirs.close();
        scatterZipCreator.writeTo(zipArchiveOutputStream);
    }
}
