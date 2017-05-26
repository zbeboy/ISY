package top.zbeboy.isy.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by zbeboy on 2017/5/26.
 */
public class TestWordTable {

    @Test
    public void testCreateWordTable() throws Exception {
        createTable();
    }

    /**
     * @Description: 添加方块♢
     */
    public void setCellContentCommonFunction(XWPFTableCell cell, String content)
            throws Exception {
        XWPFParagraph p = cell.addParagraph();
        setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "300",
                STLineSpacingRule.AUTO);
        setParagraphAlignInfo(p, ParagraphAlignment.BOTH, TextAlignment.CENTER);
        XWPFRun pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunSymInfo(p, pRun, "宋体", "Times New Roman", "21", true,
                false, false, 0, 6, 0);
        pRun = getOrAddParagraphFirstRun(p, true, false);
        setParagraphRunFontInfo(p, pRun, content, "宋体", "Times New Roman",
                "21", true, false, false, false, null, null, 0, 6, 0);
    }

    /**
     * @Description: 保存文档
     */
    public void saveDocument(XWPFDocument document, String savePath)
            throws Exception {
        FileOutputStream fos = new FileOutputStream(savePath);
        document.write(fos);
        fos.close();
    }

    /**
     * @Description: 得到单元格第一个Paragraph
     */
    public XWPFParagraph getCellFirstParagraph(XWPFTableCell cell) {
        XWPFParagraph p;
        if (cell.getParagraphs() != null && cell.getParagraphs().size() > 0) {
            p = cell.getParagraphs().get(0);
        } else {
            p = cell.addParagraph();
        }
        return p;
    }

    /**
     * @Description: 得到段落CTPPr
     */
    public CTPPr getParagraphCTPPr(XWPFParagraph p) {
        CTPPr pPPr = null;
        if (p.getCTP() != null) {
            if (p.getCTP().getPPr() != null) {
                pPPr = p.getCTP().getPPr();
            } else {
                pPPr = p.getCTP().addNewPPr();
            }
        }
        return pPPr;
    }

    /**
     * @Description: 设置段落间距信息, 一行=100 一磅=20
     */
    public void setParagraphSpacingInfo(XWPFParagraph p, boolean isSpace,
                                        String before, String after, String beforeLines, String afterLines,
                                        boolean isLine, String line, STLineSpacingRule.Enum lineValue) {
        CTPPr pPPr = getParagraphCTPPr(p);
        CTSpacing pSpacing = pPPr.getSpacing() != null ? pPPr.getSpacing()
                : pPPr.addNewSpacing();
        if (isSpace) {
            // 段前磅数
            if (before != null) {
                pSpacing.setBefore(new BigInteger(before));
            }
            // 段后磅数
            if (after != null) {
                pSpacing.setAfter(new BigInteger(after));
            }
            // 段前行数
            if (beforeLines != null) {
                pSpacing.setBeforeLines(new BigInteger(beforeLines));
            }
            // 段后行数
            if (afterLines != null) {
                pSpacing.setAfterLines(new BigInteger(afterLines));
            }
        }
        // 间距
        if (isLine) {
            if (line != null) {
                pSpacing.setLine(new BigInteger(line));
            }
            if (lineValue != null) {
                pSpacing.setLineRule(lineValue);
            }
        }
    }

    public void setParagraphRunFontInfo(XWPFParagraph p, XWPFRun pRun,
                                        String content, String cnFontFamily, String enFontFamily,
                                        String fontSize, boolean isBlod, boolean isItalic,
                                        boolean isStrike, boolean isShd, String shdColor,
                                        STShd.Enum shdStyle, int position, int spacingValue, int indent) {
        CTRPr pRpr = getRunCTRPr(p, pRun);
        if (StringUtils.isNotBlank(content)) {
            // pRun.setText(content);
            if (content.contains("\n")) {// System.properties("line.separator")
                String[] lines = content.split("\n");
                pRun.setText(lines[0], 0); // set first line into XWPFRun
                for (int i = 1; i < lines.length; i++) {
                    // add break and insert new text
                    pRun.addBreak();
                    pRun.setText(lines[i]);
                }
            } else {
                pRun.setText(content, 0);
            }
        }
        // 设置字体
        CTFonts fonts = pRpr.isSetRFonts() ? pRpr.getRFonts() : pRpr
                .addNewRFonts();
        if (StringUtils.isNotBlank(enFontFamily)) {
            fonts.setAscii(enFontFamily);
            fonts.setHAnsi(enFontFamily);
        }
        if (StringUtils.isNotBlank(cnFontFamily)) {
            fonts.setEastAsia(cnFontFamily);
            fonts.setHint(STHint.EAST_ASIA);
        }
        // 设置字体大小
        CTHpsMeasure sz = pRpr.isSetSz() ? pRpr.getSz() : pRpr.addNewSz();
        sz.setVal(new BigInteger(fontSize));

        CTHpsMeasure szCs = pRpr.isSetSzCs() ? pRpr.getSzCs() : pRpr
                .addNewSzCs();
        szCs.setVal(new BigInteger(fontSize));

        // 设置字体样式
        // 加粗
        pRun.setBold(isBlod);

        // 倾斜
        pRun.setItalic(isItalic);

        // 删除线
        pRun.setStrikeThrough(isStrike);
        if (isShd) {
            // 设置底纹
            CTShd shd = pRpr.isSetShd() ? pRpr.getShd() : pRpr.addNewShd();
            if (shdStyle != null) {
                shd.setVal(shdStyle);
            }
            if (shdColor != null) {
                shd.setColor(shdColor);
                shd.setFill(shdColor);
            }
        }

        // 设置文本位置
        if (position != 0) {
            pRun.setTextPosition(position);
        }
        if (spacingValue > 0) {
            // 设置字符间距信息
            CTSignedTwipsMeasure ctSTwipsMeasure = pRpr.isSetSpacing() ? pRpr
                    .getSpacing() : pRpr.addNewSpacing();
            ctSTwipsMeasure
                    .setVal(new BigInteger(String.valueOf(spacingValue)));
        }
        if (indent > 0) {
            CTTextScale paramCTTextScale = pRpr.isSetW() ? pRpr.getW() : pRpr
                    .addNewW();
            paramCTTextScale.setVal(indent);
        }
    }

    /**
     * @Description: 得到XWPFRun的CTRPr
     */
    public CTRPr getRunCTRPr(XWPFParagraph p, XWPFRun pRun) {
        CTRPr pRpr = null;
        if (pRun.getCTR() != null) {
            pRpr = pRun.getCTR().getRPr();
            if (pRpr == null) {
                pRpr = pRun.getCTR().addNewRPr();
            }
        } else {
            pRpr = p.getCTP().addNewR().addNewRPr();
        }
        return pRpr;
    }

    /**
     * @Description: 设置段落对齐
     */
    public void setParagraphAlignInfo(XWPFParagraph p,
                                      ParagraphAlignment pAlign, TextAlignment valign) {
        if (pAlign != null) {
            p.setAlignment(pAlign);
        }
        if (valign != null) {
            p.setVerticalAlignment(valign);
        }
    }

    public XWPFRun getOrAddParagraphFirstRun(XWPFParagraph p, boolean isInsert,
                                             boolean isNewLine) {
        XWPFRun pRun = null;
        if (isInsert) {
            pRun = p.createRun();
        } else {
            if (p.getRuns() != null && p.getRuns().size() > 0) {
                pRun = p.getRuns().get(0);
            } else {
                pRun = p.createRun();
            }
        }
        if (isNewLine) {
            pRun.addBreak();
        }
        return pRun;
    }

    /**
     * @Description: 设置Table的边框
     */
    public void setTableBorders(XWPFTable table, STBorder.Enum borderType,
                                String size, String color, String space) {
        CTTblPr tblPr = getTableCTTblPr(table);
        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders()
                : tblPr.addNewTblBorders();
        CTBorder hBorder = borders.isSetInsideH() ? borders.getInsideH()
                : borders.addNewInsideH();
        hBorder.setVal(borderType);
        hBorder.setSz(new BigInteger(size));
        hBorder.setColor(color);
        hBorder.setSpace(new BigInteger(space));

        CTBorder vBorder = borders.isSetInsideV() ? borders.getInsideV()
                : borders.addNewInsideV();
        vBorder.setVal(borderType);
        vBorder.setSz(new BigInteger(size));
        vBorder.setColor(color);
        vBorder.setSpace(new BigInteger(space));

        CTBorder lBorder = borders.isSetLeft() ? borders.getLeft() : borders
                .addNewLeft();
        lBorder.setVal(borderType);
        lBorder.setSz(new BigInteger(size));
        lBorder.setColor(color);
        lBorder.setSpace(new BigInteger(space));

        CTBorder rBorder = borders.isSetRight() ? borders.getRight() : borders
                .addNewRight();
        rBorder.setVal(borderType);
        rBorder.setSz(new BigInteger(size));
        rBorder.setColor(color);
        rBorder.setSpace(new BigInteger(space));

        CTBorder tBorder = borders.isSetTop() ? borders.getTop() : borders
                .addNewTop();
        tBorder.setVal(borderType);
        tBorder.setSz(new BigInteger(size));
        tBorder.setColor(color);
        tBorder.setSpace(new BigInteger(space));

        CTBorder bBorder = borders.isSetBottom() ? borders.getBottom()
                : borders.addNewBottom();
        bBorder.setVal(borderType);
        bBorder.setSz(new BigInteger(size));
        bBorder.setColor(color);
        bBorder.setSpace(new BigInteger(space));
    }

    /**
     * @Description: 得到Table的CTTblPr, 不存在则新建
     */
    public CTTblPr getTableCTTblPr(XWPFTable table) {
        CTTbl ttbl = table.getCTTbl();
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl
                .getTblPr();
        return tblPr;
    }

    /**
     * @Description: 设置列宽和垂直对齐方式
     */
    public void setCellWidthAndVAlign(XWPFTableCell cell, String width,
                                      STTblWidth.Enum typeEnum, STVerticalJc.Enum vAlign) {
        CTTcPr tcPr = getCellCTTcPr(cell);
        CTTblWidth tcw = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        if (width != null) {
            tcw.setW(new BigInteger(width));
        }
        if (typeEnum != null) {
            tcw.setType(typeEnum);
        }
        if (vAlign != null) {
            CTVerticalJc vJc = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr
                    .addNewVAlign();
            vJc.setVal(vAlign);
        }
    }

    /**
     * @Description: 跨列合并
     */
    public void mergeCellsHorizontal(XWPFTable table, int row, int fromCell,
                                     int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cellIndex == fromCell) {
                // The first merged cell is set with RESTART merge value
                getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one,are set with CONTINUE
                getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * @Description: 得到Cell的CTTcPr, 不存在则新建
     */
    public CTTcPr getCellCTTcPr(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
        return tcPr;
    }

    /**
     * @Description: 设置表格列宽
     */
    public void setTableGridCol(XWPFTable table, int[] colWidths) {
        CTTbl ttbl = table.getCTTbl();
        CTTblGrid tblGrid = ttbl.getTblGrid() != null ? ttbl.getTblGrid()
                : ttbl.addNewTblGrid();
        for (int j = 0, len = colWidths.length; j < len; j++) {
            CTTblGridCol gridCol = tblGrid.addNewGridCol();
            gridCol.setW(new BigInteger(String.valueOf(colWidths[j])));
        }
    }

    public void setParagraphRunSymInfo(XWPFParagraph p, XWPFRun pRun,
                                       String cnFontFamily, String enFontFamily, String fontSize,
                                       boolean isBlod, boolean isItalic, boolean isStrike, int position,
                                       int spacingValue, int indent) throws Exception {
        CTRPr pRpr = getRunCTRPr(p, pRun);
        // 设置字体
        CTFonts fonts = pRpr.isSetRFonts() ? pRpr.getRFonts() : pRpr
                .addNewRFonts();
        if (StringUtils.isNotBlank(enFontFamily)) {
            fonts.setAscii(enFontFamily);
            fonts.setHAnsi(enFontFamily);
        }
        if (StringUtils.isNotBlank(cnFontFamily)) {
            fonts.setEastAsia(cnFontFamily);
            fonts.setHint(STHint.EAST_ASIA);
        }
        // 设置字体大小
        CTHpsMeasure sz = pRpr.isSetSz() ? pRpr.getSz() : pRpr.addNewSz();
        sz.setVal(new BigInteger(fontSize));

        CTHpsMeasure szCs = pRpr.isSetSzCs() ? pRpr.getSzCs() : pRpr
                .addNewSzCs();
        szCs.setVal(new BigInteger(fontSize));

        // 设置字体样式
        // 加粗
        pRun.setBold(isBlod);
        // 倾斜
        pRun.setItalic(isItalic);
        // 删除线
        pRun.setStrikeThrough(isStrike);

        // 设置文本位置
        if (position != 0) {
            pRun.setTextPosition(position);
        }
        if (spacingValue > 0) {
            // 设置字符间距信息
            CTSignedTwipsMeasure ctSTwipsMeasure = pRpr.isSetSpacing() ? pRpr
                    .getSpacing() : pRpr.addNewSpacing();
            ctSTwipsMeasure
                    .setVal(new BigInteger(String.valueOf(spacingValue)));
        }
        if (indent > 0) {
            CTTextScale paramCTTextScale = pRpr.isSetW() ? pRpr.getW() : pRpr
                    .addNewW();
            paramCTTextScale.setVal(indent);
        }
        List<CTSym> symList = pRun.getCTR().getSymList();
        CTSym sym = CTSym.Factory
                .parse("<xml-fragment w:font=\"Wingdings 2\" w:char=\"00A3\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"> </xml-fragment>");
        symList.add(sym);
    }

    /**
     * @Description: 设置表格总宽度与水平对齐方式
     */
    public void setTableWidthAndHAlign(XWPFTable table, String width,
                                       STJc.Enum enumValue) {
        CTTblPr tblPr = getTableCTTblPr(table);
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr
                .addNewTblW();
        if (enumValue != null) {
            CTJc cTJc = tblPr.addNewJc();
            cTJc.setVal(enumValue);
        }
        tblWidth.setW(new BigInteger(width));
        tblWidth.setType(STTblWidth.DXA);
    }

    /**
     * @Description: 设置单元格Margin
     */
    public void setTableCellMargin(XWPFTable table, int top, int left,
                                   int bottom, int right) {
        table.setCellMargins(top, left, bottom, right);
    }

    /**
     * @Description: 得到CTTrPr, 不存在则新建
     */
    public CTTrPr getRowCTTrPr(XWPFTableRow row) {
        CTRow ctRow = row.getCtRow();
        CTTrPr trPr = ctRow.isSetTrPr() ? ctRow.getTrPr() : ctRow.addNewTrPr();
        return trPr;
    }

    /**
     * @Description: 设置行高
     */
    public void setRowHeight(XWPFTableRow row, String hight,
                             STHeightRule.Enum heigthEnum) {
        CTTrPr trPr = getRowCTTrPr(row);
        CTHeight trHeight;
        if (trPr.getTrHeightList() != null && trPr.getTrHeightList().size() > 0) {
            trHeight = trPr.getTrHeightList().get(0);
        } else {
            trHeight = trPr.addNewTrHeight();
        }
        trHeight.setVal(new BigInteger(hight));
        if (heigthEnum != null) {
            trHeight.setHRule(heigthEnum);
        }
    }

    /**
     * @Description: 设置底纹
     */
    public void setCellShdStyle(XWPFTableCell cell, boolean isShd,
                                String shdColor, STShd.Enum shdStyle) {
        CTTcPr tcPr = getCellCTTcPr(cell);
        if (isShd) {
            // 设置底纹
            CTShd shd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
            if (shdStyle != null) {
                shd.setVal(shdStyle);
            }
            if (shdColor != null) {
                shd.setColor(shdColor);
                shd.setFill(shdColor);
            }
        }
    }

    public void createTable() throws Exception {
        XWPFDocument xdoc = new XWPFDocument();

        XWPFParagraph p = xdoc.createParagraph();
        // 固定值25磅
        setParagraphSpacingInfo(p, true, "0", "80", null, null, true, "500",
                STLineSpacingRule.EXACT);
        // 居中
        setParagraphAlignInfo(p, ParagraphAlignment.CENTER,
                TextAlignment.CENTER);
        XWPFRun pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "毕业设计（论文）指导进度安排（计划）", "宋体",
                "Times New Roman", "36", true, false, false, false, null, null,
                0, 0, 90);

        p = xdoc.createParagraph();
        // 单倍行距
        setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                STLineSpacingRule.AUTO);
        setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                TextAlignment.CENTER);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "指导教师：王庆平", "宋体", "Times New Roman",
                "30", false, false, false, false, null, null, 0, 0, 90);

        p = xdoc.createParagraph();
        // 单倍行距
        setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                STLineSpacingRule.AUTO);
        setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                TextAlignment.CENTER);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "指导学生：", "宋体", "Times New Roman",
                "30", false, false, false, false, null, null, 0, 0, 90);

        XWPFTable studentTable = xdoc.createTable(3, 4);
        setTableBorders(studentTable, STBorder.SINGLE, "4", "auto", "0");
        setTableWidthAndHAlign(studentTable, "9024", STJc.CENTER);
        setTableCellMargin(studentTable, 0, 108, 0, 108);
        int[] studentColWidths = new int[]{1000, 1000, 1000, 1000};
        setTableGridCol(studentTable, studentColWidths);

        XWPFTableRow studentRow = studentTable.getRow(0);
        setRowHeight(studentRow, "460", STHeightRule.AT_LEAST);
        XWPFTableCell studentCell = studentRow.getCell(0);
        setCellShdStyle(studentCell, true, "FFFFFF", null);
        p = getCellFirstParagraph(studentCell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "序号", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        studentCell = studentRow.getCell(1);
        setCellShdStyle(studentCell, true, "FFFFFF", null);
        p = getCellFirstParagraph(studentCell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "姓名", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        studentCell = studentRow.getCell(2);
        setCellShdStyle(studentCell, true, "FFFFFF", null);
        p = getCellFirstParagraph(studentCell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "学号", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        studentCell = studentRow.getCell(3);
        setCellShdStyle(studentCell, true, "FFFFFF", null);
        p = getCellFirstParagraph(studentCell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "班级", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        p = xdoc.createParagraph();
        // 单倍行距
        setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "240",
                STLineSpacingRule.AUTO);
        setParagraphAlignInfo(p, ParagraphAlignment.LEFT,
                TextAlignment.CENTER);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "具体的进度安排", "宋体", "Times New Roman",
                "30", false, false, false, false, null, null, 0, 0, 90);

        // 创建表格21行5列
        XWPFTable table = xdoc.createTable(21, 5);
        setTableBorders(table, STBorder.SINGLE, "4", "auto", "0");
        setTableWidthAndHAlign(table, "9024", STJc.CENTER);
        setTableCellMargin(table, 0, 108, 0, 108);
        int[] colWidths = new int[]{1800, 1800, 1800, 2000,1800};
        setTableGridCol(table, colWidths);

        XWPFTableRow row = table.getRow(0);
        setRowHeight(row, "460", STHeightRule.AT_LEAST);
        XWPFTableCell cell = row.getCell(0);
        setCellShdStyle(cell, true, "FFFFFF", null);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "进度安排", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        cell = row.getCell(1);
        setCellShdStyle(cell, true, "FFFFFF", null);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "指导时间", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        cell = row.getCell(2);
        setCellShdStyle(cell, true, "FFFFFF", null);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "指导地点", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        cell = row.getCell(3);
        setCellShdStyle(cell, true, "FFFFFF", null);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "指导内容", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        cell = row.getCell(4);
        setCellShdStyle(cell, true, "FFFFFF", null);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "备注", "宋体",
                "Times New Roman", "24", true, false, false, false, null, null,
                0, 6, 0);

        row = table.getRow(1);
        setRowHeight(row, "800", STHeightRule.AT_LEAST);
        cell = row.getCell(0);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "第一阶段：16-17学年上学期放假前一周", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(1);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "1月5日和1月10日", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(2);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "信息工程系明轩楼304", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(3);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "拟定完成毕业设计（论文）题目。", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        row = table.getRow(2);
        setRowHeight(row, "800", STHeightRule.AT_LEAST);
        cell = row.getCell(0);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "第二阶段：（2月27 日—3月26日）", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(1);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "每周一、三的14:30-17:30", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(2);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "信息工程系明轩楼304", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        cell = row.getCell(3);
        p = getCellFirstParagraph(cell);
        pRun = getOrAddParagraphFirstRun(p, false, false);
        setParagraphRunFontInfo(p, pRun, "对学生下发毕业设计任务书和指导书；上交毕业实习实施计划和毕业设计（论文）指导进度计划；毕业实习的内容、完成开题报告，指导教师签订意见，", "宋体", "Times New Roman",
                "21", false, false, false, false, null, null, 0, 6, 0);

        saveDocument(xdoc, "f:/sys_" + System.currentTimeMillis()
                + ".docx");
    }
}
