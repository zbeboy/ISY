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
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GraduationDesignReleaseFile implements Serializable {

    private static final long serialVersionUID = -1014501069;

    private String graduationDesignReleaseId;
    private String fileId;

    public GraduationDesignReleaseFile() {}

    public GraduationDesignReleaseFile(GraduationDesignReleaseFile value) {
        this.graduationDesignReleaseId = value.graduationDesignReleaseId;
        this.fileId = value.fileId;
    }

    public GraduationDesignReleaseFile(
        String graduationDesignReleaseId,
        String fileId
    ) {
        this.graduationDesignReleaseId = graduationDesignReleaseId;
        this.fileId = fileId;
    }

    @NotNull
    @Size(max = 64)
    public String getGraduationDesignReleaseId() {
        return this.graduationDesignReleaseId;
    }

    public void setGraduationDesignReleaseId(String graduationDesignReleaseId) {
        this.graduationDesignReleaseId = graduationDesignReleaseId;
    }

    @NotNull
    @Size(max = 64)
    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GraduationDesignReleaseFile (");

        sb.append(graduationDesignReleaseId);
        sb.append(", ").append(fileId);

        sb.append(")");
        return sb.toString();
    }
}
