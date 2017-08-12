package top.zbeboy.isy.web.bean.graduate.design.replan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember;

/**
 * Created by zbeboy on 2017/7/18.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DefenseGroupMemberBean extends DefenseGroupMember {
    private String studentNumber;
    private String studentName;
    private String studentMobile;
    private String subject;
    private String staffName;
    private int studentId;
}
