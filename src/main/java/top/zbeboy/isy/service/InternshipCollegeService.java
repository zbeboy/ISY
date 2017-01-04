package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.internship.apply.InternshipCollegeVo;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface InternshipCollegeService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 校外自主实习(去单位)
     */
    InternshipCollege findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param internshipCollege 校外自主实习(去单位)
     */
    void save(InternshipCollege internshipCollege);

    /**
     * 开启事务保存
     *
     * @param internshipCollegeVo 校外自主实习(去单位)
     */
    void saveWithTransaction(InternshipCollegeVo internshipCollegeVo);

    /**
     * 更新
     *
     * @param internshipCollege 校外自主实习(去单位)
     */
    void update(InternshipCollege internshipCollege);

    /**
     * 通过实习发布id与学生id删除
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
    Result<Record> findAllByPage(DataTablesUtils<InternshipCollege> dataTablesUtils, InternshipCollege internshipCollege);

    /**
     * 系总数
     *
     * @return 总数
     */
    int countAll(InternshipCollege internshipCollege);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipCollege> dataTablesUtils, InternshipCollege internshipCollege);

    /**
     * 查询
     *
     * @param dataTablesUtils   datatables工具类
     * @param internshipCollege 校外自主实习(去单位)
     * @return 导出数据
     */
    Result<Record> exportData(DataTablesUtils<InternshipCollege> dataTablesUtils, InternshipCollege internshipCollege);
}
