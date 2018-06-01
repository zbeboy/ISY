package top.zbeboy.isy.service.export

import org.apache.poi.ss.usermodel.Row
import top.zbeboy.isy.service.util.ExportUtils
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean

/**
 * Created by zbeboy 2017-12-22 .
 **/
class InternshipTeacherDistributionExport(data: List<InternshipTeacherDistributionBean>) : ExportUtils<InternshipTeacherDistributionBean>(data) {

    // 序号
    private var sequence = 0

    override fun createHeader(row: Row) {
        row.createCell(0).setCellValue("序号")
        row.createCell(1).setCellValue("学生姓名")
        row.createCell(2).setCellValue("学生ID")
        row.createCell(3).setCellValue("学生学号")
        row.createCell(4).setCellValue("指导教师")
        row.createCell(5).setCellValue("指导教师ID")
        row.createCell(6).setCellValue("指导教师工号")
        row.createCell(7).setCellValue("分配人")
        row.createCell(8).setCellValue("分配人ID")
    }

    override fun createCell(row: Row, t: InternshipTeacherDistributionBean) {
        sequence++
        row.createCell(0).setCellValue(sequence.toDouble())
        row.createCell(1).setCellValue(t.studentRealName)
        row.createCell(2).setCellValue(t.studentUsername)
        row.createCell(3).setCellValue(t.studentNumber)
        row.createCell(4).setCellValue(t.staffRealName)
        row.createCell(5).setCellValue(t.staffUsername)
        row.createCell(6).setCellValue(t.staffNumber)
        row.createCell(7).setCellValue(t.assigner)
        row.createCell(8).setCellValue(t.username)
    }
}