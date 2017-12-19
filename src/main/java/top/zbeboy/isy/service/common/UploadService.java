package top.zbeboy.isy.service.common;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.zbeboy.isy.web.bean.file.FileBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lenovo on 2016-01-10.
 */
public interface UploadService {
    /**
     * 上传文件
     *
     * @param request 请求对象
     * @param path    根路径
     * @param address 地址
     * @return file data info.
     */
    List<FileBean> upload(MultipartHttpServletRequest request, String path, String address);

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param response 响应对象
     * @param request  请求对象
     */
    void download(String fileName, String filePath, HttpServletResponse response, HttpServletRequest request);

    /**
     * 图片显示
     *
     * @param filePath 完整路径带文件名以及后缀
     * @param request  请求
     * @param response 响应
     */
    void reviewPic(String filePath, HttpServletRequest request, HttpServletResponse response);
}
