/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Files implements Serializable {

    private static final long serialVersionUID = 1285765216;

    private String fileId;
    private String size;
    private String originalFileName;
    private String newName;
    private String relativePath;
    private String ext;

    public Files() {}

    public Files(Files value) {
        this.fileId = value.fileId;
        this.size = value.size;
        this.originalFileName = value.originalFileName;
        this.newName = value.newName;
        this.relativePath = value.relativePath;
        this.ext = value.ext;
    }

    public Files(
        String fileId,
        String size,
        String originalFileName,
        String newName,
        String relativePath,
        String ext
    ) {
        this.fileId = fileId;
        this.size = size;
        this.originalFileName = originalFileName;
        this.newName = newName;
        this.relativePath = relativePath;
        this.ext = ext;
    }

    @NotNull
    @Size(max = 64)
    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Size(max = 16777215)
    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Size(max = 300)
    public String getOriginalFileName() {
        return this.originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Size(max = 300)
    public String getNewName() {
        return this.newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Size(max = 800)
    public String getRelativePath() {
        return this.relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    @Size(max = 20)
    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Files (");

        sb.append(fileId);
        sb.append(", ").append(size);
        sb.append(", ").append(originalFileName);
        sb.append(", ").append(newName);
        sb.append(", ").append(relativePath);
        sb.append(", ").append(ext);

        sb.append(")");
        return sb.toString();
    }
}
