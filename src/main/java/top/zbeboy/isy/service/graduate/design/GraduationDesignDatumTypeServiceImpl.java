package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumTypeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumType;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zbeboy on 2017/6/23.
 */
@Slf4j
@Service("graduationDesignDatumTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDatumTypeServiceImpl implements GraduationDesignDatumTypeService {

    private final DSLContext create;

    @Resource
    private GraduationDesignDatumTypeDao graduationDesignDatumTypeDao;

    @Autowired
    public GraduationDesignDatumTypeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignDatumType> findAll() {
        return graduationDesignDatumTypeDao.findAll();
    }
}
