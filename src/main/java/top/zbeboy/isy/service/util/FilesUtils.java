package top.zbeboy.isy.service.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by lenovo on 2015/9/11.
 * 文件操作工具类
 *
 * @author zbeboy
 * @version zbeboy
 * @since 1.0
 */
public class FilesUtils {

    private final Logger log = LoggerFactory.getLogger(FilesUtils.class);

    /**
     * 删除硬盘上的文件
     *
     * @param path 文件路径
     * @return true 删除成功，false 删除失败或路径为空，文件不存在
     * @throws IOException
     */
    public static boolean deleteFile(String path) throws IOException {
        if (!Objects.isNull(path) && "" != path.trim()) {
            File file = new File(path);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 转换文件单位
     *
     * @param size 文件大小
     * @return 文件尺寸
     */
    public static String transformationFileUnit(long size) {
        String str = "";
        if (size < 1024) {
            str = size + "B";
        } else if (size >= 1024 && size < 1024 * 1024) {
            str = (size / 1024) + "KB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            str = (size / (1024 * 1024)) + "MB";
        } else {
            str = (size / (1024 * 1024 * 1024)) + "GB";
        }

        return str;
    }
}
