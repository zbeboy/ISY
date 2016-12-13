package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeUnifyBean;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeUnifyVo;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface GraduationPracticeUnifyService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(学校统一组织校外实习)
     */
    GraduationPracticeUnify findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Result<Record> findByInternshipReleaseIdAndStudentIdRelation(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    void save(GraduationPracticeUnify graduationPracticeUnify);

    /**
     * 开启事务保存
     *
     * @param graduationPracticeUnifyVo 毕业实习(学校统一组织校外实习)
     */
    void saveWithTransaction(GraduationPracticeUnifyVo graduationPracticeUnifyVo);

    /**
     * 更新
     *
     * @param graduationPracticeUnify 毕业实习(学校统一组织校外实习)
     */
    void update(GraduationPracticeUnify graduationPracticeUnify);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils, GraduationPracticeUnifyBean graduationPracticeUnifyBean);

    /**
     * 系总数
     *
     * @return 总数
     */
    int countAll(GraduationPracticeUnifyBean graduationPracticeUnifyBean);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationPracticeUnifyBean> dataTablesUtils, GraduationPracticeUnifyBean graduationPracticeUnifyBean);
}
