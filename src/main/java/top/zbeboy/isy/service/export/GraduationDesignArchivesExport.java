package top.zbeboy.isy.service.export;

import org.apache.poi.ss.usermodel.Row;
import top.zbeboy.isy.service.util.ExportUtils;
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;

import java.util.List;

/**
 * Created by lenovo on 2017-08-07.
 */
public class GraduationDesignArchivesExport extends ExportUtils<GraduationDesignArchivesBean> {

    // 序号
    private int sequence = 0;

    public GraduationDesignArchivesExport(List<GraduationDesignArchivesBean> data) {
        super(data);
    }

    @Override
    public void createHeader(Row row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("学院");
        row.createCell(2).setCellValue("学院校内代码");
        row.createCell(3).setCellValue("专业");
        row.createCell(4).setCellValue("校内专业代码");
        row.createCell(5).setCellValue("毕业时间");
        row.createCell(6).setCellValue("指导教师");
        row.createCell(7).setCellValue("指导教师工号");
        row.createCell(8).setCellValue("指导教师职称");
        row.createCell(9).setCellValue("助理教师");
        row.createCell(10).setCellValue("助理教师工号");
        row.createCell(11).setCellValue("助理教师职称");
        row.createCell(12).setCellValue("毕业设计（论文）题目");
        row.createCell(13).setCellValue("题目类型");
        row.createCell(14).setCellValue("题目来源");
        row.createCell(15).setCellValue("学生学号");
        row.createCell(16).setCellValue("学生姓名");
        row.createCell(17).setCellValue("成绩");
        row.createCell(18).setCellValue("是否学校百篇优秀论文");
        row.createCell(19).setCellValue("存档号");
        row.createCell(20).setCellValue("备注");
    }

    @Override
    public void createCell(Row row, GraduationDesignArchivesBean graduationDesignArchivesBean) {
        sequence++;
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(graduationDesignArchivesBean.getCollegeName());
        row.createCell(2).setCellValue(graduationDesignArchivesBean.getCollegeCode());
        row.createCell(3).setCellValue(graduationDesignArchivesBean.getScienceName());
        row.createCell(4).setCellValue(graduationDesignArchivesBean.getScienceCode());
        row.createCell(5).setCellValue(graduationDesignArchivesBean.getGraduationDate());
        row.createCell(6).setCellValue(graduationDesignArchivesBean.getStaffName());
        row.createCell(7).setCellValue(graduationDesignArchivesBean.getStaffNumber());
        row.createCell(8).setCellValue(graduationDesignArchivesBean.getAcademicTitleName());
        row.createCell(9).setCellValue(graduationDesignArchivesBean.getAssistantTeacher());
        row.createCell(10).setCellValue(graduationDesignArchivesBean.getAssistantTeacherNumber());
        row.createCell(11).setCellValue(graduationDesignArchivesBean.getAssistantTeacherAcademic());
        row.createCell(12).setCellValue(graduationDesignArchivesBean.getPresubjectTitle());
        row.createCell(13).setCellValue(graduationDesignArchivesBean.getSubjectTypeName());
        row.createCell(14).setCellValue(graduationDesignArchivesBean.getOriginTypeName());
        row.createCell(15).setCellValue(graduationDesignArchivesBean.getStudentNumber());
        row.createCell(16).setCellValue(graduationDesignArchivesBean.getStudentName());
        row.createCell(17).setCellValue(graduationDesignArchivesBean.getScoreTypeName());
        row.createCell(18).setCellValue(dealByte(graduationDesignArchivesBean.getIsExcellent()));
        row.createCell(19).setCellValue(graduationDesignArchivesBean.getArchiveNumber());
        row.createCell(20).setCellValue(graduationDesignArchivesBean.getNote());
    }

    public String dealByte(Byte b) {
        if (b != null && b == 1) {
            return "是";
        }
        return "否";
    }
}
