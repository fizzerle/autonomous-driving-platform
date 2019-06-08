package tuwien.dse.apigateway.cache;

import java.util.Objects;

public class ResponseCacheKey {

    private static long queueCounter = 0;

    private String url;
    private String httpMethod;
    private long queueNumber = queueCounter++;

    public ResponseCacheKey(String url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public long getQueueNumber() {
        return queueNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseCacheKey that = (ResponseCacheKey) o;
        return url.equals(that.url) &&
                httpMethod.equals(that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpMethod);
    }
}
