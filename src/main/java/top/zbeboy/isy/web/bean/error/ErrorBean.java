package top.zbeboy.isy.web.bean.error;

import java.util.List;
import java.util.Map;

/**
 * Created by zbeboy on 2016/11/24.
 */
public class ErrorBean<T> {
    private boolean hasError;
    private String errorMsg;
    private T data;
    private Map<String,Object> mapData;
    private List<T> listData;

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, Object> mapData) {
        this.mapData = mapData;
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    @Override
    public String toString() {
        return "ErrorBean{" +
                "hasError=" + hasError +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                ", mapData=" + mapData +
                ", listData=" + listData +
                '}';
    }
}
