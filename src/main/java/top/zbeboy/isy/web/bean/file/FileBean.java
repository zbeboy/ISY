package top.zbeboy.isy.web.bean.file;

import lombok.Data;

/**
 * Created by lenovo on 2016-10-30.
 */
@Data
public class FileBean {
    private String contentType;// 文件头信息
    private Long size;// 文件大小
    private String originalFileName;// 文件原始名字
    private String newName;// 文件新名字
    private String lastPath;// 服务器端最后保存路径
    private String ext;// 文件扩展名
}
