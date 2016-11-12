package top.zbeboy.isy.service;

/**
 * Created by lenovo on 2016-11-12.
 */
public interface InternshipReleaseScienceService {

    /**
     * 保存
     * @param internshipReleaseId 实习发布id
     * @param scienceId 专业id
     */
    void save(String internshipReleaseId,int scienceId);
}
