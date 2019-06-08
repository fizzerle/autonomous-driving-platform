package tuwien.dse.apigateway.cache;

import java.util.LinkedHashMap;

public class ResponseCache extends LinkedHashMap<ResponseCacheKey, HttpResponse> {

    private final int maxSize;

    public ResponseCache() {
        this(50);
    }

    public ResponseCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public HttpResponse put(ResponseCacheKey key, HttpResponse value) {
        if (containsKey(key)) {
            remove(key);
            return super.put(key, value);
        }

        if (size() >= maxSize) {
            kickOldestResponse();
        }

        return super.put(key, value);
    }

    private void kickOldestResponse() {
        ResponseCacheKey key = keySet()
                .stream()
                .sorted((a, b) -> a.getQueueNumber() > b.getQueueNumber() ? 1 : -1)
                .limit(1)
                .findFirst()
                .orElseGet(null);
        if (key != null) {
            remove(key);
        }
    }
}
