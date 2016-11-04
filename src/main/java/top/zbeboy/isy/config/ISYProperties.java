package top.zbeboy.isy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Administrator on 2016/7/21.
 */
@ConfigurationProperties(prefix = "isy", ignoreUnknownFields = false)
public class ISYProperties {

    private final Async async = new Async();

    private final Mobile mobile = new Mobile();

    private final Mail mail = new Mail();

    private final Constants constants = new Constants();

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

    ;

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

    public static class Mobile {

        private String apikey;

        private boolean open;

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
    }

    public static class Mail {

        private String user;

        private String password;

        private String host;

        private int port;

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

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }
    }

    public static class Constants {

        private String serverName;

        private String mailForm;

        private String jooqSqlDialect;

        private int serverHttpPort;

        private int serverHttpsPort;

        private String tempDir;

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
    }
}
