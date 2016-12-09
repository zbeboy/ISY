package top.zbeboy.isy.web.bean.internship.review;

import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege;

/**
 * Created by zbeboy on 2016/12/9.
 */
public class GraduationPracticeCollegeBean extends GraduationPracticeCollege {
    private String realName;
    private String studentNumber;
    private String organizeName;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }
}
