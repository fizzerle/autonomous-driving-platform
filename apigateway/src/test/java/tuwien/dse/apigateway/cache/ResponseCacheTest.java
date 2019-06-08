package tuwien.dse.apigateway.cache;

import org.junit.Assert;
import org.junit.Test;

public class ResponseCacheTest {

    private ResponseCache cache;

    @Test
    public void responseCache_shouldContain3Responses() {
        cache = new ResponseCache(5);

        ResponseCacheKey key1 = new ResponseCacheKey("http://test.com/1", "GET");
        ResponseCacheKey key2 = new ResponseCacheKey("http://test.com/2", "GET");
        ResponseCacheKey key3 = new ResponseCacheKey("http://test.com/1", "POST");

        HttpResponse resp1 = new HttpResponse("yes1", 200);
        HttpResponse resp2 = new HttpResponse("yes2", 200);
        HttpResponse resp3 = new HttpResponse("no", 404);


        cache.put(key1, resp1);
        cache.put(key2, resp2);
        cache.put(key3, resp3);

        Assert.assertTrue(cache.containsKey(key1));
        Assert.assertTrue(cache.containsKey(key2));
        Assert.assertTrue(cache.containsKey(key3));

        Assert.assertEquals("yes1", cache.get(key1).getContent());
        Assert.assertEquals("yes2", cache.get(key2).getContent());
        Assert.assertEquals("no", cache.get(key3).getContent());

    }

    @Test
    public void responseCache_shouldKickFirstResponse() {
        cache = new ResponseCache(3);

        ResponseCacheKey key1 = new ResponseCacheKey("http://test.com/1", "GET");
        ResponseCacheKey key2 = new ResponseCacheKey("http://test.com/2", "GET");
        ResponseCacheKey key3 = new ResponseCacheKey("http://test.com/1", "POST");
        ResponseCacheKey key4 = new ResponseCacheKey("http://test.com/3", "GET");

        HttpResponse resp1 = new HttpResponse("yes1", 200);
        HttpResponse resp2 = new HttpResponse("yes2", 200);
        HttpResponse resp3 = new HttpResponse("no", 404);
        HttpResponse resp4 = new HttpResponse("yes3", 200);


        cache.put(key1, resp1);
        cache.put(key2, resp2);
        cache.put(key3, resp3);
        cache.put(key4, resp4);

        Assert.assertFalse(cache.containsKey(key1));
        Assert.assertTrue(cache.containsKey(key2));
        Assert.assertTrue(cache.containsKey(key3));
        Assert.assertTrue(cache.containsKey(key4));

        Assert.assertEquals("yes2", cache.get(key2).getContent());
        Assert.assertEquals("no", cache.get(key3).getContent());
        Assert.assertEquals("yes3", cache.get(key4).getContent());

    }

    @Test
    public void responseCacheOverrideValue_shouldNotKickAnyResponse() {
        cache = new ResponseCache(3);

        ResponseCacheKey key1 = new ResponseCacheKey("http://test.com/1", "GET");
        ResponseCacheKey key2 = new ResponseCacheKey("http://test.com/2", "GET");
        ResponseCacheKey key3 = new ResponseCacheKey("http://test.com/1", "POST");
        ResponseCacheKey key4 = new ResponseCacheKey("http://test.com/1", "GET");

        HttpResponse resp1 = new HttpResponse("yes1", 200);
        HttpResponse resp2 = new HttpResponse("yes2", 200);
        HttpResponse resp3 = new HttpResponse("no", 404);
        HttpResponse resp4 = new HttpResponse("xxx", 200);


        cache.put(key1, resp1);
        cache.put(key2, resp2);
        cache.put(key3, resp3);
        cache.put(key4, resp4);

        Assert.assertTrue(cache.containsKey(key1));
        Assert.assertTrue(cache.containsKey(key2));
        Assert.assertTrue(cache.containsKey(key3));
        Assert.assertTrue(cache.containsKey(key4));

        Assert.assertEquals("xxx", cache.get(key1).getContent());
        Assert.assertEquals("yes2", cache.get(key2).getContent());
        Assert.assertEquals("no", cache.get(key3).getContent());
        Assert.assertEquals("xxx", cache.get(key4).getContent());

    }
}
