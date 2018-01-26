package top.zbeboy.isy.service.export

import org.apache.poi.ss.usermodel.Row
import top.zbeboy.isy.service.util.ExportUtils
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean

/**
 * Created by zbeboy 2018-01-26 .
 **/
class GraduationDesignManifestExport(data:List<GraduationDesignDeclareBean>): ExportUtils<GraduationDesignDeclareBean>(data) {

    // 序号
    private var sequence = 0

    override fun createHeader(row: Row) {
        row.createCell(0).setCellValue("序号")
        row.createCell(1).setCellValue("论文题目")
        row.createCell(2).setCellValue("毕业设计(论文)课题")
        row.createCell(3).setCellValue("题目类型")
        row.createCell(4).setCellValue("指导教师")
        row.createCell(5).setCellValue("教师职称")
        row.createCell(6).setCellValue("指导学生人数")
        row.createCell(7).setCellValue("指导学生学号")
        row.createCell(8).setCellValue("学生姓名")
        row.createCell(9).setCellValue("成绩")
    }

    override fun createCell(row: Row, t: GraduationDesignDeclareBean) {
        sequence++
        row.createCell(0).setCellValue(sequence.toDouble())
        row.createCell(1).setCellValue(t.presubjectTitle)
        row.createCell(2).setCellValue(t.subjectTypeName)
        row.createCell(3).setCellValue(t.originTypeName)
        row.createCell(4).setCellValue(t.staffName)
        row.createCell(5).setCellValue(t.academicTitleName)
        row.createCell(6).setCellValue(numIsNull(t.guidePeoples)!!.toDouble())
        row.createCell(7).setCellValue(t.studentNumber)
        row.createCell(8).setCellValue(t.studentName)
        row.createCell(9).setCellValue(t.scoreTypeName)
    }

    private fun numIsNull(param: Int?): Int? {
        return param ?: 0
    }
}