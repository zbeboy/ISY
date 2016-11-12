package top.zbeboy.isy.web.internship.release;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience;
import top.zbeboy.isy.domain.tables.pojos.InternshipType;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.InternshipReleaseScienceService;
import top.zbeboy.isy.service.InternshipReleaseService;
import top.zbeboy.isy.service.InternshipTypeService;
import top.zbeboy.isy.service.UsersService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.vo.internship.release.InternshipReleaseVo;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016-11-08.
 */
@Controller
public class ReleaseController {

    private final Logger log = LoggerFactory.getLogger(ReleaseController.class);

    @Resource
    private InternshipTypeService internshipTypeService;

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipReleaseScienceService internshipReleaseScienceService;

    @Resource
    private UsersService usersService;

    /**
     * 实习发布数据
     *
     * @return 实习发布数据页面
     */
    @RequestMapping(value = "/web/menu/internship/release", method = RequestMethod.GET)
    public String releaseData() {
        return "/web/internship/release/internship_release::#page-wrapper";
    }

    /**
     * 实习发布添加页面
     *
     * @return 实习发布添加页面
     */
    @RequestMapping(value = "/web/internship/release/add", method = RequestMethod.GET)
    public String releaseAdd() {
        return "/web/internship/release/internship_release_add::#page-wrapper";
    }

    /**
     * 获取实习类型数据
     *
     * @return 实习类型数据
     */
    @RequestMapping(value = "/user/internship/types", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<InternshipType> internshipTypes() {
        List<InternshipType> internshipTypes = new ArrayList<>();
        InternshipType internshipType = new InternshipType(0, "请选择实习类型");
        internshipTypes.add(internshipType);
        internshipTypes.addAll(internshipTypeService.findAll());
        return new AjaxUtils<InternshipType>().success().msg("获取实习类型数据成功").listData(internshipTypes);
    }

    /**
     * 保存时检验标题
     *
     * @param title 标题
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/release/save/valid", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils saveValid(@RequestParam("releaseTitle") String title) {
        String releaseTitle = StringUtils.trimWhitespace(title);
        if (StringUtils.hasLength(releaseTitle)) {
            List<InternshipRelease> internshipReleases = internshipReleaseService.findByReleaseTitle(releaseTitle);
            if (ObjectUtils.isEmpty(internshipReleases) && internshipReleases.isEmpty()) {
                return new AjaxUtils().success().msg("标题不重复");
            }
        }
        return new AjaxUtils().fail().msg("标题重复");
    }

    /**
     * 保存
     *
     * @param internshipReleaseVo 实习
     * @param bindingResult       检验
     * @return true or false
     */
    @RequestMapping(value = "/web/internship/release/save", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils save(@Valid InternshipReleaseVo internshipReleaseVo, BindingResult bindingResult) {
        try {
            if (!bindingResult.hasErrors()) {
                String format = "yyyy-MM-dd HH:mm:ss";
                String internshipReleaseId = UUIDUtils.getUUID();
                InternshipRelease internshipRelease = new InternshipRelease();
                internshipRelease.setInternshipReleaseId(internshipReleaseId);
                internshipRelease.setInternshipTitle(internshipReleaseVo.getReleaseTitle());
                internshipRelease.setReleaseTime(new Timestamp(System.currentTimeMillis()));
                Users users = usersService.getUserFromSession();
                internshipRelease.setUsername(users.getUsername());
                String[] teacherDistributionArr = internshipReleaseVo.getTeacherDistributionTime().split("至");
                if (!ObjectUtils.isEmpty(teacherDistributionArr) && teacherDistributionArr.length >= 2) {
                    internshipRelease.setTeacherDistributionStartTime(DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[0], format));
                    internshipRelease.setTeacherDistributionEndTime(DateTimeUtils.formatDateToTimestamp(teacherDistributionArr[1], format));
                }
                internshipRelease.setAllowGrade(internshipReleaseVo.getGrade());
                String[] timeArr = internshipReleaseVo.getTime().split("至");
                if (!ObjectUtils.isEmpty(timeArr) && timeArr.length >= 2) {
                    internshipRelease.setStartTime(DateTimeUtils.formatDateToTimestamp(timeArr[0], format));
                    internshipRelease.setEndTime(DateTimeUtils.formatDateToTimestamp(timeArr[1], format));
                }
                internshipRelease.setDepartmentId(internshipReleaseVo.getDepartmentId());
                internshipRelease.setInternshipReleaseIsDel(internshipReleaseVo.getInternshipReleaseIsDel());
                internshipRelease.setInternshipTypeId(internshipReleaseVo.getInternshipTypeId());
                internshipReleaseService.save(internshipRelease);

                String[] scienceArr = internshipReleaseVo.getScienceId().split(",");
                for (String scienceId : scienceArr) {
                    internshipReleaseScienceService.save(internshipReleaseId, NumberUtils.toInt(scienceId));
                }
                return new AjaxUtils().success().msg("保存成功");
            }
        } catch (ParseException e) {
            log.error(" format time is exception.", e);
        }
        return new AjaxUtils().fail().msg("保存失败");
    }
}
