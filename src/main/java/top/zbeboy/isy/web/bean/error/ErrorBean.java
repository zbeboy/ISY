package top.zbeboy.isy.web.bean.error;

/**
 * Created by zbeboy on 2016/11/24.
 */
public class ErrorBean {
    private boolean hasError;
    private String errorMsg;

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

    @Override
    public String toString() {
        return "ErrorBean{" +
                "hasError=" + hasError +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
