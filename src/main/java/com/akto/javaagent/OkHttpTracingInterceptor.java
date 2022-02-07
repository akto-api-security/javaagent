package com.akto.javaagent;

import okhttp3.Interceptor;

import java.io.IOException;

import com.mongodb.BasicDBObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class OkHttpTracingInterceptor implements Callback {
    BasicDBObject ret;
    Callback orig;
    public OkHttpTracingInterceptor(BasicDBObject ret, Callback orig) {
        this.ret = ret;
        this.orig = orig;
    }

    @Override
    public void onFailure(Call arg0, IOException arg1) {
        // orig.onFailure(arg0, arg1);
    }

    @Override
    public void onResponse(Call arg0, Response arg1) throws IOException {
        try { 
            if (ret == null) {
                return;
            }
            Response response = arg1;
            ret.put("statusCode", response.code()+"");
            ret.put("responseHeaders", OkHttpClientMatcher.toJsonStr(response.headers()));
            ret.put("status", response.message());
            ret.put("responsePayload", "");     
            OkHttpClientMatcher.addConstants(ret);

            OkHttpClientMatcher.apiCalls.add(ret);
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        orig.onResponse(arg0, arg1);
    }

    
}
