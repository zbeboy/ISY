package top.zbeboy.isy.service.common

import org.springframework.web.multipart.MultipartHttpServletRequest
import top.zbeboy.isy.web.bean.file.FileBean
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface UploadService {
    /**
     * 上传文件
     *
     * @param request 请求对象
     * @param path    根路径
     * @param address 地址
     * @return file data info.
     */
    fun upload(request: MultipartHttpServletRequest, path: String, address: String): List<FileBean>

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param response 响应对象
     * @param request  请求对象
     */
    fun download(fileName: String, filePath: String, response: HttpServletResponse, request: HttpServletRequest)

    /**
     * 文件下载
     *
     * @param fileName     文件名
     * @param file          文件对象
     * @param response      响应对象
     * @param request       请求对象
     */
    fun download(fileName: String, file: File, response: HttpServletResponse, request: HttpServletRequest)

    /**
     * 图片显示
     *
     * @param filePath 完整路径带文件名以及后缀
     * @param request  请求
     * @param response 响应
     */
    fun reviewPic(filePath: String, request: HttpServletRequest, response: HttpServletResponse)
}