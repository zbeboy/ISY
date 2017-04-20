package top.zbeboy.isy.service.common;

import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.FilesDao;
import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.util.RequestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by lenovo on 2016-11-13.
 */
@Service("filesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FilesServiceImpl implements FilesService {

    private final Logger log = LoggerFactory.getLogger(FilesServiceImpl.class);

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
            String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))+ ".docx";
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
                log.error("Close file is error, error {}",e);
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
                log.error("Close file is error, error {}",e);
            }
        }
    }
}
