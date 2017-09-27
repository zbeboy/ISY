package top.zbeboy.isy.web.bean.graduate.design.proposal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup;

@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignDatumGroupBean extends GraduationDesignDatumGroup {
    private String size;
    private String originalFileName;
    private String newName;
    private String relativePath;
    private String ext;
    private String uploadTimeStr;
    private String graduationDesignReleaseId;
}
