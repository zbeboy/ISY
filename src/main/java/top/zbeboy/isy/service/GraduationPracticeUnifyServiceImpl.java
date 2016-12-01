package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeUnifyDao;
import top.zbeboy.isy.domain.tables.daos.InternshipCollegeDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.GRADUATION_PRACTICE_UNIFY;
/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeUnifyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeUnifyServiceImpl implements GraduationPracticeUnifyService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeUnifyServiceImpl.class);

    private final DSLContext create;

    private GraduationPracticeUnifyDao graduationPracticeUnifyDao;

    @Autowired
    public GraduationPracticeUnifyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.graduationPracticeUnifyDao = new GraduationPracticeUnifyDao(configuration);
    }

    @Override
    public GraduationPracticeUnify findById(String id) {
        return graduationPracticeUnifyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.insert(graduationPracticeUnify);
    }

    @Override
    public void update(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.update(graduationPracticeUnify);
    }
}
