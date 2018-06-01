package top.zbeboy.isy.service.util.compress

import org.apache.commons.compress.parallel.InputStreamSupplier
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Created by zbeboy 2017-11-30 .
 **/
class ZipInputStream(private val file: File) : InputStreamSupplier {

    private val log = LoggerFactory.getLogger(ZipInputStream::class.java)

    override fun get(): InputStream? {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
        } catch (e: FileNotFoundException) {
            log.error("Read file error, is {}", e)
        }

        return inputStream
    }
}