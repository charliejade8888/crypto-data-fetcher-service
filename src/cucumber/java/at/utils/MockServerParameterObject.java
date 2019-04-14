package at.utils;

//TODO lombok
public class MockServerParameterObject {

    private String host;
    private String path;
    private String stubResponseFile;
    private String fsymParam;
    private String tsymParam;
    private String limitParam;
    private String toTsParam;
    private int port;

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getStubResponseFile() {
        return stubResponseFile;
    }

    public String getFsymParam() {
        return fsymParam;
    }

    public String getTsymParam() {
        return tsymParam;
    }

    public String getLimitParam() {
        return limitParam;
    }

    public String getToTsParam() {
        return toTsParam;
    }

    public int getPort() {
        return port;
    }

    private MockServerParameterObject() {
    }

    private MockServerParameterObject(Builder builder) {
        host = builder.host;
        path = builder.path;
        stubResponseFile = builder.stubResponseFile;
        fsymParam = builder.fsymParam;
        tsymParam = builder.tsymParam;
        limitParam = builder.limitParam;
        toTsParam = builder.toTsParam;
        port = builder.port;
    }

    public static final class Builder {
        private String host;
        private String path;
        private String stubResponseFile;
        private String fsymParam;
        private String tsymParam;
        private String limitParam;
        private String toTsParam;
        private int port;

        public Builder() {
        }

        public Builder host(String val) {
            host = val;
            return this;
        }

        public Builder path(String val) {
            path = val;
            return this;
        }

        public Builder stubResponseFile(String val) {
            stubResponseFile = val;
            return this;
        }

        public Builder fsymParam(String val) {
            fsymParam = val;
            return this;
        }

        public Builder tsymParam(String val) {
            tsymParam = val;
            return this;
        }

        public Builder limitParam(String val) {
            limitParam = val;
            return this;
        }

        public Builder toTsParam(String val) {
            toTsParam = val;
            return this;
        }

        public Builder port(int val) {
            port = val;
            return this;
        }

        public MockServerParameterObject build() {
            return new MockServerParameterObject(this);
        }
    }
}