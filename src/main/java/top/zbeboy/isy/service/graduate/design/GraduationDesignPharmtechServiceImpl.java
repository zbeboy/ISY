package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zbeboy on 2017/5/15.
 */
@Slf4j
@Service("graduationDesignPharmtechService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignPharmtechServiceImpl implements GraduationDesignPharmtechService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignPharmtechServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }
}
