package top.zbeboy.isy.web.bean.graduate.design.teacher;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;

/**
 * Created by zbeboy on 2017/5/8.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignTeacherBean extends GraduationDesignTeacher {
    private String realName;
    private String staffNumber;
    private String staffUsername;
    private String staffMobile;
    private String assignerName;
    private int residueCount;
    // 用于选中
    private boolean selected;
    private String defenseGroupId;
    private String defenseGroupName;
    private String leaderId;
}
