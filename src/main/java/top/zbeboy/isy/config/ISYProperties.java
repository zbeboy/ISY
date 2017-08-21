package top.zbeboy.isy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring boot 配置属性加载.
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "isy", ignoreUnknownFields = false)
public class ISYProperties {

    private final Async async = new Async();

    private final Mobile mobile = new Mobile();

    private final Mail mail = new Mail();

    private final Constants constants = new Constants();

    private final Weixin weixin = new Weixin();

    public Async getAsync() {
        return async;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public Mail getMail() {
        return mail;
    }

    public Constants getConstants() {
        return constants;
    }

    public Weixin getWeixin() {
        return weixin;
    }

    /**
     * 异常初始化参数
     */
    public static class Async {

        private int corePoolSize = 2;

        private int maxPoolSize = 50;

        private int queueCapacity = 10000;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }

    /**
     * 短信初始化参数
     */
    public static class Mobile {

        private String apikey;

        private boolean open;

        private String sign;

        public String getApikey() {
            return apikey;
        }

        public void setApikey(String apikey) {
            this.apikey = apikey;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }

    /**
     * 邮件初始化参数
     */
    public static class Mail {

        private String user;

        private String password;

        private String host;

        private int port;

        private String apiUser;

        private String apiKey;

        private String fromName;

        private int sendMethod;

        private boolean open;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getApiUser() {
            return apiUser;
        }

        public void setApiUser(String apiUser) {
            this.apiUser = apiUser;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getFromName() {
            return fromName;
        }

        public void setFromName(String fromName) {
            this.fromName = fromName;
        }

        public int getSendMethod() {
            return sendMethod;
        }

        public void setSendMethod(int sendMethod) {
            this.sendMethod = sendMethod;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }
    }

    /**
     * 通用初始化参数
     */
    public static class Constants {

        private String serverName;

        private String mailForm;

        private String jooqSqlDialect;

        private int serverHttpPort;

        private int serverHttpsPort;

        private String tempDir;

        private String undertowListenerIp;

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getMailForm() {
            return mailForm;
        }

        public void setMailForm(String mailForm) {
            this.mailForm = mailForm;
        }

        public String getJooqSqlDialect() {
            return jooqSqlDialect;
        }

        public void setJooqSqlDialect(String jooqSqlDialect) {
            this.jooqSqlDialect = jooqSqlDialect;
        }

        public int getServerHttpPort() {
            return serverHttpPort;
        }

        public void setServerHttpPort(int serverHttpPort) {
            this.serverHttpPort = serverHttpPort;
        }

        public int getServerHttpsPort() {
            return serverHttpsPort;
        }

        public void setServerHttpsPort(int serverHttpsPort) {
            this.serverHttpsPort = serverHttpsPort;
        }

        public String getTempDir() {
            return tempDir;
        }

        public void setTempDir(String tempDir) {
            this.tempDir = tempDir;
        }

        public String getUndertowListenerIp() {
            return undertowListenerIp;
        }

        public void setUndertowListenerIp(String undertowListenerIp) {
            this.undertowListenerIp = undertowListenerIp;
        }
    }

    /**
     * 微信初始化参数
     */
    public static class Weixin {
        private String token;
        private String appId;
        private String appSecret;
        private String encodingAESKey;
        private String smallToken;
        private String smallEncodingAESKey;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getEncodingAESKey() {
            return encodingAESKey;
        }

        public void setEncodingAESKey(String encodingAESKey) {
            this.encodingAESKey = encodingAESKey;
        }

        public String getSmallToken() {
            return smallToken;
        }

        public void setSmallToken(String smallToken) {
            this.smallToken = smallToken;
        }

        public String getSmallEncodingAESKey() {
            return smallEncodingAESKey;
        }

        public void setSmallEncodingAESKey(String smallEncodingAESKey) {
            this.smallEncodingAESKey = smallEncodingAESKey;
        }
    }
}
