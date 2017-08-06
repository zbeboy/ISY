package top.zbeboy.isy.web.graduate.design.archives;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.pojos.UsersType;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2017-08-05.
 */
@Slf4j
@Controller
public class GraduationDesignArchivesController {

    /**
     * 毕业设计归档
     *
     * @return 毕业设计归档页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/archives", method = RequestMethod.GET)
    public String manifest() {
        return "web/graduate/design/archives/design_archives::#page-wrapper";
    }

    /**
     * 列表
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/archives/list", method = RequestMethod.GET)
    public String list(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
        return "web/graduate/design/archives/design_archives_list::#page-wrapper";
    }

    /**
     * 清单数据
     *
     * @param request 请求
     * @return 数据
     */
  /*  @RequestMapping(value = "/web/graduate/design/archives/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDeclareBean> listData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            List<String> headers = new ArrayList<>();
            headers.add("presubject_title");
            headers.add("subject_type_name");
            headers.add("origin_type_name");
            headers.add("guide_teacher");
            headers.add("academic_title_name");
            headers.add("guide_peoples");
            headers.add("student_number");
            headers.add("student_name");
            headers.add("score_type_name");
            headers.add("operator");
            dataTablesUtils = new DataTablesUtils<>(request, headers);
            GraduationDesignDeclareBean otherCondition = new GraduationDesignDeclareBean();
            int staffId = NumberUtils.toInt(request.getParameter("staffId"));
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            otherCondition.setStaffId(staffId);
            List<GraduationDesignDeclareBean> graduationDesignDeclareBeens = graduationDesignDeclareService.findAllManifestByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignDeclareBeens);
            dataTablesUtils.setiTotalRecords(graduationDesignDeclareService.countAllManifest(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignDeclareService.countManifestByCondition(dataTablesUtils, otherCondition));
        }
        return dataTablesUtils;
    }*/
}
