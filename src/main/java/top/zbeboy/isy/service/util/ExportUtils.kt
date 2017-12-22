package top.zbeboy.isy.service.util

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.util.ObjectUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by zbeboy 2017-11-30 .
 **/
open class ExportUtils<T>(var data: List<T>) {

    @Throws(IOException::class)
    open fun exportExcel(outputPath: String, fileName: String, ext: String): Boolean {
        var isCreate = false
        var wb: Workbook? = null
        if (ext == top.zbeboy.isy.config.Workbook.XLS_FILE) {
            wb = HSSFWorkbook()
        } else if (ext == top.zbeboy.isy.config.Workbook.XLSX_FILE) {
            wb = XSSFWorkbook()
        }

        if (!ObjectUtils.isEmpty(wb)) {
            val sheet = wb!!.createSheet("new sheet")
            var row = sheet.createRow(0)
            createHeader(row)
            for (i in data.indices) {
                row = sheet.createRow(i + 1)
                createCell(row, data[i])
            }
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
        return isCreate
    }

    /**
     * excel 标题
     *
     * @param row 列
     */
    open fun createHeader(row: Row) {
        // 实现
    }

    /**
     * 每行内容
     *
     * @param row 列
     * @param t   对象
     */
    open fun createCell(row: Row, t: T) {
        // 实现
    }

    /**
     * 每行内容
     *
     * @param row   列
     * @param t     对象
     * @param style 列格式
     */
    open fun createCell(row: Row, t: T, style: CellStyle) {
        // 实现
    }
}