package top.zbeboy.isy.service.common

import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.TextAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.daos.FilesDao
import top.zbeboy.isy.domain.tables.pojos.Files
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.service.util.OperatorWordUtils
import top.zbeboy.isy.service.util.RequestUtils
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-20 .
 **/
@Service("filesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class FilesServiceImpl : FilesService {

    private val log = LoggerFactory.getLogger(FilesServiceImpl::class.java)

    @Resource
    open lateinit var filesDao: FilesDao


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(files: Files) {
        filesDao.insert(files)
    }

    override fun deleteById(fileId: String) {
        filesDao.deleteById(fileId)
    }

    override fun findById(id: String): Files {
        return filesDao.findById(id)
    }

    @Async
    override fun saveInternshipJournal(internshipJournal: InternshipJournal, users: Users, request: HttpServletRequest): String {
        var outputPath = ""
        try {
            val templatePath = Workbook.INTERNSHIP_JOURNAL_FILE_PATH
            val `is` = FileInputStream(templatePath)
            val cellMap = HashMap<String, String>()
            cellMap.put("\${studentName}", internshipJournal.studentName)
            cellMap.put("\${studentNumber}", internshipJournal.studentNumber)
            cellMap.put("\${organize}", internshipJournal.organize)
            cellMap.put("\${schoolGuidanceTeacher}", internshipJournal.schoolGuidanceTeacher)
            cellMap.put("\${graduationPracticeCompanyName}", internshipJournal.graduationPracticeCompanyName)

            val paraMap = HashMap<String, String>()
            paraMap.put("\${internshipJournalContent}", internshipJournal.internshipJournalContent)
            paraMap.put("\${date}", internshipJournal.internshipJournalDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")))

            val doc = XWPFDocument(`is`)

            val itTable = doc.tablesIterator
            while (itTable.hasNext()) {
                val table = itTable.next()
                val rcount = table.numberOfRows
                for (i in 0 until rcount) {
                    val row = table.getRow(i)
                    val cells = row.tableCells
                    for (cell in cells) {
                        val itParas = cell.paragraphs
                        for (itPara in itParas) {

                            val runs = itPara.runs
                            for (run in runs) {
                                var oneparaString = run.getText(
                                        run.textPosition)

                                for ((key, value) in paraMap) {
                                    oneparaString = oneparaString.replace(
                                            key, value)
                                }

                                run.setText(oneparaString, 0)
                            }
                        }

                        var cellTextString = cell.text
                        for ((key, value) in cellMap) {
                            if (cellTextString.contains(key)) {

                                cellTextString = cellTextString.replace(key,
                                        value)
                                cell.removeParagraph(0)
                                cell.text = cellTextString
                            }

                        }

                    }
                }
            }

            val path = RequestUtils.getRealPath(request) + Workbook.internshipJournalPath(users)
            val filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".docx"
            val saveFile = File(path)
            if (!saveFile.exists()) {
                saveFile.mkdirs()
            }
            val os = FileOutputStream(path + filename)
            //把doc输出到输出流中
            doc.write(os)
            log.info("Save journal path {}", path)
            outputPath = Workbook.internshipJournalPath(users) + filename
            this.closeStream(os)
            this.closeStream(`is`)
            log.info("Save internship journal finish, the path is {}", outputPath)
        } catch (e: IOException) {
            log.error("Save internship journal error,error is {}", e)
            return outputPath
        }

        return outputPath
    }

    override fun saveGraduationDesignPlan(users: Users, request: HttpServletRequest, graduationDesignTutorBeanList: List<GraduationDesignTutorBean>,
                                          graduationDesignPlanBeanList: List<GraduationDesignPlanBean>): String {
        var outputPath = ""
        try {
            val wordUtils = OperatorWordUtils()
            val xdoc = XWPFDocument()

            var p = xdoc.createParagraph()
            // 固定值25磅
            wordUtils.setParagraphSpacingInfo(p, true, "0", "80", null, null, true, "500",
                    STLineSpacingRule.EXACT)
            // 居中
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.CENTER,
                    TextAlignment.CENTER)
            var pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "毕业设计（论文）指导进度安排（计划）", "宋体",
                    "Times New Roman", "36", true, false, false, false, null, null,
                    0, 0, 90)

            p = xdoc.createParagraph()
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO)
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导教师：" + users.realName, "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90)

            p = xdoc.createParagraph()
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO)
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导学生：", "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90)

            val studentTable = xdoc.createTable(graduationDesignTutorBeanList.size + 1, 4)
            wordUtils.setTableBorders(studentTable, STBorder.SINGLE, "4", "auto", "0")
            wordUtils.setTableWidthAndHAlign(studentTable, "9024", STJc.CENTER)
            wordUtils.setTableCellMargin(studentTable, 0, 108, 0, 108)
            val studentColWidths = intArrayOf(1000, 1000, 1000, 1000)
            wordUtils.setTableGridCol(studentTable, studentColWidths)

            var studentRow = studentTable.getRow(0)
            wordUtils.setRowHeight(studentRow, "460", STHeightRule.AT_LEAST)
            var studentCell = studentRow.getCell(0)
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(studentCell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "序号", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            studentCell = studentRow.getCell(1)
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(studentCell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "姓名", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            studentCell = studentRow.getCell(2)
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(studentCell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "学号", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            studentCell = studentRow.getCell(3)
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(studentCell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "班级", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)
            var studentRowPlus = 1
            // 循环学生
            for (graduationDesignTutorBean in graduationDesignTutorBeanList) {
                studentRow = studentTable.getRow(studentRowPlus)
                wordUtils.setRowHeight(studentRow, "460", STHeightRule.AT_LEAST)

                studentCell = studentRow.getCell(0)
                p = wordUtils.getCellFirstParagraph(studentCell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, studentRowPlus.toString() + "", "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                studentCell = studentRow.getCell(1)
                p = wordUtils.getCellFirstParagraph(studentCell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.realName!!, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                studentCell = studentRow.getCell(2)
                p = wordUtils.getCellFirstParagraph(studentCell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.studentNumber!!, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                studentCell = studentRow.getCell(3)
                p = wordUtils.getCellFirstParagraph(studentCell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.organizeName!!, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                studentRowPlus++
            }

            p = xdoc.createParagraph()
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO)
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "具体的进度安排", "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90)

            // 创建表格21行5列
            val table = xdoc.createTable(graduationDesignPlanBeanList.size + 1, 5)
            wordUtils.setTableBorders(table, STBorder.SINGLE, "4", "auto", "0")
            wordUtils.setTableWidthAndHAlign(table, "9024", STJc.CENTER)
            wordUtils.setTableCellMargin(table, 0, 108, 0, 108)
            val colWidths = intArrayOf(1800, 1800, 1800, 2000, 1800)
            wordUtils.setTableGridCol(table, colWidths)

            var row = table.getRow(0)
            wordUtils.setRowHeight(row, "460", STHeightRule.AT_LEAST)
            var cell = row.getCell(0)
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(cell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "进度安排", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            cell = row.getCell(1)
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(cell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导时间", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            cell = row.getCell(2)
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(cell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导地点", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            cell = row.getCell(3)
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(cell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导内容", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            cell = row.getCell(4)
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null)
            p = wordUtils.getCellFirstParagraph(cell)
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
            wordUtils.setParagraphRunFontInfo(p, pRun, "备注", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0)

            // 循环规划内容
            var rowPlus = 1
            for (graduationDesignPlanBean in graduationDesignPlanBeanList) {
                row = table.getRow(rowPlus)
                wordUtils.setRowHeight(row, "800", STHeightRule.AT_LEAST)

                cell = row.getCell(0)
                p = wordUtils.getCellFirstParagraph(cell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.scheduling, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                cell = row.getCell(1)
                p = wordUtils.getCellFirstParagraph(cell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.supervisionTime, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                cell = row.getCell(2)
                p = wordUtils.getCellFirstParagraph(cell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.buildingName + " " + graduationDesignPlanBean.buildingCode, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                cell = row.getCell(3)
                p = wordUtils.getCellFirstParagraph(cell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.guideContent, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                cell = row.getCell(4)
                p = wordUtils.getCellFirstParagraph(cell)
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false)
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.note, "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0)

                rowPlus++
            }

            val path = RequestUtils.getRealPath(request) + Workbook.graduationDesignPlanPath(users)
            val filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".docx"
            val saveFile = File(path)
            if (!saveFile.exists()) {
                saveFile.mkdirs()
            }

            wordUtils.saveDocument(xdoc, path + filename)

            outputPath = Workbook.graduationDesignPlanPath(users) + filename
        } catch (e: Exception) {
            log.error("Save graduation design plan error,error is {}", e)
            return outputPath
        }

        return outputPath
    }

    /**
     * 关闭输入流
     *
     * @param is 流
     */
    private fun closeStream(`is`: InputStream?) {
        if (`is` != null) {
            try {
                `is`.close()
            } catch (e: IOException) {
                log.error("Close file is error, error {}", e)
            }

        }
    }

    /**
     * 关闭输出流
     *
     * @param os 流
     */
    private fun closeStream(os: OutputStream?) {
        if (os != null) {
            try {
                os.close()
            } catch (e: IOException) {
                log.error("Close file is error, error {}", e)
            }

        }
    }
}