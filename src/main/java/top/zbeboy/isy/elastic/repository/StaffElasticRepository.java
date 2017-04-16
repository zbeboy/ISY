package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.StaffElastic;

import java.util.Collection;

/**
 * Created by lenovo on 2017-04-12.
 */
public interface StaffElasticRepository extends ElasticsearchRepository<StaffElastic, String> {

    /**
     * 通过账号查询
     *
     * @param username 账号
     * @return 教职工
     */
    StaffElastic findByUsername(String username);

    /**
     * 根据账号删除
     *
     * @param username 账号
     */
    void deleteByUsername(String username);

    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    long countByAuthorities(int authorities);

    /**
     * 通过权限id统计
     *
     * @param authorities 权限区分字段(详见:UsersElastic)
     * @return 数量
     */
    long countNotInAuthorities(Collection<Integer> authorities);
}
