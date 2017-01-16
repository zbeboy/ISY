package top.zbeboy.isy.service.export;

import org.apache.poi.ss.usermodel.Row;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.ExportUtils;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;

import java.util.List;

/**
 * Created by lenovo on 2017-01-16.
 */
public class InternshipRegulateExport extends ExportUtils<InternshipRegulateBean> {

    // 序号
    private int sequence = 0;

    /**
     * 初始化数据
     *
     * @param data 数据
     */
    public InternshipRegulateExport(List<InternshipRegulateBean> data) {
        super(data);
    }

    @Override
    public void createHeader(Row row) {
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("学生姓名");
        row.createCell(2).setCellValue("学号");
        row.createCell(3).setCellValue("联系方式");
        row.createCell(4).setCellValue("实习内容");
        row.createCell(5).setCellValue("实习进展（周报）");
        row.createCell(6).setCellValue("汇报信息途径（电话、QQ、邮箱、见面沟通等）");
        row.createCell(7).setCellValue("汇报日期");
        row.createCell(8).setCellValue("指导教师");
        row.createCell(9).setCellValue("备注（有无变更单位、脱岗或联系不上等具体情况备注）");
    }

    @Override
    public void createCell(Row row, InternshipRegulateBean internshipRegulateBean) {
        sequence ++ ;
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(internshipRegulateBean.getStudentName());
        row.createCell(2).setCellValue(internshipRegulateBean.getStudentNumber());
        row.createCell(3).setCellValue(internshipRegulateBean.getStudentTel());
        row.createCell(4).setCellValue(internshipRegulateBean.getInternshipContent());
        row.createCell(5).setCellValue(internshipRegulateBean.getInternshipProgress());
        row.createCell(6).setCellValue(internshipRegulateBean.getReportWay());
        row.createCell(7).setCellValue(DateTimeUtils.formatDate(internshipRegulateBean.getReportDate(), "yyyy-MM-dd"));
        row.createCell(8).setCellValue(internshipRegulateBean.getSchoolGuidanceTeacher());
        row.createCell(9).setCellValue(internshipRegulateBean.getTliy());
    }
}
