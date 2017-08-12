package top.zbeboy.isy.service.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.FilesDao;
import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.util.OperatorWordUtils;
import top.zbeboy.isy.service.util.RequestUtils;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016-11-13.
 */
@Slf4j
@Service("filesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FilesServiceImpl implements FilesService {

    @Resource
    private FilesDao filesDao;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Files files) {
        filesDao.insert(files);
    }

    @Override
    public void deleteById(String fileId) {
        filesDao.deleteById(fileId);
    }

    @Override
    public Files findById(String id) {
        return filesDao.findById(id);
    }

    @Override
    public String saveInternshipJournal(InternshipJournal internshipJournal, Users users, HttpServletRequest request) {
        String outputPath = "";
        try {
            String templatePath = Workbook.INTERNSHIP_JOURNAL_FILE_PATH;
            InputStream is = new FileInputStream(templatePath);
            Map<String, String> cellMap = new HashMap<>();
            cellMap.put("${studentName}", internshipJournal.getStudentName());
            cellMap.put("${studentNumber}", internshipJournal.getStudentNumber());
            cellMap.put("${organize}", internshipJournal.getOrganize());
            cellMap.put("${schoolGuidanceTeacher}", internshipJournal.getSchoolGuidanceTeacher());
            cellMap.put("${graduationPracticeCompanyName}", internshipJournal.getGraduationPracticeCompanyName());

            Map<String, String> paraMap = new HashMap<>();
            paraMap.put("${internshipJournalContent}", internshipJournal.getInternshipJournalContent());
            paraMap.put("${date}", internshipJournal.getInternshipJournalDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));

            XWPFDocument doc = new XWPFDocument(is);

            Iterator<XWPFTable> itTable = doc.getTablesIterator();
            while (itTable.hasNext()) {
                XWPFTable table = itTable.next();
                int rcount = table.getNumberOfRows();
                for (int i = 0; i < rcount; i++) {
                    XWPFTableRow row = table.getRow(i);
                    List<XWPFTableCell> cells = row.getTableCells();
                    for (XWPFTableCell cell : cells) {
                        List<XWPFParagraph> itParas = cell.getParagraphs();
                        for (XWPFParagraph itPara : itParas) {

                            List<XWPFRun> runs = itPara.getRuns();
                            for (XWPFRun run : runs) {
                                String oneparaString = run.getText(
                                        run.getTextPosition());

                                for (Map.Entry<String, String> entry : paraMap
                                        .entrySet()) {
                                    oneparaString = oneparaString.replace(
                                            entry.getKey(), entry.getValue());
                                }

                                run.setText(oneparaString, 0);
                            }
                        }

                        String cellTextString = cell.getText();
                        for (Map.Entry<String, String> e : cellMap.entrySet()) {
                            if (cellTextString.contains(e.getKey())) {

                                cellTextString = cellTextString.replace(e.getKey(),
                                        e.getValue());
                                cell.removeParagraph(0);
                                cell.setText(cellTextString);
                            }

                        }

                    }
                }
            }

            String path = RequestUtils.getRealPath(request) + Workbook.internshipJournalPath(users);
            String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".docx";
            File saveFile = new File(path);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            OutputStream os = new FileOutputStream(path + filename);
            //把doc输出到输出流中
            doc.write(os);
            log.info("Save journal path {}", path);
            outputPath = Workbook.internshipJournalPath(users) + filename;
            this.closeStream(os);
            this.closeStream(is);
            log.info("Save internship journal finish, the path is {}", outputPath);
        } catch (IOException e) {
            log.error("Save internship journal error,error is {}", e);
            return outputPath;
        }
        return outputPath;
    }

    @Override
    public String saveGraduationDesignPlan(Users users, HttpServletRequest request, List<GraduationDesignTutorBean> graduationDesignTutorBeanList,
                                           List<GraduationDesignPlanBean> graduationDesignPlanBeanList) {
        String outputPath = "";
        try {
            OperatorWordUtils wordUtils = new OperatorWordUtils();
            XWPFDocument xdoc = new XWPFDocument();

            XWPFParagraph p = xdoc.createParagraph();
            // 固定值25磅
            wordUtils.setParagraphSpacingInfo(p, true, "0", "80", null, null, true, "500",
                    STLineSpacingRule.EXACT);
            // 居中
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.CENTER,
                    TextAlignment.CENTER);
            XWPFRun pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "毕业设计（论文）指导进度安排（计划）", "宋体",
                    "Times New Roman", "36", true, false, false, false, null, null,
                    0, 0, 90);

            p = xdoc.createParagraph();
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO);
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导教师：" + users.getRealName(), "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90);

            p = xdoc.createParagraph();
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO);
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导学生：", "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90);

            XWPFTable studentTable = xdoc.createTable(graduationDesignTutorBeanList.size() + 1, 4);
            wordUtils.setTableBorders(studentTable, STBorder.SINGLE, "4", "auto", "0");
            wordUtils.setTableWidthAndHAlign(studentTable, "9024", STJc.CENTER);
            wordUtils.setTableCellMargin(studentTable, 0, 108, 0, 108);
            int[] studentColWidths = new int[]{1000, 1000, 1000, 1000};
            wordUtils.setTableGridCol(studentTable, studentColWidths);

            XWPFTableRow studentRow = studentTable.getRow(0);
            wordUtils.setRowHeight(studentRow, "460", STHeightRule.AT_LEAST);
            XWPFTableCell studentCell = studentRow.getCell(0);
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(studentCell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "序号", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            studentCell = studentRow.getCell(1);
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(studentCell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "姓名", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            studentCell = studentRow.getCell(2);
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(studentCell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "学号", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            studentCell = studentRow.getCell(3);
            wordUtils.setCellShdStyle(studentCell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(studentCell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "班级", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);
            int studentRowPlus = 1;
            // 循环学生
            for (GraduationDesignTutorBean graduationDesignTutorBean : graduationDesignTutorBeanList) {
                studentRow = studentTable.getRow(studentRowPlus);
                wordUtils.setRowHeight(studentRow, "460", STHeightRule.AT_LEAST);

                studentCell = studentRow.getCell(0);
                p = wordUtils.getCellFirstParagraph(studentCell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, studentRowPlus + "", "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                studentCell = studentRow.getCell(1);
                p = wordUtils.getCellFirstParagraph(studentCell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.getRealName(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                studentCell = studentRow.getCell(2);
                p = wordUtils.getCellFirstParagraph(studentCell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.getStudentNumber(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                studentCell = studentRow.getCell(3);
                p = wordUtils.getCellFirstParagraph(studentCell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignTutorBean.getOrganizeName(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                studentRowPlus++;
            }

            p = xdoc.createParagraph();
            // 单倍行距
            wordUtils.setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                    STLineSpacingRule.AUTO);
            wordUtils.setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                    TextAlignment.CENTER);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "具体的进度安排", "宋体", "Times New Roman",
                    "30", false, false, false, false, null, null, 0, 0, 90);

            // 创建表格21行5列
            XWPFTable table = xdoc.createTable(graduationDesignPlanBeanList.size() + 1, 5);
            wordUtils.setTableBorders(table, STBorder.SINGLE, "4", "auto", "0");
            wordUtils.setTableWidthAndHAlign(table, "9024", STJc.CENTER);
            wordUtils.setTableCellMargin(table, 0, 108, 0, 108);
            int[] colWidths = new int[]{1800, 1800, 1800, 2000, 1800};
            wordUtils.setTableGridCol(table, colWidths);

            XWPFTableRow row = table.getRow(0);
            wordUtils.setRowHeight(row, "460", STHeightRule.AT_LEAST);
            XWPFTableCell cell = row.getCell(0);
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(cell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "进度安排", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            cell = row.getCell(1);
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(cell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导时间", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            cell = row.getCell(2);
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(cell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导地点", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            cell = row.getCell(3);
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(cell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "指导内容", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            cell = row.getCell(4);
            wordUtils.setCellShdStyle(cell, true, "FFFFFF", null);
            p = wordUtils.getCellFirstParagraph(cell);
            pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
            wordUtils.setParagraphRunFontInfo(p, pRun, "备注", "宋体",
                    "Times New Roman", "24", true, false, false, false, null, null,
                    0, 6, 0);

            // 循环规划内容
            int rowPlus = 1;
            for (GraduationDesignPlanBean graduationDesignPlanBean : graduationDesignPlanBeanList) {
                row = table.getRow(rowPlus);
                wordUtils.setRowHeight(row, "800", STHeightRule.AT_LEAST);

                cell = row.getCell(0);
                p = wordUtils.getCellFirstParagraph(cell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.getScheduling(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                cell = row.getCell(1);
                p = wordUtils.getCellFirstParagraph(cell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.getSupervisionTime(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                cell = row.getCell(2);
                p = wordUtils.getCellFirstParagraph(cell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.getBuildingName() + " " + graduationDesignPlanBean.getBuildingCode(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                cell = row.getCell(3);
                p = wordUtils.getCellFirstParagraph(cell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.getGuideContent(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                cell = row.getCell(4);
                p = wordUtils.getCellFirstParagraph(cell);
                pRun = wordUtils.getOrAddParagraphFirstRun(p, false, false);
                wordUtils.setParagraphRunFontInfo(p, pRun, graduationDesignPlanBean.getNote(), "宋体", "Times New Roman",
                        "21", false, false, false, false, null, null, 0, 6, 0);

                rowPlus++;
            }

            String path = RequestUtils.getRealPath(request) + Workbook.graduationDesignPlanPath(users);
            String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + ".docx";
            File saveFile = new File(path);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }

            wordUtils.saveDocument(xdoc, path + filename);

            outputPath = Workbook.graduationDesignPlanPath(users) + filename;
        } catch (Exception e) {
            log.error("Save graduation design plan error,error is {}", e);
            return outputPath;
        }
        return outputPath;
    }

    /**
     * 关闭输入流
     *
     * @param is 流
     */
    private void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.error("Close file is error, error {}", e);
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param os 流
     */
    private void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                log.error("Close file is error, error {}", e);
            }
        }
    }
}
