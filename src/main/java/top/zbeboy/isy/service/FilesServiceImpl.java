package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.FilesDao;
import top.zbeboy.isy.domain.tables.pojos.Files;

import java.io.File;

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
}
