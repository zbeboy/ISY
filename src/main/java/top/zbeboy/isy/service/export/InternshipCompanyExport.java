package top.zbeboy.isy.service.export;

import org.apache.poi.ss.usermodel.Row;
import top.zbeboy.isy.domain.tables.pojos.InternshipCompany;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.ExportUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/1/4.
 */
public class InternshipCompanyExport extends ExportUtils<InternshipCompany> {

    // 序号
    private int sequence = 0;

    public InternshipCompanyExport(List<InternshipCompany> data) {
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
    public void createCell(Row row, InternshipCompany internshipCompany) {
        sequence ++ ;
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(internshipCompany.getStudentName());
        row.createCell(2).setCellValue(internshipCompany.getCollegeClass());
        row.createCell(3).setCellValue(internshipCompany.getStudentSex());
        row.createCell(4).setCellValue(internshipCompany.getStudentNumber());
        row.createCell(5).setCellValue(internshipCompany.getPhoneNumber());
        row.createCell(6).setCellValue(internshipCompany.getQqMailbox());
        row.createCell(7).setCellValue(internshipCompany.getParentalContact());
        row.createCell(8).setCellValue(internshipCompany.getHeadmaster());
        row.createCell(9).setCellValue(internshipCompany.getHeadmasterContact());
        row.createCell(10).setCellValue(internshipCompany.getInternshipCompanyName());
        row.createCell(11).setCellValue(internshipCompany.getInternshipCompanyAddress());
        row.createCell(12).setCellValue(internshipCompany.getInternshipCompanyContacts());
        row.createCell(13).setCellValue(internshipCompany.getInternshipCompanyTel());
        row.createCell(14).setCellValue(internshipCompany.getSchoolGuidanceTeacher());
        row.createCell(15).setCellValue(internshipCompany.getSchoolGuidanceTeacherTel());
        row.createCell(16).setCellValue(DateTimeUtils.formatDate(internshipCompany.getStartTime(), "yyyy-MM-dd"));
        row.createCell(17).setCellValue(DateTimeUtils.formatDate(internshipCompany.getEndTime(), "yyyy-MM-dd"));
        row.createCell(18).setCellValue(dealByte(internshipCompany.getCommitmentBook()));
        row.createCell(19).setCellValue(dealByte(internshipCompany.getSafetyResponsibilityBook()));
        row.createCell(20).setCellValue(dealByte(internshipCompany.getPracticeAgreement()));
        row.createCell(21).setCellValue(dealByte(internshipCompany.getInternshipApplication()));
        row.createCell(22).setCellValue(dealByte(internshipCompany.getPracticeReceiving()));
        row.createCell(23).setCellValue(dealByte(internshipCompany.getSecurityEducationAgreement()));
        row.createCell(24).setCellValue(dealByte(internshipCompany.getParentalConsent()));
    }

    public String dealByte(Byte b) {
        if (b != null && b == 1) {
            return "已交";
        }
        return "未交";
    }
}
