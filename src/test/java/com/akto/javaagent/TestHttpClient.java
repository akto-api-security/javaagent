package com.akto.javaagent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.akto.HttpClientExample;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;

public class TestHttpClient extends AgentTest {

    @Test
    public void testGetIntercept() {
        try {
            HttpClientExample.main(new String[]{"arn:aws:kms:us-east-1:233905125530:key/e4af77cf-f37a-4ca0-bc60-3cb51d6361b8", "plaintext"});
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    private static List<String> expectedOutput() {
        List<String> ret = new ArrayList<>();

        String testGetInterceptSendGet = "{\"path\": \"https://httpbin.org/get\", \"method\": \"GET\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{\\\"custom-key\\\": \\\"mkyong\\\", \\\"User-Agent\\\": \\\"Googlebot\\\"}\", \"requestPayload\": \"\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"Date\\\": \\\"Sun, 06 Feb 2022 21:45:48 GMT\\\", \\\"Content-Type\\\": \\\"application/json\\\", \\\"Content-Length\\\": \\\"300\\\", \\\"Connection\\\": \\\"keep-alive\\\", \\\"Server\\\": \\\"gunicorn/19.9.0\\\", \\\"Access-Control-Allow-Origin\\\": \\\"*\\\", \\\"Access-Control-Allow-Credentials\\\": \\\"true\\\"}\", \"status\": \"OK\", \"responsePayload\": \"\", \"ip\": \"\", \"time\": \"1644183948\", \"akto_account_id\": \"1000000\", \"akto_vxlan_id\": 123, \"source\": \"OTHER\"}";
        ret.add(testGetInterceptSendGet.replaceAll(" ", ""));
        ret.add(testGetInterceptSendGet.replaceAll(" ", ""));

        String testGetInterceptSendPost = "{\"path\": \"https://httpbin.org/post\", \"method\": \"POST\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{}\", \"requestPayload\": \"username=abc&password=123&custom=secret\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"Date\\\": \\\"Sun, 06 Feb 2022 22:07:23 GMT\\\", \\\"Content-Type\\\": \\\"application/json\\\", \\\"Content-Length\\\": \\\"521\\\", \\\"Connection\\\": \\\"keep-alive\\\", \\\"Server\\\": \\\"gunicorn/19.9.0\\\", \\\"Access-Control-Allow-Origin\\\": \\\"*\\\", \\\"Access-Control-Allow-Credentials\\\": \\\"true\\\"}\", \"status\": \"OK\", \"responsePayload\": \"\", \"ip\": \"\", \"time\": \"1644185243\", \"akto_account_id\": \"1000000\", \"akto_vxlan_id\": 123, \"source\": \"OTHER\"}";
        ret.add(testGetInterceptSendPost.replaceAll(" ", ""));
        ret.add(testGetInterceptSendPost.replaceAll(" ", ""));

        String testGetInterceptSendPost2 = "{\"path\": \"https://httpbin.org/post\", \"method\": \"POST\", \"type\": \"HTTP/1.1\", \"requestHeaders\": \"{}\", \"requestPayload\": \"{\\\"name\\\":\\\"mkyong\\\",\\\"notes\\\":\\\"hello\\\"}\", \"statusCode\": \"200\", \"responseHeaders\": \"{\\\"Date\\\": \\\"Sun, 06 Feb 2022 22:07:24 GMT\\\", \\\"Content-Type\\\": \\\"application/json\\\", \\\"Content-Length\\\": \\\"530\\\", \\\"Connection\\\": \\\"keep-alive\\\", \\\"Server\\\": \\\"gunicorn/19.9.0\\\", \\\"Access-Control-Allow-Origin\\\": \\\"*\\\", \\\"Access-Control-Allow-Credentials\\\": \\\"true\\\"}\", \"status\": \"OK\", \"responsePayload\": \"\", \"ip\": \"\", \"time\": \"1644185244\", \"akto_account_id\": \"1000000\", \"akto_vxlan_id\": 123, \"source\": \"OTHER\"}";
        ret.add(testGetInterceptSendPost2.replaceAll(" ", ""));
        ret.add(testGetInterceptSendPost2.replaceAll(" ", ""));

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
            assertTrue(diff == 0 || diff == 4);        
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException| IllegalAccessException e) {
            assertFalse(true);
        }

    } 
    
}
