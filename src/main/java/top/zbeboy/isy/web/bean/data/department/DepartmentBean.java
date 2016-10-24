package top.zbeboy.isy.web.bean.data.department;

import top.zbeboy.isy.domain.tables.pojos.Department;

/**
 * Created by lenovo on 2016/9/23.
 */
public class DepartmentBean extends Department {
    private String schoolName;
    private String collegeName;
    private int schoolId;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }
}
