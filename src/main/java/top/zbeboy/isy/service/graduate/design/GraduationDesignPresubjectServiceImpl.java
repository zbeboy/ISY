package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignPresubjectDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;

import javax.annotation.Resource;

import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_PRESUBJECT;

/**
 * Created by zbeboy on 2017/6/5.
 */
@Slf4j
@Service("graduationDesignPresubjectService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignPresubjectServiceImpl implements GraduationDesignPresubjectService {

    private final DSLContext create;

    @Resource
    private GraduationDesignPresubjectDao graduationDesignPresubjectDao;

    @Autowired
    public GraduationDesignPresubjectServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationDesignPresubject findById(String id) {
        return graduationDesignPresubjectDao.findById(id);
    }

    @Override
    public GraduationDesignPresubjectRecord findByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId) {
        return create.selectFrom(GRADUATION_DESIGN_PRESUBJECT)
                .where(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID.eq(studentId).and(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId)))
                .fetchOne();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignPresubject graduationDesignPresubject) {
        graduationDesignPresubjectDao.insert(graduationDesignPresubject);
    }

    @Override
    public void update(GraduationDesignPresubject graduationDesignPresubject) {
        graduationDesignPresubjectDao.update(graduationDesignPresubject);
    }
}
