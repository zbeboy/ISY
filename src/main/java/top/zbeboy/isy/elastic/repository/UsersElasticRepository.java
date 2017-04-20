package top.zbeboy.isy.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.zbeboy.isy.elastic.pojo.UsersElastic;

import java.util.Collection;

/**
 * Created by lenovo on 2017-04-10.
 */
public interface UsersElasticRepository extends ElasticsearchRepository<UsersElastic, String> {
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
    long countByAuthoritiesNotIn(Collection<Integer> authorities);
}
