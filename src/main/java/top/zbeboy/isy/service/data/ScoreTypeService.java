package top.zbeboy.isy.service.data;

import top.zbeboy.isy.domain.tables.pojos.ScoreType;

import java.util.List;

/**
 * Created by lenovo on 2017-07-23.
 */
public interface ScoreTypeService {

    /**
     * 查询全部类型
     *
     * @return 全部
     */
    List<ScoreType> findAll();
}
