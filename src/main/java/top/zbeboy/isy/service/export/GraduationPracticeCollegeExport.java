package top.zbeboy.isy.service.export;

import org.apache.poi.ss.usermodel.Row;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.ExportUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-01-07.
 */
public class GraduationPracticeCollegeExport extends ExportUtils<GraduationPracticeCollege> {

    // 序号
    private int sequence = 0;

    /**
     * 初始化数据
     *
     * @param data 数据
     */
    public GraduationPracticeCollegeExport(List<GraduationPracticeCollege> data) {
        super(data);
    }

    @Override
    public void createHeader(Row row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("学生姓名");
        row.createCell(2).setCellValue("专业班级");
        row.createCell(3).setCellValue("性别");
        row.createCell(4).setCellValue("学号");
        row.createCell(5).setCellValue("电话号码");
        row.createCell(6).setCellValue("qq邮箱");
        row.createCell(7).setCellValue("父母联系方式");
        row.createCell(8).setCellValue("班主任");
        row.createCell(9).setCellValue("班主任联系方式");
        row.createCell(10).setCellValue("实习单位名称");
        row.createCell(11).setCellValue("实习单位地址");
        row.createCell(12).setCellValue("实习单位联系人");
        row.createCell(13).setCellValue("实习单位联系人联系方式");
        row.createCell(14).setCellValue("校内指导教师");
        row.createCell(15).setCellValue("校内指导教师联系方式");
        row.createCell(16).setCellValue("实习开始时间");
        row.createCell(17).setCellValue("实习结束时间");
        row.createCell(18).setCellValue("承诺书");
        row.createCell(19).setCellValue("安全责任书");
        row.createCell(20).setCellValue("实践协议书");
        row.createCell(21).setCellValue("实习申请书");
        row.createCell(22).setCellValue("实习接收");
        row.createCell(23).setCellValue("安全教育协议");
        row.createCell(24).setCellValue("父母同意书");
    }

    @Override
    public void createCell(Row row, GraduationPracticeCollege graduationPracticeCollege) {
        sequence ++ ;
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(graduationPracticeCollege.getStudentName());
        row.createCell(2).setCellValue(graduationPracticeCollege.getCollegeClass());
        row.createCell(3).setCellValue(graduationPracticeCollege.getStudentSex());
        row.createCell(4).setCellValue(graduationPracticeCollege.getStudentNumber());
        row.createCell(5).setCellValue(graduationPracticeCollege.getPhoneNumber());
        row.createCell(6).setCellValue(graduationPracticeCollege.getQqMailbox());
        row.createCell(7).setCellValue(graduationPracticeCollege.getParentalContact());
        row.createCell(8).setCellValue(graduationPracticeCollege.getHeadmaster());
        row.createCell(9).setCellValue(graduationPracticeCollege.getHeadmasterContact());
        row.createCell(10).setCellValue(graduationPracticeCollege.getGraduationPracticeCollegeName());
        row.createCell(11).setCellValue(graduationPracticeCollege.getGraduationPracticeCollegeAddress());
        row.createCell(12).setCellValue(graduationPracticeCollege.getGraduationPracticeCollegeContacts());
        row.createCell(13).setCellValue(graduationPracticeCollege.getGraduationPracticeCollegeTel());
        row.createCell(14).setCellValue(graduationPracticeCollege.getSchoolGuidanceTeacher());
        row.createCell(15).setCellValue(graduationPracticeCollege.getSchoolGuidanceTeacherTel());
        row.createCell(16).setCellValue(DateTimeUtils.formatDate(graduationPracticeCollege.getStartTime(), "yyyy-MM-dd"));
        row.createCell(17).setCellValue(DateTimeUtils.formatDate(graduationPracticeCollege.getEndTime(), "yyyy-MM-dd"));
        row.createCell(18).setCellValue(dealByte(graduationPracticeCollege.getCommitmentBook()));
        row.createCell(19).setCellValue(dealByte(graduationPracticeCollege.getSafetyResponsibilityBook()));
        row.createCell(20).setCellValue(dealByte(graduationPracticeCollege.getPracticeAgreement()));
        row.createCell(21).setCellValue(dealByte(graduationPracticeCollege.getInternshipApplication()));
        row.createCell(22).setCellValue(dealByte(graduationPracticeCollege.getPracticeReceiving()));
        row.createCell(23).setCellValue(dealByte(graduationPracticeCollege.getSecurityEducationAgreement()));
        row.createCell(24).setCellValue(dealByte(graduationPracticeCollege.getParentalConsent()));
    }

    public String dealByte(Byte b) {
        if (b != null && b == 1) {
            return "已交";
        }
        return "未交";
    }
}
