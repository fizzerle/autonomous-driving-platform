package tuwien.dse.apigateway.cache;

public class HttpResponse {

    private String content;
    private int code;

    public HttpResponse(String content, int code) {
        this.content = content;
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
