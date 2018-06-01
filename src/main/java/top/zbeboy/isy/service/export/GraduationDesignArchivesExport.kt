package top.zbeboy.isy.service.export

import org.apache.poi.ss.usermodel.Row
import top.zbeboy.isy.service.util.ExportUtils
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean

/**
 * Created by zbeboy 2018-01-26 .
 **/
class GraduationDesignArchivesExport(data:List<GraduationDesignArchivesBean>): ExportUtils<GraduationDesignArchivesBean>(data) {

    // 序号
    private var sequence = 0

    override fun createHeader(row: Row) {
        row.createCell(0).setCellValue("序号")
        row.createCell(1).setCellValue("学院")
        row.createCell(2).setCellValue("学院校内代码")
        row.createCell(3).setCellValue("专业")
        row.createCell(4).setCellValue("校内专业代码")
        row.createCell(5).setCellValue("毕业时间")
        row.createCell(6).setCellValue("指导教师")
        row.createCell(7).setCellValue("指导教师工号")
        row.createCell(8).setCellValue("指导教师职称")
        row.createCell(9).setCellValue("助理教师")
        row.createCell(10).setCellValue("助理教师工号")
        row.createCell(11).setCellValue("助理教师职称")
        row.createCell(12).setCellValue("毕业设计（论文）题目")
        row.createCell(13).setCellValue("题目类型")
        row.createCell(14).setCellValue("题目来源")
        row.createCell(15).setCellValue("学生学号")
        row.createCell(16).setCellValue("学生姓名")
        row.createCell(17).setCellValue("成绩")
        row.createCell(18).setCellValue("是否学校百篇优秀论文")
        row.createCell(19).setCellValue("存档号")
        row.createCell(20).setCellValue("备注")
    }

    override fun createCell(row: Row, t: GraduationDesignArchivesBean) {
        sequence++
        row.createCell(0).setCellValue(sequence.toDouble())
        row.createCell(1).setCellValue(t.collegeName)
        row.createCell(2).setCellValue(t.collegeCode)
        row.createCell(3).setCellValue(t.scienceName)
        row.createCell(4).setCellValue(t.scienceCode)
        row.createCell(5).setCellValue(t.graduationDate)
        row.createCell(6).setCellValue(t.staffName)
        row.createCell(7).setCellValue(t.staffNumber)
        row.createCell(8).setCellValue(t.academicTitleName)
        row.createCell(9).setCellValue(t.assistantTeacher)
        row.createCell(10).setCellValue(t.assistantTeacherNumber)
        row.createCell(11).setCellValue(t.assistantTeacherAcademic)
        row.createCell(12).setCellValue(t.presubjectTitle)
        row.createCell(13).setCellValue(t.subjectTypeName)
        row.createCell(14).setCellValue(t.originTypeName)
        row.createCell(15).setCellValue(t.studentNumber)
        row.createCell(16).setCellValue(t.studentName)
        row.createCell(17).setCellValue(t.scoreTypeName)
        row.createCell(18).setCellValue(dealByte(t.isExcellent))
        row.createCell(19).setCellValue(t.archiveNumber)
        row.createCell(20).setCellValue(t.note)
    }

    private fun dealByte(b: Byte?): String {
        return if (b != null && b == 1.toByte()) {
            "是"
        } else "否"
    }
}