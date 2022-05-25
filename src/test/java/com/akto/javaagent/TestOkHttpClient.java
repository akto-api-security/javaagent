package com.akto.javaagent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.akto.OkHttpClientExample;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestOkHttpClient {
    @Test
    public void testGetIntercept() throws IOException {
        // OkHttpClientExample.sendGetAsync();
        // OkHttpClientExample.sendGetSync();
        // OkHttpClientExample.sendPostForm();
        // OkHttpClientExample.sendPostJson();
    }

    private static List<String> expectedOutput() {
        List<String> ret = new ArrayList<>();

        String sendGetSync = "{\"path\": \"/get\", \"method\": \"GET\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{\\\"custom-key\\\": \\\"mkyong\\\", \\\"User-Agent\\\": \\\"OkHttp Bot\\\"}\", \"requestPayload\": \"\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"date\\\": \\\"Mon, 07 Feb 2022 00:15:55 GMT\\\", \\\"content-type\\\": \\\"application/json\\\", \\\"content-length\\\": \\\"293\\\", \\\"server\\\": \\\"gunicorn/19.9.0\\\", \\\"access-control-allow-origin\\\": \\\"*\\\", \\\"access-control-allow-credentials\\\": \\\"true\\\"}\", \"status\": \"\", \"responsePayload\": \"\"}";
        ret.add(sendGetSync.replaceAll(" ", ""));

        String sendGetAsync = "{\"path\": \"/post\", \"method\": \"POST\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{\\\"User-Agent\\\": \\\"OkHttp Bot\\\"}\", \"requestPayload\": \"username=abc&password=123&custom=secret\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"date\\\": \\\"Mon, 07 Feb 2022 00:15:59 GMT\\\", \\\"content-type\\\": \\\"application/json\\\", \\\"content-length\\\": \\\"489\\\", \\\"server\\\": \\\"gunicorn/19.9.0\\\", \\\"access-control-allow-origin\\\": \\\"*\\\", \\\"access-control-allow-credentials\\\": \\\"true\\\"}\", \"status\": \"\", \"responsePayload\": \"\"}";
        ret.add(sendGetAsync.replaceAll(" ", ""));

        String sendPostForm = "{\"path\": \"/post\", \"method\": \"POST\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{\\\"User-Agent\\\": \\\"OkHttp Bot\\\"}\", \"requestPayload\": \"username=abc&password=123&custom=secret\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"date\\\": \\\"Mon, 07 Feb 2022 00:24:06 GMT\\\", \\\"content-type\\\": \\\"application/json\\\", \\\"content-length\\\": \\\"489\\\", \\\"server\\\": \\\"gunicorn/19.9.0\\\", \\\"access-control-allow-origin\\\": \\\"*\\\", \\\"access-control-allow-credentials\\\": \\\"true\\\"}\", \"status\": \"\", \"responsePayload\": \"\"}";
        ret.add(sendPostForm.replaceAll(" ", ""));

        String sendPostJson = "{\"path\": \"/post\", \"method\": \"POST\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{\\\"User-Agent\\\": \\\"OkHttp Bot\\\"}\", \"requestPayload\": \"{\\\"name\\\":\\\"mkyong\\\",\\\"notes\\\":\\\"hello\\\"}\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"date\\\": \\\"Mon, 07 Feb 2022 00:16:01 GMT\\\", \\\"content-type\\\": \\\"application/json\\\", \\\"content-length\\\": \\\"499\\\", \\\"server\\\": \\\"gunicorn/19.9.0\\\", \\\"access-control-allow-origin\\\": \\\"*\\\", \\\"access-control-allow-credentials\\\": \\\"true\\\"}\", \"status\": \"\", \"responsePayload\": \"\"}";
        ret.add(sendPostJson.replaceAll(" ", ""));

        return ret;
    }

    // @AfterClass
    public static void unloadAgent() {
        try {
            Class classToLoad = Class.forName("com.akto.utils.RecordConsumer$QueueRecorder");
            Field field = classToLoad.getDeclaredField("apiCalls");
            Object listApiCalls = field.get(null);

            List<String> expected = expectedOutput();
            
            int diff = ((List) listApiCalls).size() - expected.size();
            assertTrue(diff == 0 || diff == 6);        
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException| IllegalAccessException e) {
            assertFalse(true);
        }

    } 
    

}
