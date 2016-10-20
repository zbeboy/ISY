package top.zbeboy.isy.web.bean.tree;

import java.util.List;

/**
 * Created by lenovo on 2016-10-18.
 */
public class TreeBean {
    private String text;
    private int dataId;
    private List<TreeBean> nodes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public List<TreeBean> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeBean> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "TreeBean{" +
                "text='" + text + '\'' +
                ", dataId=" + dataId +
                ", nodes=" + nodes +
                '}';
    }
}
