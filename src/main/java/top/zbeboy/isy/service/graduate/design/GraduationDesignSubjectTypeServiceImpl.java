package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignSubjectTypeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zbeboy on 2017/6/13.
 */
@Slf4j
@Service("graduationDesignSubjectTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignSubjectTypeServiceImpl implements GraduationDesignSubjectTypeService {

    private final DSLContext create;

    @Resource
    private GraduationDesignSubjectTypeDao graduationDesignSubjectTypeDao;

    @Autowired
    public GraduationDesignSubjectTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignSubjectType> findAll() {
        return graduationDesignSubjectTypeDao.findAll();
    }
}
