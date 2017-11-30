package top.zbeboy.isy.service.util

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipMethod
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.service.util.compress.ZipInputStream
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by zbeboy 2017-11-30 .
 **/
class FilesUtils {
    companion object {
        /**
         * 删除硬盘上的文件
         *
         * @param path 文件路径
         * @return true 删除成功，false 删除失败或路径为空，文件不存在
         * @throws IOException
         */
        @JvmStatic
        @Throws(IOException::class)
        fun deleteFile(path: String): Boolean {
            if (!Objects.isNull(path) && "" != path.trim { it <= ' ' }) {
                val file = File(path)
                if (file.exists()) {
                    return file.delete()
                }
            }
            return false
        }

        /**
         * 转换文件单位
         *
         * @param size 文件大小
         * @return 文件尺寸
         */
        @JvmStatic
        fun transformationFileUnit(size: Long): String {
            return if (size < 1024) {
                size.toString() + "B"
            } else if (size >= 1024 && size < 1024 * 1024) {
                (size / 1024).toString() + "KB"
            } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
                (size / (1024 * 1024)).toString() + "MB"
            } else {
                (size / (1024 * 1024 * 1024)).toString() + "GB"
            }
        }

        /**
         * 单个文件压缩成zip
         *
         * @param fileName 文件名 带后缀
         * @param zipPath  输出zip路径
         * @param filePath 文件路径，带文件名 + 后缀
         * @return 是否成功
         * @throws Exception
         */
        @JvmStatic
        @Throws(Exception::class)
        fun compressZip(fileName: String, zipPath: String, filePath: String): Boolean {
            val file = File(filePath)
            if (file.exists()) {
                val scatterSample = ScatterSample()
                val zipFile = File(zipPath)
                if (!zipFile.parentFile.exists()) {//create file
                    zipFile.parentFile.mkdirs()
                }
                val zipArchiveOutputStream = ZipArchiveOutputStream(zipFile)
                val entry = ZipArchiveEntry(fileName)
                entry.method = ZipMethod.STORED.code
                scatterSample.addEntry(entry, ZipInputStream(file))
                scatterSample.writeTo(zipArchiveOutputStream)
                zipArchiveOutputStream.close()
                return true
            }
            return false
        }

        /**
         * 多个文件压缩成zip
         *
         * @param fileName 文件名 带后缀
         * @param zipPath  输出zip路径
         * @param filePath 文件路径，带文件名 + 后缀
         * @throws Exception
         */
        @JvmStatic
        @Throws(Exception::class)
        fun compressZipMulti(fileName: List<String>, zipPath: String, filePath: List<String>) {
            val zipFile = File(zipPath)
            if (!zipFile.parentFile.exists()) {//create file
                zipFile.parentFile.mkdirs()
            }
            val zipArchiveOutputStream = ZipArchiveOutputStream(zipFile)
            if (!ObjectUtils.isEmpty(fileName) && !ObjectUtils.isEmpty(filePath) && fileName.size == filePath.size) {
                for (i in fileName.indices) {
                    val file = File(filePath[i])
                    if (file.exists()) {
                        val scatterSample = ScatterSample()
                        val entry = ZipArchiveEntry(fileName[i])
                        entry.method = ZipMethod.STORED.code
                        scatterSample.addEntry(entry, ZipInputStream(file))
                        scatterSample.writeTo(zipArchiveOutputStream)
                    }
                }
            }
            zipArchiveOutputStream.close()
        }
    }
}