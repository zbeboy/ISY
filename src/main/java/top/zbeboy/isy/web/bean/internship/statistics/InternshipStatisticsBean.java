package top.zbeboy.isy.web.bean.internship.statistics;

/**
 * Created by lenovo on 2016-12-10.
 */
public class InternshipStatisticsBean {
    private int studentId;
    private String internshipReleaseId;
    private String realName;
    private String studentNumber;
    private String scienceName;
    private String organizeName;
    private int internshipApplyState;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getInternshipReleaseId() {
        return internshipReleaseId;
    }

    public void setInternshipReleaseId(String internshipReleaseId) {
        this.internshipReleaseId = internshipReleaseId;
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

    public String getScienceName() {
        return scienceName;
    }

    public void setScienceName(String scienceName) {
        this.scienceName = scienceName;
    }

    public String getOrganizeName() {
        return organizeName;
    }

    public void setOrganizeName(String organizeName) {
        this.organizeName = organizeName;
    }

    public int getInternshipApplyState() {
        return internshipApplyState;
    }

    public void setInternshipApplyState(int internshipApplyState) {
        this.internshipApplyState = internshipApplyState;
    }
}
