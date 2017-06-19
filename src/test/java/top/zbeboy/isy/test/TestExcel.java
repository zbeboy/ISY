package top.zbeboy.isy.test;

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zbeboy on 2017/6/16.
 */
public class TestExcel {

    @Test
    public void mergingCells() throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        Row row = sheet.createRow((short) 1);
        Cell cell = row.createCell((short) 1);
        cell.setCellValue("This is a test of merging");

        sheet.addMergedRegion(new CellRangeAddress(
                1, //first row (0-based)
                1, //last row  (0-based)
                1, //first column (0-based)
                2  //last column  (0-based)
        ));

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("f:/workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }

    @Test
    public void workWithFonts() throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(1);

        // Create a new font and alter it.
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)24);
        font.setFontName("Courier New");
        font.setItalic(true);
        font.setStrikeout(true);

        // Fonts are set into a style so create a new one to use.
        CellStyle style = wb.createCellStyle();
        style.setFont(font);

        // Create a cell and put a value in it.
        Cell cell = row.createCell(1);
        cell.setCellValue("This is a test of fonts");
        cell.setCellStyle(style);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("f:/workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }

    @Test
    public void headersAndFooters() throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        Header header = sheet.getHeader();
        header.setCenter("Center Header");
        header.setLeft("Left Header");
        header.setRight(HSSFHeader.font("Stencil-Normal", "Italic") +
                HSSFHeader.fontSize((short) 16) + "Right w/ Stencil-Normal Italic font and size 16");

        FileOutputStream fileOut = new FileOutputStream("f:/workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }

    @Test
    public void fillsAndColors() throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 1);

        // Aqua background
        CellStyle style = wb.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        Cell cell = row.createCell((short) 1);
        cell.setCellValue("X");
        cell.setCellStyle(style);

        // Orange "foreground", foreground being the fill foreground not the font color.
        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell = row.createCell((short) 2);
        cell.setCellValue("X");
        cell.setCellStyle(style);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("f:/workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }

    @Test
    public void createGraduationDesignApply() throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("申报表");

        Row row1 = sheet.createRow((short) 0);
        row1.setHeightInPoints(36);

        Font font1 = wb.createFont();
        font1.setFontHeightInPoints((short)18);
        font1.setFontName("黑体");

        CellStyle style1 = wb.createCellStyle();
        style1.setFont(font1);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell1 = row1.createCell((short) 0);
        cell1.setCellValue("昆明理工大学城市学院2017届毕业设计(论文)题目申报表");
        cell1.setCellStyle(style1);

        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                0, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ));

        Row row2 = sheet.createRow((short) 1);
        row2.setHeightInPoints(34);

        Font font2 = wb.createFont();
        font2.setFontHeightInPoints((short)12);
        font2.setFontName("宋体");

        CellStyle style2 = wb.createCellStyle();
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell2 = row2.createCell((short) 0);
        cell2.setCellValue("毕业时间:2017　　系:信息  　专业:计算机科学与技术　　　　 班级:1311、1312、1313　　　  班级人数:　　　　  指导人数:9");
        cell2.setCellStyle(style2);

        sheet.addMergedRegion(new CellRangeAddress(
                1, //first row (0-based)
                1, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ));

        Row row3 = sheet.createRow((short) 2);
        row3.setHeightInPoints(34);

        Font font3 = wb.createFont();
        font3.setFontHeightInPoints((short)12);
        font3.setFontName("宋体");

        CellStyle style3 = wb.createCellStyle();
        style3.setFont(font3);
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setVerticalAlignment(VerticalAlignment.CENTER);

        Cell cell3 = row3.createCell((short) 0);
        cell3.setCellValue(" 教研室主任(签名):                            系毕业设计工作领导小组组长 (签名):                    2017  年  2月   28日");
        cell3.setCellStyle(style3);

        sheet.addMergedRegion(new CellRangeAddress(
                2, //first row (0-based)
                2, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ));

        // column width
        sheet.setColumnWidth((short)0,(short)1000);
        sheet.setColumnWidth((short)1,(short)5000);
        sheet.setColumnWidth((short)2,(short)2200);
        sheet.setColumnWidth((short)3,(short)2200);
        sheet.setColumnWidth((short)4,(short)2200);
        sheet.setColumnWidth((short)5,(short)2600);
        sheet.setColumnWidth((short)6,(short)2600);
        sheet.setColumnWidth((short)7,(short)3100);
        sheet.setColumnWidth((short)8,(short)3100);
        sheet.setColumnWidth((short)9,(short)3100);
        sheet.setColumnWidth((short)10,(short)2200);
        sheet.setColumnWidth((short)11,(short)2200);
        sheet.setColumnWidth((short)12,(short)2200);
        sheet.setColumnWidth((short)13,(short)2200);
        sheet.setColumnWidth((short)14,(short)3100);
        sheet.setColumnWidth((short)15,(short)3100);
        sheet.setColumnWidth((short)16,(short)3490);

        Row row4 = sheet.createRow((short) 3);
        row4.setHeightInPoints(34);

        Font font4 = wb.createFont();
        font4.setFontHeightInPoints((short)10);
        font4.setFontName("宋体");

        CellStyle style4 = wb.createCellStyle();
        style4.setFont(font4);
        style4.setAlignment(HorizontalAlignment.CENTER);
        style4.setVerticalAlignment(VerticalAlignment.CENTER);
        style4.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style4.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell cell4 = row4.createCell((short) 0);
        cell4.setCellValue("编号");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 1);
        cell4.setCellValue("毕业设计(论文)课题");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 2);
        cell4.setCellValue("题目类型");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 3);
        cell4.setCellValue("课题来源");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 4);
        cell4.setCellValue("是否新题");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 5);
        cell4.setCellValue("新教师试做");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 6);
        cell4.setCellValue("新题目试做");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 7);
        cell4.setCellValue("旧题有无改进");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 8);
        cell4.setCellValue("旧题使用次数");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 9);
        cell4.setCellValue("计划上机学时");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 10);
        cell4.setCellValue("指导教师");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 11);
        cell4.setCellValue("教师职称");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 12);
        cell4.setCellValue("助理教师");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 13);
        cell4.setCellValue("教师职称");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 14);
        cell4.setCellValue("指导学生次数");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 15);
        cell4.setCellValue("指导学生人数");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 16);
        cell4.setCellValue("学号");
        cell4.setCellStyle(style4);

        cell4 = row4.createCell((short) 17);
        cell4.setCellValue("学生姓名");
        cell4.setCellStyle(style4);

        Row row5 = sheet.createRow((short) 4);
        row5.setHeightInPoints(27);

        Font font5 = wb.createFont();
        font5.setFontHeightInPoints((short)10);
        font5.setFontName("宋体");

        CellStyle style5 = wb.createCellStyle();
        style5.setFont(font5);
        style5.setAlignment(HorizontalAlignment.CENTER);
        style5.setVerticalAlignment(VerticalAlignment.CENTER);
        style5.setWrapText(true);

        Cell cell5 = row5.createCell((short) 0);
        cell5.setCellValue("1");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 1);
        cell5.setCellValue("时间超市App——时间商品挑选模块");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 2);
        cell5.setCellValue("软件型");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 3);
        cell5.setCellValue("生产");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 4);
        cell5.setCellValue("是");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 5);
        cell5.setCellValue("是");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 6);
        cell5.setCellValue("是");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 7);
        cell5.setCellValue("是");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 8);
        cell5.setCellValue("23");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 9);
        cell5.setCellValue("23");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 10);
        cell5.setCellValue("阿家酉榀");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 11);
        cell5.setCellValue("阿家酉榀");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 12);
        cell5.setCellValue("阿家酉榀");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 13);
        cell5.setCellValue("阿家酉榀");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 14);
        cell5.setCellValue("23");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 15);
        cell5.setCellValue("23");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 16);
        cell5.setCellValue("2012118505129");
        cell5.setCellStyle(style5);

        cell5 = row5.createCell((short) 17);
        cell5.setCellValue("阿家酉榀");
        cell5.setCellStyle(style5);

        Row row6 = sheet.createRow((short) 5);
        row6.setHeightInPoints(15);

        Font font6 = wb.createFont();
        font6.setFontHeightInPoints((short)10);
        font6.setFontName("宋体");

        CellStyle style6 = wb.createCellStyle();
        style6.setFont(font6);

        Cell cell6 = row6.createCell((short) 0);
        cell6.setCellValue("填表说明：");
        cell6.setCellStyle(style6);

        sheet.addMergedRegion(new CellRangeAddress(
                5, //first row (0-based)
                5, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ));

        Row row7 = sheet.createRow((short) 6);
        row7.setHeightInPoints(15);

        Font font7 = wb.createFont();
        font7.setFontHeightInPoints((short)10);
        font7.setFontName("宋体");

        CellStyle style7 = wb.createCellStyle();
        style7.setFont(font7);

        Cell cell7 = row7.createCell((short) 0);
        cell7.setCellValue("1、由每位教师填写，题目请按主课题、子课题准确填写；");
        cell7.setCellStyle(style7);

        sheet.addMergedRegion(new CellRangeAddress(
                6, //first row (0-based)
                6, //last row  (0-based)
                0, //first column (0-based)
                17  //last column  (0-based)
        ));

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("f:/workbook.xls");
        wb.write(fileOut);
        fileOut.close();

        /*
        HSSFCellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setWrapText(true);    自动换行
        cell.setCellStyle(cellStyle);
         */
    }
}
