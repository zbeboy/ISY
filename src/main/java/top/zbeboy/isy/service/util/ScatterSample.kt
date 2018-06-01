package top.zbeboy.isy.service.util

import org.apache.commons.compress.archivers.zip.*
import org.apache.commons.compress.parallel.InputStreamSupplier
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * Created by zbeboy 2017-11-30 .
 **/
class ScatterSample @Throws(IOException::class) constructor() {
    internal var scatterZipCreator = ParallelScatterZipCreator()
    internal var dirs = ScatterZipOutputStream.fileBased(File.createTempFile("scatter-dirs", "tmp"))

    @Throws(IOException::class)
    fun addEntry(zipArchiveEntry: ZipArchiveEntry, streamSupplier: InputStreamSupplier) {
        if (zipArchiveEntry.isDirectory && !zipArchiveEntry.isUnixSymlink)
            dirs.addArchiveEntry(ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, streamSupplier))
        else
            scatterZipCreator.addArchiveEntry(zipArchiveEntry, streamSupplier)
    }

    @Throws(IOException::class, ExecutionException::class, InterruptedException::class)
    fun writeTo(zipArchiveOutputStream: ZipArchiveOutputStream) {
        dirs.writeTo(zipArchiveOutputStream)
        dirs.close()
        scatterZipCreator.writeTo(zipArchiveOutputStream)
    }
}