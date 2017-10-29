package top.zbeboy.isy.test

import org.apache.poi.hssf.usermodel.HSSFHeader
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Test
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by zbeboy 2017-10-29 .
 **/
class TestExcel {
    @Test
    @Throws(IOException::class)
    fun mergingCells() {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet("new sheet")

        val row = sheet.createRow(1.toShort().toInt())
        val cell = row.createCell(1.toShort().toInt())
        cell.setCellValue("This is a test of merging")

        sheet.addMergedRegion(CellRangeAddress(
                1, //first row (0-based)
                1, //last row  (0-based)
                1, //first column (0-based)
                2  //last column  (0-based)
        ))

        // Write the output to a file
        val fileOut = FileOutputStream("f:/workbook.xls")
        wb.write(fileOut)
        fileOut.close()
    }

    @Test
    @Throws(IOException::class)
    fun workWithFonts() {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet("new sheet")

        // Create a row and put some cells in it. Rows are 0 based.
        val row = sheet.createRow(1)

        // Create a new font and alter it.
        val font = wb.createFont()
        font.fontHeightInPoints = 24.toShort()
        font.fontName = "Courier New"
        font.italic = true
        font.strikeout = true

        // Fonts are set into a style so create a new one to use.
        val style = wb.createCellStyle()
        style.setFont(font)

        // Create a cell and put a value in it.
        val cell = row.createCell(1)
        cell.setCellValue("This is a test of fonts")
        cell.setCellStyle(style)

        // Write the output to a file
        val fileOut = FileOutputStream("f:/workbook.xls")
        wb.write(fileOut)
        fileOut.close()
    }

    @Test
    @Throws(IOException::class)
    fun headersAndFooters() {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet("new sheet")

        val header = sheet.header
        header.center = "Center Header"
        header.left = "Left Header"
        header.right = HSSFHeader.font("Stencil-Normal", "Italic") +
                HSSFHeader.fontSize(16.toShort()) + "Right w/ Stencil-Normal Italic font and size 16"

        val fileOut = FileOutputStream("f:/workbook.xls")
        wb.write(fileOut)
        fileOut.close()
    }

    @Test
    @Throws(IOException::class)
    fun fillsAndColors() {
        val wb = XSSFWorkbook()
        val sheet = wb.createSheet("new sheet")

        // Create a row and put some cells in it. Rows are 0 based.
        val row = sheet.createRow(1.toShort().toInt())

        // Aqua background
        var style = wb.createCellStyle()
        style.fillBackgroundColor = IndexedColors.AQUA.getIndex()
        style.setFillPattern(FillPatternType.BIG_SPOTS)
        var cell = row.createCell(1.toShort().toInt())
        cell.setCellValue("X")
        cell.cellStyle = style

        // Orange "foreground", foreground being the fill foreground not the font color.
        style = wb.createCellStyle()
        style.fillForegroundColor = IndexedColors.ORANGE.getIndex()
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        cell = row.createCell(2.toShort().toInt())
        cell.setCellValue("X")
        cell.cellStyle = style

        // Write the output to a file
        val fileOut = FileOutputStream("f:/workbook.xls")
        wb.write(fileOut)
        fileOut.close()
    }

    @Test
    @Throws(IOException::class)
    fun createGraduationDesignApply() {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet("申报表")

        val row1 = sheet.createRow(0.toShort().toInt())
        row1.heightInPoints = 36f

        val font1 = wb.createFont()
        font1.fontHeightInPoints = 18.toShort()
        font1.fontName = "黑体"

        val style1 = wb.createCellStyle()
        style1.setFont(font1)
        style1.setAlignment(HorizontalAlignment.CENTER)
        style1.setVerticalAlignment(VerticalAlignment.CENTER)

        val cell1 = row1.createCell(0.toShort().toInt())
        cell1.setCellValue("昆明理工大学城市学院2017届毕业设计(论文)题目申报表")
        cell1.setCellStyle(style1)

        sheet.addMergedRegion(CellRangeAddress(
                0, //first row (0-based)
                0, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ))

        val row2 = sheet.createRow(1.toShort().toInt())
        row2.heightInPoints = 34f

        val font2 = wb.createFont()
        font2.fontHeightInPoints = 12.toShort()
        font2.fontName = "宋体"

        val style2 = wb.createCellStyle()
        style2.setFont(font2)
        style2.setAlignment(HorizontalAlignment.CENTER)
        style2.setVerticalAlignment(VerticalAlignment.CENTER)

        val cell2 = row2.createCell(0.toShort().toInt())
        cell2.setCellValue("毕业时间:2017　　系:信息  　专业:计算机科学与技术　　　　 班级:1311、1312、1313　　　  班级人数:　　　　  指导人数:9")
        cell2.setCellStyle(style2)

        sheet.addMergedRegion(CellRangeAddress(
                1, //first row (0-based)
                1, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ))

        val row3 = sheet.createRow(2.toShort().toInt())
        row3.heightInPoints = 34f

        val font3 = wb.createFont()
        font3.fontHeightInPoints = 12.toShort()
        font3.fontName = "宋体"

        val style3 = wb.createCellStyle()
        style3.setFont(font3)
        style3.setAlignment(HorizontalAlignment.CENTER)
        style3.setVerticalAlignment(VerticalAlignment.CENTER)

        val cell3 = row3.createCell(0.toShort().toInt())
        cell3.setCellValue(" 教研室主任(签名):                            系毕业设计工作领导小组组长 (签名):                    2017  年  2月   28日")
        cell3.setCellStyle(style3)

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

        val row4 = sheet.createRow(3.toShort().toInt())
        row4.heightInPoints = 34f

        val font4 = wb.createFont()
        font4.fontHeightInPoints = 10.toShort()
        font4.fontName = "宋体"

        val style4 = wb.createCellStyle()
        style4.setFont(font4)
        style4.setAlignment(HorizontalAlignment.CENTER)
        style4.setVerticalAlignment(VerticalAlignment.CENTER)
        style4.fillForegroundColor = IndexedColors.SKY_BLUE.getIndex()
        style4.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        var cell4 = row4.createCell(0.toShort().toInt())
        cell4.setCellValue("编号")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(1.toShort().toInt())
        cell4.setCellValue("毕业设计(论文)课题")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(2.toShort().toInt())
        cell4.setCellValue("题目类型")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(3.toShort().toInt())
        cell4.setCellValue("课题来源")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(4.toShort().toInt())
        cell4.setCellValue("是否新题")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(5.toShort().toInt())
        cell4.setCellValue("新教师试做")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(6.toShort().toInt())
        cell4.setCellValue("新题目试做")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(7.toShort().toInt())
        cell4.setCellValue("旧题有无改进")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(8.toShort().toInt())
        cell4.setCellValue("旧题使用次数")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(9.toShort().toInt())
        cell4.setCellValue("计划上机学时")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(10.toShort().toInt())
        cell4.setCellValue("指导教师")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(11.toShort().toInt())
        cell4.setCellValue("教师职称")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(12.toShort().toInt())
        cell4.setCellValue("助理教师")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(13.toShort().toInt())
        cell4.setCellValue("教师职称")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(14.toShort().toInt())
        cell4.setCellValue("指导学生次数")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(15.toShort().toInt())
        cell4.setCellValue("指导学生人数")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(16.toShort().toInt())
        cell4.setCellValue("学号")
        cell4.setCellStyle(style4)

        cell4 = row4.createCell(17.toShort().toInt())
        cell4.setCellValue("学生姓名")
        cell4.setCellStyle(style4)

        val row5 = sheet.createRow(4.toShort().toInt())
        row5.heightInPoints = 27f

        val font5 = wb.createFont()
        font5.fontHeightInPoints = 10.toShort()
        font5.fontName = "宋体"

        val style5 = wb.createCellStyle()
        style5.setFont(font5)
        style5.setAlignment(HorizontalAlignment.CENTER)
        style5.setVerticalAlignment(VerticalAlignment.CENTER)
        style5.wrapText = true

        var cell5 = row5.createCell(0.toShort().toInt())
        cell5.setCellValue("1")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(1.toShort().toInt())
        cell5.setCellValue("时间超市App——时间商品挑选模块")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(2.toShort().toInt())
        cell5.setCellValue("软件型")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(3.toShort().toInt())
        cell5.setCellValue("生产")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(4.toShort().toInt())
        cell5.setCellValue("是")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(5.toShort().toInt())
        cell5.setCellValue("是")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(6.toShort().toInt())
        cell5.setCellValue("是")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(7.toShort().toInt())
        cell5.setCellValue("是")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(8.toShort().toInt())
        cell5.setCellValue("23")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(9.toShort().toInt())
        cell5.setCellValue("23")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(10.toShort().toInt())
        cell5.setCellValue("阿家酉榀")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(11.toShort().toInt())
        cell5.setCellValue("阿家酉榀")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(12.toShort().toInt())
        cell5.setCellValue("阿家酉榀")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(13.toShort().toInt())
        cell5.setCellValue("阿家酉榀")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(14.toShort().toInt())
        cell5.setCellValue("23")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(15.toShort().toInt())
        cell5.setCellValue("23")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(16.toShort().toInt())
        cell5.setCellValue("2012118505129")
        cell5.setCellStyle(style5)

        cell5 = row5.createCell(17.toShort().toInt())
        cell5.setCellValue("阿家酉榀")
        cell5.setCellStyle(style5)

        val row6 = sheet.createRow(5.toShort().toInt())
        row6.heightInPoints = 15f

        val font6 = wb.createFont()
        font6.fontHeightInPoints = 10.toShort()
        font6.fontName = "宋体"

        val style6 = wb.createCellStyle()
        style6.setFont(font6)

        val cell6 = row6.createCell(0.toShort().toInt())
        cell6.setCellValue("填表说明：")
        cell6.setCellStyle(style6)

        sheet.addMergedRegion(CellRangeAddress(
                5, //first row (0-based)
                5, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ))

        val row7 = sheet.createRow(6.toShort().toInt())
        row7.heightInPoints = 15f

        val font7 = wb.createFont()
        font7.fontHeightInPoints = 10.toShort()
        font7.fontName = "宋体"

        val style7 = wb.createCellStyle()
        style7.setFont(font7)

        val cell7 = row7.createCell(0.toShort().toInt())
        cell7.setCellValue("1、由每位教师填写，题目请按主课题、子课题准确填写；")
        cell7.setCellStyle(style7)

        sheet.addMergedRegion(CellRangeAddress(
                6, //first row (0-based)
                6, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ))

        // Write the output to a file
        val fileOut = FileOutputStream("f:/workbook.xls")
        wb.write(fileOut)
        fileOut.close()

        /*
        HSSFCellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setWrapText(true);    自动换行
        cell.setCellStyle(cellStyle);
         */
    }
}