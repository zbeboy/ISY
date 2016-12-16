package top.zbeboy.isy.service;

import org.apache.poi.POIDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.FilesDao;
import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.pojos.Users;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016-11-13.
 */
@Service("filesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FilesServiceImpl implements FilesService {

    private final Logger log = LoggerFactory.getLogger(FilesServiceImpl.class);

    private final DSLContext create;

    private FilesDao filesDao;

    @Autowired
    public FilesServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.filesDao = new FilesDao(configuration);
    }

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
    public String saveInternshipJournal(InternshipJournal internshipJournal,Users users) {
        String outputPath = Workbook.internshipJournalPath(users)+ new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".doc";
        try{
            String templatePath = Workbook.INTERNSHIP_JOURNAL_FILE_PATH;
            InputStream is = new FileInputStream(templatePath);
            HWPFDocument doc = new HWPFDocument(is);
            Range range = doc.getRange();
            range.replaceText("${studentName}", internshipJournal.getStudentName());
            range.replaceText("${studentNumber}", internshipJournal.getStudentNumber());
            range.replaceText("${organize}", internshipJournal.getOrganize());
            range.replaceText("${schoolGuidanceTeacher}", internshipJournal.getSchoolGuidanceTeacher());
            range.replaceText("${graduationPracticeCompanyName}", internshipJournal.getGraduationPracticeCompanyName());
            range.replaceText("${internshipJournalContent}", internshipJournal.getInternshipJournalContent());
            range.replaceText("${internshipJournalDate}", new SimpleDateFormat("yyyy-MM-dd").format(internshipJournal.getInternshipJournalDate()));
            String path = Workbook.internshipJournalPath(users);
            String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".doc";
            File saveFile = new File(path);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            OutputStream os = new FileOutputStream(path + filename);
            //把doc输出到输出流中
            doc.write(os);
            outputPath = path + filename;
            this.closeStream(os);
            this.closeStream(is);
            log.info("Save internship journal finish, the path is {}",outputPath);
        } catch (IOException e) {
            log.error("Save internship journal error,error is {}",e);
            return null;
        }
        return outputPath;
    }

    /**
     * 关闭输入流
     * @param is 流
     */
    private void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os 流
     */
    private void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
