package top.zbeboy.isy.web.util;


import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016-01-09.
 * ajax消息以及数据封装
 */
@Slf4j
@RequiredArgsConstructor(staticName = "of")
@ToString
public class AjaxUtils<T> {

    private boolean state;//消息状态
    private String msg;//消息
    private Map<String, Object> mapResult;//map数据
    private List<T> listResult;//list数据
    private Object objectResult;//单个对象数据
    private PaginationUtils paginationUtils;//分页数据

    public AjaxUtils<T> success() {
        this.state = true;
        return this;
    }

    public AjaxUtils<T> fail() {
        this.state = false;
        return this;
    }

    public AjaxUtils<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public AjaxUtils<T> obj(Object obj) {
        this.objectResult = obj;
        return this;
    }

    public AjaxUtils<T> mapData(Map<String, Object> map) {
        this.mapResult = map;
        return this;
    }

    public AjaxUtils<T> listData(List<T> list) {
        this.listResult = list;
        return this;
    }

    public AjaxUtils<T> paginationUtils(PaginationUtils paginationUtils) {
        this.paginationUtils = paginationUtils;
        return this;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public PaginationUtils getPaginationUtils() {
        return paginationUtils;
    }

    public void setPaginationUtils(PaginationUtils paginationUtils) {
        this.paginationUtils = paginationUtils;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObjectResult() {
        return objectResult;
    }

    public void setObjectResult(Object obj) {
        this.objectResult = obj;
    }

    public Map<String, Object> getMapResult() {
        return mapResult;
    }

    public void setMapResult(Map<String, Object> mapResult) {
        this.mapResult = mapResult;
    }

    public List<T> getListResult() {
        return listResult;
    }

    public void setListResult(List<T> listResult) {
        this.listResult = listResult;
    }
}
