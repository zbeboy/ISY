package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData;

/**
 * Created by zbeboy on 2017/5/9.
 */
public interface GraduationDesignDeclareDataService {

    /**
     * 根据毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param graduationDesignDeclareData 数据
     */
    void save(GraduationDesignDeclareData graduationDesignDeclareData);
}
