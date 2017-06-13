package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignSubjectOriginTypeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zbeboy on 2017/6/13.
 */
@Slf4j
@Service("graduationDesignSubjectOriginTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignSubjectOriginTypeServiceImpl implements GraduationDesignSubjectOriginTypeService {

    private final DSLContext create;

    @Resource
    private GraduationDesignSubjectOriginTypeDao graduationDesignSubjectOriginTypeDao;

    @Autowired
    public GraduationDesignSubjectOriginTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignSubjectOriginType> findAll() {
        return graduationDesignSubjectOriginTypeDao.findAll();
    }
}
