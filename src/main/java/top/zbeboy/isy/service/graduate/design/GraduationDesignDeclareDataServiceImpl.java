package top.zbeboy.isy.service.graduate.design;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDeclareDataDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.*;
/**
 * Created by zbeboy on 2017/5/9.
 */
@Service("graduationDesignDeclareDataService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDeclareDataServiceImpl implements GraduationDesignDeclareDataService {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignDeclareDataServiceImpl.class);

    private final DSLContext create;

    @Resource
    private GraduationDesignDeclareDataDao graduationDesignDeclareDataDao;

    @Autowired
    public GraduationDesignDeclareDataServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        create.deleteFrom(GRADUATION_DESIGN_DECLARE_DATA)
                .where(GRADUATION_DESIGN_DECLARE_DATA.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignDeclareData graduationDesignDeclareData) {
        graduationDesignDeclareDataDao.insert(graduationDesignDeclareData);
    }
}
