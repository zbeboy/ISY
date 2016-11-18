package top.zbeboy.isy.web.bean.file;

/**
 * Created by lenovo on 2016-10-30.
 */
public class FileBean {
    private String contentType;// 文件头信息
    private Long size;// 文件大小
    private String originalFileName;// 文件原始名字
    private String newName;// 文件新名字
    private String lastPath;// 服务器端最后保存路径
    private String ext;// 文件扩展名

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "contentType='" + contentType + '\'' +
                ", size=" + size +
                ", originalFileName='" + originalFileName + '\'' +
                ", newName='" + newName + '\'' +
                ", lastPath='" + lastPath + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }
}
