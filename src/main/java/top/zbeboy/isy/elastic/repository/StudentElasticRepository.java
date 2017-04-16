package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.StudentElastic;

/**
 * Created by lenovo on 2017-04-12.
 */
public interface StudentElasticRepository extends ElasticsearchRepository<StudentElastic, String> {

    /**
     * 通过账号查询
     *
     * @param username 账号
     * @return 学生
     */
    StudentElastic findByUsername(String username);

    /**
     * 根据账号删除
     *
     * @param username 账号
     */
    void deleteByUsername(String username);
}
