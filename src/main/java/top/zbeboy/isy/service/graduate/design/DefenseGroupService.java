package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;

import java.util.List;

/**
 * Created by zbeboy on 2017/7/11.
 */
public interface DefenseGroupService {

    /**
     * 通过毕业设计安排id查询
     *
     * @param defenseArrangementId 毕业设计安排id
     * @return 数据
     */
    List<DefenseGroupBean> findByDefenseArrangementId(String defenseArrangementId);
}
