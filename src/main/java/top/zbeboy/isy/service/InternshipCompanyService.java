package top.zbeboy.isy.service;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.InternshipCompany;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface InternshipCompanyService {

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 校外自主实习(去单位)
     */
    InternshipCompany findById(String id);

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
     * @param internshipCompany 校外自主实习(去单位)
     */
    void save(InternshipCompany internshipCompany);

    /**
     * 更新
     *
     * @param internshipCompany 校外自主实习(去单位)
     */
    void update(InternshipCompany internshipCompany);
}
