package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.PoliticalLandscape;

import java.util.List;

/**
 * Created by lenovo on 2016-10-30.
 */
public interface PoliticalLandscapeService {

    /**
     * 查询全部政治面貌
     *
     * @return 全部政治面貌
     */
    List<PoliticalLandscape> findAll();
}
