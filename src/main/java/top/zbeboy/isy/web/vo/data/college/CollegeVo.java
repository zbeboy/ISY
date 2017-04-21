package top.zbeboy.isy.web.vo.data.college;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016/9/22.
 */
public class CollegeVo {
    private Integer collegeId;
    @NotNull
    @Size(max = 200)
    private String collegeName;
    private Byte collegeIsDel;
    @NotNull
    @Min(1)
    private Integer schoolId;
    @NotNull
    @Size(max = 500)
    private String  collegeAddress;
    @NotNull
    @Size(max = 20)
    private String  collegeCode;

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Byte getCollegeIsDel() {
        return collegeIsDel;
    }

    public void setCollegeIsDel(Byte collegeIsDel) {
        this.collegeIsDel = collegeIsDel;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getCollegeAddress() {
        return collegeAddress;
    }

    public void setCollegeAddress(String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    @Override
    public String toString() {
        return "CollegeVo{" +
                "collegeId=" + collegeId +
                ", collegeName='" + collegeName + '\'' +
                ", collegeIsDel=" + collegeIsDel +
                ", schoolId=" + schoolId +
                ", collegeAddress='" + collegeAddress + '\'' +
                ", collegeCode='" + collegeCode + '\'' +
                '}';
    }
}
