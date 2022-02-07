package com.akto.javaagent;

import java.io.IOException;

import com.mongodb.BasicDBObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OkHttpTracingInterceptor implements Callback {
    BasicDBObject ret;
    Callback orig;
    public OkHttpTracingInterceptor(BasicDBObject ret, Callback orig) {
        this.ret = ret;
        this.orig = orig;
    }

    @Override
    public void onFailure(Call arg0, IOException arg1) {
        orig.onFailure(arg0, arg1);
    }

    @Override
    public void onResponse(Call arg0, Response arg1) throws IOException {
        try { 
            if (ret != null) {
                Response response = arg1;
                ret.put("statusCode", response.code()+"");
                ret.put("responseHeaders", OkHttpClientMatcher.toJsonStr(response.headers()));
                ret.put("status", response.message());
                ret.put("responsePayload", "");     
                OkHttpClientMatcher.addConstants(ret);

                AgentMain.recordConsumer.consume(ret.toJson());
                System.out.println(ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        orig.onResponse(arg0, arg1);
    }

    
}
