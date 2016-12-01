package top.zbeboy.isy.web.vo.internship.apply;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-12-01.
 */
public class GraduationPracticeCollegeVo {
    private String graduationPracticeCollegeId;
    @NotNull
    private Integer studentId;
    @NotNull
    @Size(max = 100)
    private String internshipReleaseId;

    public String getGraduationPracticeCollegeId() {
        return graduationPracticeCollegeId;
    }

    public void setGraduationPracticeCollegeId(String graduationPracticeCollegeId) {
        this.graduationPracticeCollegeId = graduationPracticeCollegeId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
    }
}
