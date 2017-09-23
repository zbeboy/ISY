package top.zbeboy.isy.service.export;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.ExportUtils;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by zbeboy on 2017/6/19.
 */
public class GraduationDesignDeclareExport extends ExportUtils<GraduationDesignDeclareBean> {

    // 序号
    private int sequence = 0;

    private final GraduationDesignDeclareData graduationDesignDeclareData;

    private final int guidePeoples;

    private final String year;

    public GraduationDesignDeclareExport(List<GraduationDesignDeclareBean> data,
                                         GraduationDesignDeclareData graduationDesignDeclareData,
                                         int guidePeoples, String year) {
        super(data);
        this.graduationDesignDeclareData = graduationDesignDeclareData;
        this.guidePeoples = guidePeoples;
        this.year = year;
    }

    @Override
    public boolean exportExcel(String outputPath, String fileName, String ext) throws IOException {
        boolean isCreate = false;
        Workbook wb = null;
        if (ext.equals(top.zbeboy.isy.config.Workbook.XLS_FILE)) {
            wb = new HSSFWorkbook();
        } else if (ext.equals(top.zbeboy.isy.config.Workbook.XLSX_FILE)) {
            wb = new XSSFWorkbook();
        }

        if (!ObjectUtils.isEmpty(wb)) {
            Sheet sheet = wb.createSheet("申报表");
            if (!ObjectUtils.isEmpty(getData())) {
                Row row = sheet.createRow((short) 0);
                row.setHeightInPoints(36);

                Font font = wb.createFont();
                font.setFontHeightInPoints((short) 18);
                font.setFontName("黑体");

                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);

                Cell cell = row.createCell((short) 0);
                cell.setCellValue("昆明理工大学城市学院" + year + "届毕业设计(论文)题目申报表");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        0, //first row (0-based)
                        0, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                row = sheet.createRow((short) 1);
                row.setHeightInPoints(34);

                font = wb.createFont();
                font.setFontHeightInPoints((short) 12);
                font.setFontName("宋体");

                style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);

                cell = row.createCell((short) 0);
                cell.setCellValue("毕业时间:" + graduationDesignDeclareData.getGraduationDate() +
                        "　　系:" + graduationDesignDeclareData.getDepartmentName() +
                        "  　专业:" + graduationDesignDeclareData.getScienceName() +
                        "　　　　 班级:" + replaceData(graduationDesignDeclareData.getOrganizeNames()) +
                        "　　　  班级人数:" + replaceData(graduationDesignDeclareData.getOrganizePeoples()) +
                        "　　　　  指导人数:" + guidePeoples);
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        1, //first row (0-based)
                        1, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                row = sheet.createRow((short) 2);
                row.setHeightInPoints(34);

                cell = row.createCell((short) 0);
                cell.setCellValue(" 教研室主任(签名):                            系毕业设计工作领导小组组长 (签名):                    " + DateTimeUtils.formatDate(new Date(), "yyyy年 MM月 dd日"));
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        2, //first row (0-based)
                        2, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                // column width
                sheet.setColumnWidth((short) 0, (short) 1000);
                sheet.setColumnWidth((short) 1, (short) 5000);
                sheet.setColumnWidth((short) 2, (short) 2200);
                sheet.setColumnWidth((short) 3, (short) 2200);
                sheet.setColumnWidth((short) 4, (short) 2200);
                sheet.setColumnWidth((short) 5, (short) 2600);
                sheet.setColumnWidth((short) 6, (short) 2600);
                sheet.setColumnWidth((short) 7, (short) 3100);
                sheet.setColumnWidth((short) 8, (short) 3100);
                sheet.setColumnWidth((short) 9, (short) 3100);
                sheet.setColumnWidth((short) 10, (short) 2200);
                sheet.setColumnWidth((short) 11, (short) 2200);
                sheet.setColumnWidth((short) 12, (short) 2200);
                sheet.setColumnWidth((short) 13, (short) 2200);
                sheet.setColumnWidth((short) 14, (short) 3100);
                sheet.setColumnWidth((short) 15, (short) 3100);
                sheet.setColumnWidth((short) 16, (short) 3490);

                row = sheet.createRow((short) 3);
                row.setHeightInPoints(34);

                font = wb.createFont();
                font.setFontHeightInPoints((short) 10);
                font.setFontName("宋体");

                style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                cell = row.createCell((short) 0);
                cell.setCellValue("编号");
                cell.setCellStyle(style);

                cell = row.createCell((short) 1);
                cell.setCellValue("毕业设计(论文)课题");
                cell.setCellStyle(style);

                cell = row.createCell((short) 2);
                cell.setCellValue("题目类型");
                cell.setCellStyle(style);

                cell = row.createCell((short) 3);
                cell.setCellValue("课题来源");
                cell.setCellStyle(style);

                cell = row.createCell((short) 4);
                cell.setCellValue("是否新题");
                cell.setCellStyle(style);

                cell = row.createCell((short) 5);
                cell.setCellValue("新教师试做");
                cell.setCellStyle(style);

                cell = row.createCell((short) 6);
                cell.setCellValue("新题目试做");
                cell.setCellStyle(style);

                cell = row.createCell((short) 7);
                cell.setCellValue("旧题有无改进");
                cell.setCellStyle(style);

                cell = row.createCell((short) 8);
                cell.setCellValue("旧题使用次数");
                cell.setCellStyle(style);

                cell = row.createCell((short) 9);
                cell.setCellValue("计划上机学时");
                cell.setCellStyle(style);

                cell = row.createCell((short) 10);
                cell.setCellValue("指导教师");
                cell.setCellStyle(style);

                cell = row.createCell((short) 11);
                cell.setCellValue("教师职称");
                cell.setCellStyle(style);

                cell = row.createCell((short) 12);
                cell.setCellValue("助理教师");
                cell.setCellStyle(style);

                cell = row.createCell((short) 13);
                cell.setCellValue("教师职称");
                cell.setCellStyle(style);

                cell = row.createCell((short) 14);
                cell.setCellValue("指导学生次数");
                cell.setCellStyle(style);

                cell = row.createCell((short) 15);
                cell.setCellValue("指导学生人数");
                cell.setCellStyle(style);

                cell = row.createCell((short) 16);
                cell.setCellValue("学号");
                cell.setCellStyle(style);

                cell = row.createCell((short) 17);
                cell.setCellValue("学生姓名");
                cell.setCellStyle(style);

                style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setWrapText(true);

                int rowLength = 4;
                for (int i = 0; i < getData().size(); i++) {
                    row = sheet.createRow((short) rowLength);
                    row.setHeightInPoints(27);
                    createCell(row, getData().get(i), style);
                    rowLength++;
                }

                row = sheet.createRow((short) rowLength);
                row.setHeightInPoints(15);

                style = wb.createCellStyle();
                style.setFont(font);

                cell = row.createCell((short) 0);
                cell.setCellValue("填表说明：");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                rowLength++;

                row = sheet.createRow((short) rowLength);
                row.setHeightInPoints(15);

                style = wb.createCellStyle();
                style.setFont(font);

                cell = row.createCell((short) 0);
                cell.setCellValue("1、由每位教师填写，题目请按主课题、子课题准确填写；");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                rowLength++;

                row = sheet.createRow((short) rowLength);
                row.setHeightInPoints(15);

                style = wb.createCellStyle();
                style.setFont(font);

                cell = row.createCell((short) 0);
                cell.setCellValue("2、题型分类以《昆明理工大学毕业设计（论文）工作管理手册》为准；");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                rowLength++;

                row = sheet.createRow((short) rowLength);
                row.setHeightInPoints(15);

                style = wb.createCellStyle();
                style.setFont(font);

                cell = row.createCell((short) 0);
                cell.setCellValue("3、教师在上报该表时，要同时上报选题报告交各系，以供审题；");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                rowLength++;

                row = sheet.createRow((short) rowLength);
                row.setHeightInPoints(15);

                style = wb.createCellStyle();
                style.setFont(font);

                cell = row.createCell((short) 0);
                cell.setCellValue("4、以下几个栏目的内容必须使用下拉式菜单中的标准选择，不要自行填写与下拉式菜单中不同的内容：课题类型、课题来源、是否新题、新教师试做、新题目试做、旧题 有无改进、教师职称、助理职称。");
                cell.setCellStyle(style);

                sheet.addMergedRegion(new CellRangeAddress(
                        rowLength, //first row (0-based)
                        rowLength, //last row  (0-based)
                        0, //first column (0-based)
                        17  //last column  (0-based)
                ));

                File saveFile = new File(outputPath, fileName + "." + ext);
                if (!saveFile.getParentFile().exists()) {//create file
                    saveFile.getParentFile().mkdirs();
                }
                // Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(outputPath + fileName + "." + ext);
                wb.write(fileOut);
                fileOut.close();
                isCreate = true;
            }
        }
        return isCreate;
    }

    @Override
    public void createCell(Row row, GraduationDesignDeclareBean graduationDesignDeclareBean, CellStyle style) {
        sequence++;
        Cell cell = row.createCell(0);
        cell.setCellValue(sequence);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(graduationDesignDeclareBean.getPresubjectTitle());
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue(graduationDesignDeclareBean.getSubjectTypeName());
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(graduationDesignDeclareBean.getOriginTypeName());
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue(dealByte(graduationDesignDeclareBean.getIsNewSubject()));
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(dealByte(graduationDesignDeclareBean.getIsNewTeacherMake()));
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue(dealByte(graduationDesignDeclareBean.getIsNewSubjectMake()));
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(dealByte(graduationDesignDeclareBean.getIsOldSubjectChange()));
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue(isNull(graduationDesignDeclareBean.getOldSubjectUsesTimes()));
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue(graduationDesignDeclareBean.getPlanPeriod());
        cell.setCellStyle(style);

        cell = row.createCell(10);
        cell.setCellValue(graduationDesignDeclareBean.getStaffName());
        cell.setCellStyle(style);

        cell = row.createCell(11);
        cell.setCellValue(graduationDesignDeclareBean.getAcademicTitleName());
        cell.setCellStyle(style);

        cell = row.createCell(12);
        cell.setCellValue(graduationDesignDeclareBean.getAssistantTeacher());
        cell.setCellStyle(style);

        cell = row.createCell(13);
        cell.setCellValue(graduationDesignDeclareBean.getAssistantTeacherAcademic());
        cell.setCellStyle(style);

        cell = row.createCell(14);
        cell.setCellValue(isNull(graduationDesignDeclareBean.getGuideTimes()));
        cell.setCellStyle(style);

        cell = row.createCell(15);
        cell.setCellValue(guidePeoples);
        cell.setCellStyle(style);

        cell = row.createCell(16);
        cell.setCellValue(graduationDesignDeclareBean.getStudentNumber());
        cell.setCellStyle(style);

        cell = row.createCell(17);
        cell.setCellValue(graduationDesignDeclareBean.getStudentName());
        cell.setCellStyle(style);
    }

    private String dealByte(Byte b) {
        if (b != null && b == 1) {
            return "是";
        }
        return "否";
    }

    private Integer isNull(Integer param) {
        return param != null ? param : 0;
    }

    private String replaceData(String str) {
        if (StringUtils.hasLength(str)) {
            str = str.replaceAll("###", ",");
        }
        return str;
    }
}
