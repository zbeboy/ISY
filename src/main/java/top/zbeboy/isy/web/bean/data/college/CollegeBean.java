package top.zbeboy.isy.web.bean.data.college;

import top.zbeboy.isy.domain.tables.pojos.College;

/**
 * Created by lenovo on 2016-09-21.
 */
public class CollegeBean extends College {
    private String schoolName;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
