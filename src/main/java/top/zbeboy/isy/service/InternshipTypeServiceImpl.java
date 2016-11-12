package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.InternshipTypeDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipType;

import java.util.List;

/**
 * Created by lenovo on 2016-11-12.
 */
@Service("internshipTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipTypeServiceImpl implements InternshipTypeService {

    private final Logger log = LoggerFactory.getLogger(InternshipTypeServiceImpl.class);

    private final DSLContext create;

    private InternshipTypeDao internshipTypeDao;

    @Autowired
    public InternshipTypeServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipTypeDao = new InternshipTypeDao(configuration);
    }

    @Override
    public List<InternshipType> findAll() {
        return internshipTypeDao.findAll();
    }
}
