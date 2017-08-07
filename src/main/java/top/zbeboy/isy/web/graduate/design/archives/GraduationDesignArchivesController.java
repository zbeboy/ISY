package top.zbeboy.isy.web.graduate.design.archives;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.service.graduate.design.GraduationDesignArchivesService;
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-08-05.
 */
@Slf4j
@Controller
public class GraduationDesignArchivesController {

    @Resource
    private GraduationDesignArchivesService graduationDesignArchivesService;

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
     * 归档数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/archives/list/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignArchivesBean> listData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            // 前台数据标题 注：要和前台标题顺序一致，获取order用
            List<String> headers = new ArrayList<>();
            headers.add("college_name");
            headers.add("college_code");
            headers.add("science_name");
            headers.add("science_code");
            headers.add("graduation_date");
            headers.add("staff_name");
            headers.add("staff_number");
            headers.add("academic_title_name");
            headers.add("assistant_teacher");
            headers.add("assistant_teacher_number");
            headers.add("assistant_teacher_academic");
            headers.add("presubject_title");
            headers.add("subject_type_name");
            headers.add("origin_type_name");
            headers.add("student_number");
            headers.add("student_name");
            headers.add("score_type_name");
            headers.add("is_excellent");
            headers.add("archive_number");
            headers.add("note");
            headers.add("operator");
            dataTablesUtils = new DataTablesUtils<>(request, headers);
            GraduationDesignArchivesBean otherCondition = new GraduationDesignArchivesBean();
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            List<GraduationDesignArchivesBean> graduationDesignArchivesBeans = graduationDesignArchivesService.findAllByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignArchivesBeans);
            dataTablesUtils.setiTotalRecords(graduationDesignArchivesService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignArchivesService.countByCondition(dataTablesUtils, otherCondition));
        }
        return dataTablesUtils;
    }
}
