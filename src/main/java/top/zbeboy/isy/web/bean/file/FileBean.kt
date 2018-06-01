package top.zbeboy.isy.web.bean.file

/**
 * Created by zbeboy 2017-11-19 .
 **/
open class FileBean {
    var contentType: String? = null// 文件头信息
    var size: Long? = null// 文件大小
    var originalFileName: String? = null// 文件原始名字
    var newName: String? = null// 文件新名字
    var lastPath: String? = null// 服务器端最后保存路径
    var ext: String? = null// 文件扩展名
}