package top.zbeboy.isy.service.export;

import org.apache.poi.ss.usermodel.Row;
import top.zbeboy.isy.service.util.ExportUtils;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;

import java.util.List;

/**
 * Created by zbeboy on 2017/8/2.
 */
public class GraduationDesignManifestExport extends ExportUtils<GraduationDesignDeclareBean> {

    // 序号
    private int sequence = 0;

    public GraduationDesignManifestExport(List<GraduationDesignDeclareBean> data) {
        super(data);
    }

    @Override
    public void createHeader(Row row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("论文题目");
        row.createCell(2).setCellValue("毕业设计(论文)课题");
        row.createCell(3).setCellValue("题目类型");
        row.createCell(4).setCellValue("指导教师");
        row.createCell(5).setCellValue("教师职称");
        row.createCell(6).setCellValue("指导学生人数");
        row.createCell(7).setCellValue("指导学生学号");
        row.createCell(8).setCellValue("学生姓名");
        row.createCell(9).setCellValue("成绩");
    }

    @Override
    public void createCell(Row row, GraduationDesignDeclareBean graduationDesignDeclareBean) {
        sequence ++ ;
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(graduationDesignDeclareBean.getPresubjectTitle());
        row.createCell(2).setCellValue(graduationDesignDeclareBean.getSubjectTypeName());
        row.createCell(3).setCellValue(graduationDesignDeclareBean.getOriginTypeName());
        row.createCell(4).setCellValue(graduationDesignDeclareBean.getStaffName());
        row.createCell(5).setCellValue(graduationDesignDeclareBean.getAcademicTitleName());
        row.createCell(6).setCellValue(graduationDesignDeclareBean.getGuidePeoples());
        row.createCell(7).setCellValue(graduationDesignDeclareBean.getStudentNumber());
        row.createCell(8).setCellValue(graduationDesignDeclareBean.getStudentName());
        row.createCell(9).setCellValue(graduationDesignDeclareBean.getScoreTypeName());
    }
}
