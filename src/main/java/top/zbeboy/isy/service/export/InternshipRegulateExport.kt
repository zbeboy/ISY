package top.zbeboy.isy.service.export

import org.apache.poi.ss.usermodel.Row
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.ExportUtils
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean

/**
 * Created by zbeboy 2017-12-29 .
 **/
class InternshipRegulateExport(data:List<InternshipRegulateBean> ): ExportUtils<InternshipRegulateBean>(data) {

    // 序号
    private var sequence = 0

    override fun createHeader(row: Row) {
        row.createCell(0).setCellValue("序号")
        row.createCell(1).setCellValue("学生姓名")
        row.createCell(2).setCellValue("学号")
        row.createCell(3).setCellValue("联系方式")
        row.createCell(4).setCellValue("实习内容")
        row.createCell(5).setCellValue("实习进展（周报）")
        row.createCell(6).setCellValue("汇报信息途径（电话、QQ、邮箱、见面沟通等）")
        row.createCell(7).setCellValue("汇报日期")
        row.createCell(8).setCellValue("指导教师")
        row.createCell(9).setCellValue("备注（有无变更单位、脱岗或联系不上等具体情况备注）")
    }

    override fun createCell(row: Row, t: InternshipRegulateBean) {
        sequence++
        row.createCell(0).setCellValue(sequence.toDouble())
        row.createCell(1).setCellValue(t.studentName)
        row.createCell(2).setCellValue(t.studentNumber)
        row.createCell(3).setCellValue(t.studentTel)
        row.createCell(4).setCellValue(t.internshipContent)
        row.createCell(5).setCellValue(t.internshipProgress)
        row.createCell(6).setCellValue(t.reportWay)
        row.createCell(7).setCellValue(DateTimeUtils.formatDate(t.reportDate, "yyyy-MM-dd"))
        row.createCell(8).setCellValue(t.schoolGuidanceTeacher)
        row.createCell(9).setCellValue(t.tliy)
    }
}