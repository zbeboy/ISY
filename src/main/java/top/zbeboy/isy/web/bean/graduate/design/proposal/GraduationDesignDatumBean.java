package top.zbeboy.isy.web.bean.graduate.design.proposal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum;

/**
 * Created by zbeboy on 2017/6/23.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignDatumBean extends GraduationDesignDatum {
    private String size;
    private String originalFileName;
    private String newName;
    private String relativePath;
    private String ext;
    private String updateTimeStr;
    private String graduationDesignReleaseId;
}
