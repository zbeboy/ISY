package top.zbeboy.isy.web.bean.graduate.design.teacher;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;

/**
 * Created by zbeboy on 2017/5/8.
 */
public class GraduationDesignTeacherBean  extends GraduationDesignTeacher {
    private String realName;
    private String staffNumber;
    private String staffUsername;
    private String assignerName;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getStaffUsername() {
        return staffUsername;
    }

    public void setStaffUsername(String staffUsername) {
        this.staffUsername = staffUsername;
    }

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
    }
}
