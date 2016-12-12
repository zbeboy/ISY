package top.zbeboy.isy.web.bean.internship.statistics;

import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory;

/**
 * Created by lenovo on 2016-12-12.
 */
public class InternshipChangeHistoryBean extends InternshipChangeHistory {
    private String internshipTitle;
    private String realName;
    private String studentNumber;
    private String organizeName;

    public String getInternshipTitle() {
        return internshipTitle;
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

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
