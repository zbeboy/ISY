package top.zbeboy.isy.service.export

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.ExportUtils
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by zbeboy 2018-01-26 .
 **/
class GraduationDesignDeclareExport(data: List<GraduationDesignDeclareBean>,
                                    private val graduationDesignDeclareData: GraduationDesignDeclareData,
                                    private val guidePeoples: Int,
                                    private val year: String) : ExportUtils<GraduationDesignDeclareBean>(data) {
    // 序号
    private var sequence = 0

    @Throws(IOException::class)
    override fun exportExcel(outputPath: String, fileName: String, ext: String): Boolean {
        var isCreate = false
        var wb: Workbook? = null
        if (ext == top.zbeboy.isy.config.Workbook.XLS_FILE) {
            wb = HSSFWorkbook()
        } else if (ext == top.zbeboy.isy.config.Workbook.XLSX_FILE) {
            wb = XSSFWorkbook()
        }

        if (!ObjectUtils.isEmpty(wb)) {
            val sheet = wb!!.createSheet("申报表")
            if (!ObjectUtils.isEmpty(data)) {
                var row = sheet.createRow(0.toShort().toInt())
                row.heightInPoints = 36f

                var font = wb.createFont()
                font.fontHeightInPoints = 18.toShort()
                font.fontName = "黑体"

                var style = wb.createCellStyle()
                style.setFont(font)
                style.setAlignment(HorizontalAlignment.CENTER)
                style.setVerticalAlignment(VerticalAlignment.CENTER)

                var cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("昆明理工大学城市学院" + year + "届毕业设计(论文)题目申报表")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        0, //first row (0-based)
                        0, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                row = sheet.createRow(1.toShort().toInt())
                row.heightInPoints = 34f

                font = wb.createFont()
                font.fontHeightInPoints = 12.toShort()
                font.fontName = "宋体"

                style = wb.createCellStyle()
                style.setFont(font)
                style.setAlignment(HorizontalAlignment.CENTER)
                style.setVerticalAlignment(VerticalAlignment.CENTER)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("毕业时间:" + graduationDesignDeclareData.graduationDate +
                        "　　系:" + graduationDesignDeclareData.departmentName +
                        "  　专业:" + graduationDesignDeclareData.scienceName +
                        "　　　　 班级:" + replaceData(graduationDesignDeclareData.organizeNames) +
                        "　　　  班级人数:" + replaceData(graduationDesignDeclareData.organizePeoples) +
                        "　　　　  指导人数:" + guidePeoples)
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        1, //first row (0-based)
                        1, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                row = sheet.createRow(2.toShort().toInt())
                row.heightInPoints = 34f

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue(" 教研室主任(签名):                            系毕业设计工作领导小组组长 (签名):                    " + DateTimeUtils.formatDate(Date(), "yyyy年 MM月 dd日"))
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        2, //first row (0-based)
                        2, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                // column width
                sheet.setColumnWidth(0.toShort().toInt(), 1000.toShort().toInt())
                sheet.setColumnWidth(1.toShort().toInt(), 5000.toShort().toInt())
                sheet.setColumnWidth(2.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(3.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(4.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(5.toShort().toInt(), 2600.toShort().toInt())
                sheet.setColumnWidth(6.toShort().toInt(), 2600.toShort().toInt())
                sheet.setColumnWidth(7.toShort().toInt(), 3100.toShort().toInt())
                sheet.setColumnWidth(8.toShort().toInt(), 3100.toShort().toInt())
                sheet.setColumnWidth(9.toShort().toInt(), 3100.toShort().toInt())
                sheet.setColumnWidth(10.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(11.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(12.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(13.toShort().toInt(), 2200.toShort().toInt())
                sheet.setColumnWidth(14.toShort().toInt(), 3100.toShort().toInt())
                sheet.setColumnWidth(15.toShort().toInt(), 3100.toShort().toInt())
                sheet.setColumnWidth(16.toShort().toInt(), 3490.toShort().toInt())

                row = sheet.createRow(3.toShort().toInt())
                row.heightInPoints = 34f

                font = wb.createFont()
                font.fontHeightInPoints = 10.toShort()
                font.fontName = "宋体"

                style = wb.createCellStyle()
                style.setFont(font)
                style.setAlignment(HorizontalAlignment.CENTER)
                style.setVerticalAlignment(VerticalAlignment.CENTER)
                style.fillForegroundColor = IndexedColors.SKY_BLUE.getIndex()
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("编号")
                cell.cellStyle = style

                cell = row.createCell(1.toShort().toInt())
                cell.setCellValue("毕业设计(论文)课题")
                cell.cellStyle = style

                cell = row.createCell(2.toShort().toInt())
                cell.setCellValue("题目类型")
                cell.cellStyle = style

                cell = row.createCell(3.toShort().toInt())
                cell.setCellValue("课题来源")
                cell.cellStyle = style

                cell = row.createCell(4.toShort().toInt())
                cell.setCellValue("是否新题")
                cell.cellStyle = style

                cell = row.createCell(5.toShort().toInt())
                cell.setCellValue("新教师试做")
                cell.cellStyle = style

                cell = row.createCell(6.toShort().toInt())
                cell.setCellValue("新题目试做")
                cell.cellStyle = style

                cell = row.createCell(7.toShort().toInt())
                cell.setCellValue("旧题有无改进")
                cell.cellStyle = style

                cell = row.createCell(8.toShort().toInt())
                cell.setCellValue("旧题使用次数")
                cell.cellStyle = style

                cell = row.createCell(9.toShort().toInt())
                cell.setCellValue("计划上机学时")
                cell.cellStyle = style

                cell = row.createCell(10.toShort().toInt())
                cell.setCellValue("指导教师")
                cell.cellStyle = style

                cell = row.createCell(11.toShort().toInt())
                cell.setCellValue("教师职称")
                cell.cellStyle = style

                cell = row.createCell(12.toShort().toInt())
                cell.setCellValue("助理教师")
                cell.cellStyle = style

                cell = row.createCell(13.toShort().toInt())
                cell.setCellValue("教师职称")
                cell.cellStyle = style

                cell = row.createCell(14.toShort().toInt())
                cell.setCellValue("指导学生次数")
                cell.cellStyle = style

                cell = row.createCell(15.toShort().toInt())
                cell.setCellValue("指导学生人数")
                cell.cellStyle = style

                cell = row.createCell(16.toShort().toInt())
                cell.setCellValue("学号")
                cell.cellStyle = style

                cell = row.createCell(17.toShort().toInt())
                cell.setCellValue("学生姓名")
                cell.cellStyle = style

                style = wb.createCellStyle()
                style.setFont(font)
                style.setAlignment(HorizontalAlignment.CENTER)
                style.setVerticalAlignment(VerticalAlignment.CENTER)
                style.wrapText = true

                var rowLength = 4
                for (i in 0 until data.size) {
                    row = sheet.createRow(rowLength.toShort().toInt())
                    row.heightInPoints = 27f
                    createCell(row, data[i], style)
                    rowLength++
                }

                row = sheet.createRow(rowLength.toShort().toInt())
                row.heightInPoints = 15f

                style = wb.createCellStyle()
                style.setFont(font)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("填表说明：")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                rowLength++

                row = sheet.createRow(rowLength.toShort().toInt())
                row.heightInPoints = 15f

                style = wb.createCellStyle()
                style.setFont(font)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("1、由每位教师填写，题目请按主课题、子课题准确填写；")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                rowLength++

                row = sheet.createRow(rowLength.toShort().toInt())
                row.heightInPoints = 15f

                style = wb.createCellStyle()
                style.setFont(font)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("2、题型分类以《昆明理工大学毕业设计（论文）工作管理手册》为准；")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                rowLength++

                row = sheet.createRow(rowLength.toShort().toInt())
                row.heightInPoints = 15f

                style = wb.createCellStyle()
                style.setFont(font)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("3、教师在上报该表时，要同时上报选题报告交各系，以供审题；")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                rowLength++

                row = sheet.createRow(rowLength.toShort().toInt())
                row.heightInPoints = 15f

                style = wb.createCellStyle()
                style.setFont(font)

                cell = row.createCell(0.toShort().toInt())
                cell.setCellValue("4、以下几个栏目的内容必须使用下拉式菜单中的标准选择，不要自行填写与下拉式菜单中不同的内容：课题类型、课题来源、是否新题、新教师试做、新题目试做、旧题 有无改进、教师职称、助理职称。")
                cell.cellStyle = style

                sheet.addMergedRegion(CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ))

                val saveFile = File(outputPath, fileName + "." + ext)
                if (!saveFile.parentFile.exists()) {//create file
                    saveFile.parentFile.mkdirs()
                }
                // Write the output to a file
                val fileOut = FileOutputStream(outputPath + fileName + "." + ext)
                wb.write(fileOut)
                fileOut.close()
                isCreate = true
            }
        }
        return isCreate
    }

    override fun createCell(row: Row, t: GraduationDesignDeclareBean, style: CellStyle) {
        sequence++
        var cell = row.createCell(0)
        cell.setCellValue(sequence.toDouble())
        cell.cellStyle = style

        cell = row.createCell(1)
        cell.setCellValue(t.presubjectTitle)
        cell.cellStyle = style

        cell = row.createCell(2)
        cell.setCellValue(t.subjectTypeName)
        cell.cellStyle = style

        cell = row.createCell(3)
        cell.setCellValue(t.originTypeName)
        cell.cellStyle = style

        cell = row.createCell(4)
        cell.setCellValue(dealByte(t.isNewSubject))
        cell.cellStyle = style

        cell = row.createCell(5)
        cell.setCellValue(dealByte(t.isNewTeacherMake))
        cell.cellStyle = style

        cell = row.createCell(6)
        cell.setCellValue(dealByte(t.isNewSubjectMake))
        cell.cellStyle = style

        cell = row.createCell(7)
        cell.setCellValue(dealByte(t.isOldSubjectChange))
        cell.cellStyle = style

        cell = row.createCell(8)
        cell.setCellValue(isNull(t.oldSubjectUsesTimes)!!.toDouble())
        cell.cellStyle = style

        cell = row.createCell(9)
        cell.setCellValue(t.planPeriod)
        cell.cellStyle = style

        cell = row.createCell(10)
        cell.setCellValue(t.staffName)
        cell.cellStyle = style

        cell = row.createCell(11)
        cell.setCellValue(t.academicTitleName)
        cell.cellStyle = style

        cell = row.createCell(12)
        cell.setCellValue(t.assistantTeacher)
        cell.cellStyle = style

        cell = row.createCell(13)
        cell.setCellValue(t.assistantTeacherAcademic)
        cell.cellStyle = style

        cell = row.createCell(14)
        cell.setCellValue(isNull(t.guideTimes)!!.toDouble())
        cell.cellStyle = style

        cell = row.createCell(15)
        cell.setCellValue(guidePeoples.toDouble())
        cell.cellStyle = style

        cell = row.createCell(16)
        cell.setCellValue(t.studentNumber)
        cell.cellStyle = style

        cell = row.createCell(17)
        cell.setCellValue(t.studentName)
        cell.cellStyle = style
    }

    private fun dealByte(b: Byte?): String {
        return if (b != null && b == 1.toByte()) {
            "是"
        } else "否"
    }

    private fun isNull(param: Int?): Int? {
        return param ?: 0
    }

    private fun replaceData(str: String): String? {
        var temp = str
        if (StringUtils.hasLength(temp)) {
            temp = temp.replace("###".toRegex(), ",")
        }
        return temp
    }
}