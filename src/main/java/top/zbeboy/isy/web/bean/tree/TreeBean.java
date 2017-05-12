package top.zbeboy.isy.web.bean.tree;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by lenovo on 2016-10-18.
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class TreeBean {
    private String text;
    private List<TreeBean> nodes;
    private int dataId;
}
