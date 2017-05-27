package top.zbeboy.isy.web.data.building;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.BuildingService;
import top.zbeboy.isy.web.bean.data.building.BuildingBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/27.
 */
@Slf4j
@Controller
public class BuildingController {

    @Resource
    private BuildingService buildingService;

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    /**
     * 楼数据
     *
     * @return 楼数据页面
     */
    @RequestMapping(value = "/web/menu/data/building", method = RequestMethod.GET)
    public String buildingData() {
        return "web/data/building/building_data::#page-wrapper";
    }

    /**
     * datatables ajax查询数据
     *
     * @param request 请求
     * @return datatables数据
     */
    @RequestMapping(value = "/web/data/building/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<BuildingBean> buildingDatas(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("building_id");
        headers.add("school_name");
        headers.add("college_name");
        headers.add("building_name");
        headers.add("building_is_del");
        headers.add("operator");
        DataTablesUtils<BuildingBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        Result<Record> records = buildingService.findAllByPage(dataTablesUtils);
        List<BuildingBean> buildingBeens = new ArrayList<>();
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            buildingBeens = records.into(BuildingBean.class);
        }
        dataTablesUtils.setData(buildingBeens);
        dataTablesUtils.setiTotalRecords(buildingService.countAll());
        dataTablesUtils.setiTotalDisplayRecords(buildingService.countByCondition(dataTablesUtils));
        return dataTablesUtils;
    }

    /**
     * 楼数据添加
     *
     * @return 添加页面
     */
    @RequestMapping(value = "/web/data/building/add", method = RequestMethod.GET)
    public String buildingAdd(ModelMap modelMap) {
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/building/building_add::#page-wrapper";
    }

    /**
     * 楼数据编辑
     *
     * @param id       楼id
     * @param modelMap 页面对象
     * @return 编辑页面
     */
    @RequestMapping(value = "/web/data/building/edit", method = RequestMethod.GET)
    public String buildingEdit(@RequestParam("id") int id, ModelMap modelMap) {
        commonControllerMethodService.currentUserRoleNameAndCollegeIdPageParam(modelMap);
        return "web/data/building/building_edit::#page-wrapper";
    }
}
