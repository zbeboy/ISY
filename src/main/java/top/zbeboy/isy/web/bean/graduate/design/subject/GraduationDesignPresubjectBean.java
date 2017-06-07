package top.zbeboy.isy.web.bean.graduate.design.subject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject;

/**
 * Created by zbeboy on 2017/6/5.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignPresubjectBean extends GraduationDesignPresubject {
    private String updateTimeStr;
    private String realName;
    private String studentNumber;
    private String organizeName;
    private int staffId;
}
