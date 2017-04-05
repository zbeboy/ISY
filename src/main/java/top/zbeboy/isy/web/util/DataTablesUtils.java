package top.zbeboy.isy.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by lenovo on 2016-09-14.
 */
public class DataTablesUtils<T> {
    /*
    返回的数据
     */
    private List<T> data;

    private int draw;

    /*
    数据总数
     */
    private long iTotalRecords;

    /*
    过滤条件下的总数
     */
    private long iTotalDisplayRecords;

    /*
    从哪页开始
     */
    private int start;

    /*
    页大小
     */
    private int length;

    /*
    哪列排序 是索引
     */
    private int orderColumn;

    /*
    列
     */
    private List<String> headers;

    /*
    哪列排序 是数据库对应列名
     */
    private String orderColumnName;

    /*
    asc or desc
     */
    private String orderDir = "asc";

    /*
    当开启过滤时，过滤参数
     */
    private String searchValue;

    /*
    额外搜索参数
     */
    private String extraSearch;

    /*
    当前页号
     */
    private int extraPage;

    /*
    object extraSearch
     */
    private JSONObject search;

    public DataTablesUtils() {

    }

    public DataTablesUtils(HttpServletRequest request, List<String> headers) {
        String startParam = request.getParameter("start");
        String lengthParam = request.getParameter("length");
        String orderColumnParam = request.getParameter("order[0][column]");
        String orderDirParam = request.getParameter("order[0][dir]");
        String searchValueParam = request.getParameter("search[value]");
        String extraSearchParam = request.getParameter("extra_search");
        String extraPage = request.getParameter("extra_page");
        String dramParam = request.getParameter("draw");

        if (NumberUtils.isNumber(startParam)) {
            this.start = NumberUtils.toInt(startParam);
        }

        if (NumberUtils.isNumber(lengthParam)) {
            this.length = NumberUtils.toInt(lengthParam);
        }

        if (NumberUtils.isNumber(orderColumnParam)) {
            this.orderColumn = NumberUtils.toInt(orderColumnParam);
        }

        if (!ObjectUtils.isEmpty(headers) && !headers.isEmpty() && headers.size() > this.orderColumn) {
            this.orderColumnName = headers.get(this.orderColumn);
            this.headers = headers;
        }

        if (StringUtils.isNotBlank(orderDirParam)) {
            this.orderDir = orderDirParam;
        }

        if (StringUtils.isNotBlank(searchValueParam)) {
            this.searchValue = searchValueParam;
        }

        if (StringUtils.isNotBlank(extraSearchParam)) {
            this.extraSearch = extraSearchParam;
            this.search = JSON.parseObject(extraSearchParam);
        }

        if(NumberUtils.isNumber(extraPage)){
            this.extraPage = NumberUtils.toInt(extraPage);
        }

        if (NumberUtils.isNumber(dramParam)) {
            this.draw = NumberUtils.toInt(dramParam);
        }

    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(int orderColumn) {
        this.orderColumn = orderColumn;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getOrderColumnName() {
        return orderColumnName;
    }

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getExtraSearch() {
        return extraSearch;
    }

    public void setExtraSearch(String extraSearch) {
        this.extraSearch = extraSearch;
    }

    public int getExtraPage() {
        return extraPage;
    }

    public void setExtraPage(int extraPage) {
        this.extraPage = extraPage;
    }

    public JSONObject getSearch() {
        return search;
    }

    public void setSearch(JSONObject search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "DataTablesUtils{" +
                "data=" + data +
                ", draw=" + draw +
                ", iTotalRecords=" + iTotalRecords +
                ", iTotalDisplayRecords=" + iTotalDisplayRecords +
                ", start=" + start +
                ", length=" + length +
                ", orderColumn=" + orderColumn +
                ", headers=" + headers +
                ", orderColumnName='" + orderColumnName + '\'' +
                ", orderDir='" + orderDir + '\'' +
                ", searchValue='" + searchValue + '\'' +
                ", extraSearch='" + extraSearch + '\'' +
                ", search=" + search +
                '}';
    }
}
