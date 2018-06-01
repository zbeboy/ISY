package top.zbeboy.isy.service.util

import org.apache.commons.lang3.StringUtils
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*
import java.io.FileOutputStream
import java.math.BigInteger

/**
 * Created by zbeboy 2017-11-30 .
 **/
class OperatorWordUtils {
    /**
     * 添加方块♢
     */
    @Throws(Exception::class)
    fun setCellContentCommonFunction(cell: XWPFTableCell, content: String) {
        val p = cell.addParagraph()
        setParagraphSpacingInfo(p, true, "0", "0", "0", "0", true, "300",
                STLineSpacingRule.AUTO)
        setParagraphAlignInfo(p, ParagraphAlignment.BOTH, TextAlignment.CENTER)
        var pRun = getOrAddParagraphFirstRun(p, false, false)
        setParagraphRunSymInfo(p, pRun, "宋体", "Times New Roman", "21", true,
                false, false, 0, 6, 0)
        pRun = getOrAddParagraphFirstRun(p, true, false)
        setParagraphRunFontInfo(p, pRun, content, "宋体", "Times New Roman",
                "21", true, false, false, false, null, null, 0, 6, 0)
    }

    /**
     * 保存文档
     */
    @Throws(Exception::class)
    fun saveDocument(document: XWPFDocument, savePath: String) {
        val fos = FileOutputStream(savePath)
        document.write(fos)
        fos.close()
    }

    /**
     * 得到单元格第一个Paragraph
     */
    fun getCellFirstParagraph(cell: XWPFTableCell): XWPFParagraph {
        return if (cell.paragraphs != null && cell.paragraphs.size > 0) {
            cell.paragraphs[0]
        } else {
            cell.addParagraph()
        }
    }

    /**
     * 得到段落CTPPr
     */
    fun getParagraphCTPPr(p: XWPFParagraph): CTPPr? {
        var pPPr: CTPPr? = null
        if (p.ctp != null) {
            pPPr = if (p.ctp.pPr != null) {
                p.ctp.pPr
            } else {
                p.ctp.addNewPPr()
            }
        }
        return pPPr
    }

    /**
     * 设置段落间距信息, 一行=100 一磅=20
     */
    fun setParagraphSpacingInfo(p: XWPFParagraph, isSpace: Boolean,
                                before: String?, after: String?, beforeLines: String?, afterLines: String?,
                                isLine: Boolean, line: String?, lineValue: STLineSpacingRule.Enum?) {
        val pPPr = getParagraphCTPPr(p)
        val pSpacing = if (pPPr!!.spacing != null)
            pPPr.spacing
        else
            pPPr.addNewSpacing()
        if (isSpace) {
            // 段前磅数
            if (before != null) {
                pSpacing.before = BigInteger(before)
            }
            // 段后磅数
            if (after != null) {
                pSpacing.after = BigInteger(after)
            }
            // 段前行数
            if (beforeLines != null) {
                pSpacing.beforeLines = BigInteger(beforeLines)
            }
            // 段后行数
            if (afterLines != null) {
                pSpacing.afterLines = BigInteger(afterLines)
            }
        }
        // 间距
        if (isLine) {
            if (line != null) {
                pSpacing.line = BigInteger(line)
            }
            if (lineValue != null) {
                pSpacing.lineRule = lineValue
            }
        }
    }

    fun setParagraphRunFontInfo(p: XWPFParagraph, pRun: XWPFRun,
                                content: String, cnFontFamily: String, enFontFamily: String,
                                fontSize: String, isBlod: Boolean, isItalic: Boolean,
                                isStrike: Boolean, isShd: Boolean, shdColor: String?,
                                shdStyle: STShd.Enum?, position: Int, spacingValue: Int, indent: Int) {
        val pRpr = getRunCTRPr(p, pRun)
        if (StringUtils.isNotBlank(content)) {
            // pRun.setText(content);
            if (content.contains("\n")) {// System.properties("line.separator")
                val lines = content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                pRun.setText(lines[0], 0) // set first line into XWPFRun
                for (i in 1 until lines.size) {
                    // add break and insert new text
                    pRun.addBreak()
                    pRun.setText(lines[i])
                }
            } else {
                pRun.setText(content, 0)
            }
        }
        // 设置字体
        val fonts = if (pRpr.isSetRFonts)
            pRpr.rFonts
        else
            pRpr
                    .addNewRFonts()
        if (StringUtils.isNotBlank(enFontFamily)) {
            fonts.ascii = enFontFamily
            fonts.hAnsi = enFontFamily
        }
        if (StringUtils.isNotBlank(cnFontFamily)) {
            fonts.eastAsia = cnFontFamily
            fonts.hint = STHint.EAST_ASIA
        }
        // 设置字体大小
        val sz = if (pRpr.isSetSz) pRpr.sz else pRpr.addNewSz()
        sz.`val` = BigInteger(fontSize)

        val szCs = if (pRpr.isSetSzCs)
            pRpr.szCs
        else
            pRpr
                    .addNewSzCs()
        szCs.`val` = BigInteger(fontSize)

        // 设置字体样式
        // 加粗
        pRun.isBold = isBlod

        // 倾斜
        pRun.isItalic = isItalic

        // 删除线
        pRun.isStrikeThrough = isStrike
        if (isShd) {
            // 设置底纹
            val shd = if (pRpr.isSetShd) pRpr.shd else pRpr.addNewShd()
            if (shdStyle != null) {
                shd.`val` = shdStyle
            }
            if (shdColor != null) {
                shd.color = shdColor
                shd.fill = shdColor
            }
        }

        // 设置文本位置
        if (position != 0) {
            pRun.textPosition = position
        }
        if (spacingValue > 0) {
            // 设置字符间距信息
            val ctSTwipsMeasure = if (pRpr.isSetSpacing)
                pRpr
                        .spacing
            else
                pRpr.addNewSpacing()
            ctSTwipsMeasure.`val` = BigInteger(spacingValue.toString())
        }
        if (indent > 0) {
            val paramCTTextScale = if (pRpr.isSetW)
                pRpr.w
            else
                pRpr
                        .addNewW()
            paramCTTextScale.`val` = indent
        }
    }

    /**
     * 得到XWPFRun的CTRPr
     */
    fun getRunCTRPr(p: XWPFParagraph, pRun: XWPFRun): CTRPr {
        var pRpr: CTRPr
        if (pRun.ctr != null) {
            pRpr = pRun.ctr.rPr
            if (pRpr == null) {
                pRpr = pRun.ctr.addNewRPr()
            }
        } else {
            pRpr = p.ctp.addNewR().addNewRPr()
        }
        return pRpr
    }

    /**
     * 设置段落对齐
     */
    fun setParagraphAlignInfo(p: XWPFParagraph,
                              pAlign: ParagraphAlignment?, valign: TextAlignment?) {
        if (pAlign != null) {
            p.alignment = pAlign
        }
        if (valign != null) {
            p.verticalAlignment = valign
        }
    }

    fun getOrAddParagraphFirstRun(p: XWPFParagraph, isInsert: Boolean,
                                  isNewLine: Boolean): XWPFRun {
        val pRun: XWPFRun = if (isInsert) {
            p.createRun()
        } else {
            if (p.runs != null && p.runs.size > 0) {
                p.runs[0]
            } else {
                p.createRun()
            }
        }
        if (isNewLine) {
            pRun.addBreak()
        }
        return pRun
    }

    /**
     * 设置Table的边框
     */
    fun setTableBorders(table: XWPFTable, borderType: STBorder.Enum,
                        size: String, color: String, space: String) {
        val tblPr = getTableCTTblPr(table)
        val borders = if (tblPr.isSetTblBorders)
            tblPr.tblBorders
        else
            tblPr.addNewTblBorders()
        val hBorder = if (borders.isSetInsideH)
            borders.insideH
        else
            borders.addNewInsideH()
        hBorder.`val` = borderType
        hBorder.sz = BigInteger(size)
        hBorder.color = color
        hBorder.space = BigInteger(space)

        val vBorder = if (borders.isSetInsideV)
            borders.insideV
        else
            borders.addNewInsideV()
        vBorder.`val` = borderType
        vBorder.sz = BigInteger(size)
        vBorder.color = color
        vBorder.space = BigInteger(space)

        val lBorder = if (borders.isSetLeft)
            borders.left
        else
            borders
                    .addNewLeft()
        lBorder.`val` = borderType
        lBorder.sz = BigInteger(size)
        lBorder.color = color
        lBorder.space = BigInteger(space)

        val rBorder = if (borders.isSetRight)
            borders.right
        else
            borders
                    .addNewRight()
        rBorder.`val` = borderType
        rBorder.sz = BigInteger(size)
        rBorder.color = color
        rBorder.space = BigInteger(space)

        val tBorder = if (borders.isSetTop)
            borders.top
        else
            borders
                    .addNewTop()
        tBorder.`val` = borderType
        tBorder.sz = BigInteger(size)
        tBorder.color = color
        tBorder.space = BigInteger(space)

        val bBorder = if (borders.isSetBottom)
            borders.bottom
        else
            borders.addNewBottom()
        bBorder.`val` = borderType
        bBorder.sz = BigInteger(size)
        bBorder.color = color
        bBorder.space = BigInteger(space)
    }

    /**
     * 得到Table的CTTblPr, 不存在则新建
     */
    fun getTableCTTblPr(table: XWPFTable): CTTblPr {
        val ttbl = table.ctTbl
        return if (ttbl.tblPr == null)
            ttbl.addNewTblPr()
        else
            ttbl
                    .tblPr
    }

    /**
     * 设置列宽和垂直对齐方式
     */
    fun setCellWidthAndVAlign(cell: XWPFTableCell, width: String?,
                              typeEnum: STTblWidth.Enum?, vAlign: STVerticalJc.Enum?) {
        val tcPr = getCellCTTcPr(cell)
        val tcw = if (tcPr.isSetTcW) tcPr.tcW else tcPr.addNewTcW()
        if (width != null) {
            tcw.w = BigInteger(width)
        }
        if (typeEnum != null) {
            tcw.type = typeEnum
        }
        if (vAlign != null) {
            val vJc = if (tcPr.isSetVAlign)
                tcPr.vAlign
            else
                tcPr
                        .addNewVAlign()
            vJc.`val` = vAlign
        }
    }

    /**
     * 跨列合并
     */
    fun mergeCellsHorizontal(table: XWPFTable, row: Int, fromCell: Int,
                             toCell: Int) {
        for (cellIndex in fromCell..toCell) {
            val cell = table.getRow(row).getCell(cellIndex)
            if (cellIndex == fromCell) {
                // The first merged cell is set with RESTART merge value
                getCellCTTcPr(cell).addNewHMerge().`val` = STMerge.RESTART
            } else {
                // Cells which join (merge) the first one,are set with CONTINUE
                getCellCTTcPr(cell).addNewHMerge().`val` = STMerge.CONTINUE
            }
        }
    }

    /**
     * 得到Cell的CTTcPr, 不存在则新建
     */
    fun getCellCTTcPr(cell: XWPFTableCell): CTTcPr {
        val cttc = cell.ctTc
        return if (cttc.isSetTcPr) cttc.tcPr else cttc.addNewTcPr()
    }

    /**
     * @Description: 设置表格列宽
     */
    fun setTableGridCol(table: XWPFTable, colWidths: IntArray) {
        val ttbl = table.ctTbl
        val tblGrid = if (ttbl.tblGrid != null)
            ttbl.tblGrid
        else
            ttbl.addNewTblGrid()
        var j = 0
        val len = colWidths.size
        while (j < len) {
            val gridCol = tblGrid.addNewGridCol()
            gridCol.w = BigInteger(colWidths[j].toString())
            j++
        }
    }

    @Throws(Exception::class)
    fun setParagraphRunSymInfo(p: XWPFParagraph, pRun: XWPFRun,
                               cnFontFamily: String, enFontFamily: String, fontSize: String,
                               isBlod: Boolean, isItalic: Boolean, isStrike: Boolean, position: Int,
                               spacingValue: Int, indent: Int) {
        val pRpr = getRunCTRPr(p, pRun)
        // 设置字体
        val fonts = if (pRpr.isSetRFonts)
            pRpr.rFonts
        else
            pRpr
                    .addNewRFonts()
        if (StringUtils.isNotBlank(enFontFamily)) {
            fonts.ascii = enFontFamily
            fonts.hAnsi = enFontFamily
        }
        if (StringUtils.isNotBlank(cnFontFamily)) {
            fonts.eastAsia = cnFontFamily
            fonts.hint = STHint.EAST_ASIA
        }
        // 设置字体大小
        val sz = if (pRpr.isSetSz) pRpr.sz else pRpr.addNewSz()
        sz.`val` = BigInteger(fontSize)

        val szCs = if (pRpr.isSetSzCs)
            pRpr.szCs
        else
            pRpr
                    .addNewSzCs()
        szCs.`val` = BigInteger(fontSize)

        // 设置字体样式
        // 加粗
        pRun.isBold = isBlod
        // 倾斜
        pRun.isItalic = isItalic
        // 删除线
        pRun.isStrikeThrough = isStrike

        // 设置文本位置
        if (position != 0) {
            pRun.textPosition = position
        }
        if (spacingValue > 0) {
            // 设置字符间距信息
            val ctSTwipsMeasure = if (pRpr.isSetSpacing)
                pRpr
                        .spacing
            else
                pRpr.addNewSpacing()
            ctSTwipsMeasure.`val` = BigInteger(spacingValue.toString())
        }
        if (indent > 0) {
            val paramCTTextScale = if (pRpr.isSetW)
                pRpr.w
            else
                pRpr
                        .addNewW()
            paramCTTextScale.`val` = indent
        }
        val symList = pRun.ctr.symList
        val sym = CTSym.Factory
                .parse("<xml-fragment w:font=\"Wingdings 2\" w:char=\"00A3\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"> </xml-fragment>")
        symList.add(sym)
    }

    /**
     * 设置表格总宽度与水平对齐方式
     */
    fun setTableWidthAndHAlign(table: XWPFTable, width: String,
                               enumValue: STJc.Enum?) {
        val tblPr = getTableCTTblPr(table)
        val tblWidth = if (tblPr.isSetTblW)
            tblPr.tblW
        else
            tblPr
                    .addNewTblW()
        if (enumValue != null) {
            val cTJc = tblPr.addNewJc()
            cTJc.`val` = enumValue
        }
        tblWidth.w = BigInteger(width)
        tblWidth.type = STTblWidth.DXA
    }

    /**
     * 设置单元格Margin
     */
    fun setTableCellMargin(table: XWPFTable, top: Int, left: Int,
                           bottom: Int, right: Int) {
        table.setCellMargins(top, left, bottom, right)
    }

    /**
     * 得到CTTrPr, 不存在则新建
     */
    fun getRowCTTrPr(row: XWPFTableRow): CTTrPr {
        val ctRow = row.ctRow
        return if (ctRow.isSetTrPr) ctRow.trPr else ctRow.addNewTrPr()
    }

    /**
     * 设置行高
     */
    fun setRowHeight(row: XWPFTableRow, hight: String,
                     heigthEnum: STHeightRule.Enum?) {
        val trPr = getRowCTTrPr(row)
        val trHeight: CTHeight
        if (trPr.trHeightList != null && trPr.trHeightList.size > 0) {
            trHeight = trPr.trHeightList[0]
        } else {
            trHeight = trPr.addNewTrHeight()
        }
        trHeight.`val` = BigInteger(hight)
        if (heigthEnum != null) {
            trHeight.hRule = heigthEnum
        }
    }

    /**
     * 设置底纹
     */
    fun setCellShdStyle(cell: XWPFTableCell, isShd: Boolean,
                        shdColor: String?, shdStyle: STShd.Enum?) {
        val tcPr = getCellCTTcPr(cell)
        if (isShd) {
            // 设置底纹
            val shd = if (tcPr.isSetShd) tcPr.shd else tcPr.addNewShd()
            if (shdStyle != null) {
                shd.`val` = shdStyle
            }
            if (shdColor != null) {
                shd.color = shdColor
                shd.fill = shdColor
            }
        }
    }
}