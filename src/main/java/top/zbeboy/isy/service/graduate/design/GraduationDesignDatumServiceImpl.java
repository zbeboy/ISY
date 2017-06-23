package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by zbeboy on 2017/6/23.
 */
@Slf4j
@Service("graduationDesignDatumService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignDatumServiceImpl implements GraduationDesignDatumService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignDatumServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        return null;
    }

    @Override
    public int countAll(GraduationDesignDatumBean graduationDesignDatumBean) {
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean) {
        return 0;
    }
}
