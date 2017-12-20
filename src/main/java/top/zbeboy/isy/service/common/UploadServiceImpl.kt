package top.zbeboy.isy.service.common

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.service.util.IPTimeStamp
import top.zbeboy.isy.web.bean.file.FileBean
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-20 .
 **/
@Service("uploadService")
open class UploadServiceImpl : UploadService {

    private val log = LoggerFactory.getLogger(UploadServiceImpl::class.java)


    override fun upload(request: MultipartHttpServletRequest, path: String, address: String): List<FileBean> {
        val list = ArrayList<FileBean>()
        //1. build an iterator.
        val iterator = request.fileNames
        var multipartFile: MultipartFile
        //2. get each file
        while (iterator.hasNext()) {
            val fileBean = FileBean()
            //2.1 get next MultipartFile
            multipartFile = request.getFile(iterator.next())
            log.info(multipartFile.originalFilename + " uploaded!")
            fileBean.contentType = multipartFile.contentType
            val ipTimeStamp = IPTimeStamp(address)
            val words = multipartFile.originalFilename.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (words.size > 1) {
                val ext = words[words.size - 1].toLowerCase()
                var filename = ipTimeStamp.getIPTimeRand() + "." + ext
                if (filename.contains(":")) {
                    filename = filename.substring(filename.lastIndexOf(':') + 1, filename.length)
                }
                fileBean.originalFileName = multipartFile.originalFilename.substring(0, multipartFile.originalFilename.lastIndexOf('.'))
                fileBean.ext = ext
                fileBean.newName = filename
                fileBean.size = multipartFile.size
                //copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
                buildList(fileBean, list, path, filename, multipartFile)
            } else {
                // no filename
                val filename = ipTimeStamp.getIPTimeRand()
                fileBean.originalFileName = multipartFile.originalFilename.substring(0, multipartFile.originalFilename.lastIndexOf('.'))
                fileBean.newName = filename
                fileBean.size = multipartFile.size
                // copy file to local disk (make sure the path "e.g. D:/temp/files" exists)
                buildList(fileBean, list, path, filename, multipartFile)
            }
        }
        return list
    }

    @Throws(IOException::class)
    private fun buildPath(path: String, filename: String, multipartFile: MultipartFile): String? {
        var lastPath: String
        val saveFile = File(path, filename)
        log.info(path)
        if (multipartFile.size < File(path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + ":").freeSpace) {// has space with disk
            if (!saveFile.parentFile.exists()) {//create file
                saveFile.parentFile.mkdirs()
            }
            log.info(path)
            FileCopyUtils.copy(multipartFile.bytes, FileOutputStream(path + File.separator + filename))
            lastPath = path + File.separator + filename
            lastPath = lastPath.replace("\\\\".toRegex(), "/")
        } else {
            log.info("not valiablespace!")
            return null
        }
        return lastPath
    }

    private fun buildList(fileBean: FileBean, list: MutableList<FileBean>, path: String, filename: String, multipartFile: MultipartFile) {
        try {
            if (!StringUtils.isEmpty(path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])) {
                fileBean.lastPath = buildPath(path, filename, multipartFile)
                list.add(fileBean)
            }
        } catch (e: IOException) {
            log.error("Build File list exception, is {}", e)
        }

    }

    override fun download(fileName: String, filePath: String, response: HttpServletResponse, request: HttpServletRequest) {
        try {
            response.contentType = "application/x-msdownload"
            response.setHeader("Content-disposition", "attachment; filename=\"" + String((fileName + filePath.substring(filePath.lastIndexOf('.'))).toByteArray(charset("gb2312")), charset("ISO8859-1")) + "\"")
            val realPath = request.session.servletContext.getRealPath("/")
            val inputStream = FileInputStream(realPath + filePath)
            FileCopyUtils.copy(inputStream, response.outputStream)
        } catch (e: Exception) {
            log.error(" file is not found exception is {} ", e)
        }

    }

    override fun reviewPic(filePath: String, request: HttpServletRequest, response: HttpServletResponse) {
        try {
            val realPath = request.session.servletContext.getRealPath("/")
            val file = File(realPath + filePath)
            if (file.exists()) {
                val mediaType: MediaType
                val ext = filePath.substring(filePath.lastIndexOf('.') + 1)
                mediaType = if (ext.equals("png", ignoreCase = true)) {
                    MediaType.IMAGE_PNG
                } else if (ext.equals("gif", ignoreCase = true)) {
                    MediaType.IMAGE_GIF
                } else if (ext.equals("jpg", ignoreCase = true) || ext.equals("jpeg", ignoreCase = true)) {
                    MediaType.IMAGE_JPEG
                } else {
                    MediaType.APPLICATION_OCTET_STREAM
                }
                response.contentType = mediaType.toString()
                response.setHeader("Content-disposition", "attachment; filename=\"" + file.name + "\"")
                val inputStream = FileInputStream(file)
                FileCopyUtils.copy(inputStream, response.outputStream)
            }
        } catch (e: Exception) {
            log.error(" file is not found exception is {} ", e)
        }

    }
}