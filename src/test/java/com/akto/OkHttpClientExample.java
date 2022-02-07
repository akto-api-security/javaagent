package com.akto;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpClientExample {

    public static void sendGetSync() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://httpbin.org/get")
                .addHeader("custom-key", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response headers
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                // System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            // Get response body
            // System.out.println(response.body().string());
        }

    }    

    public static void sendGetAsync() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://httpbin.org/get")
                .addHeader("xxxxxxxxxxxxxxxx", "mkyong")  // add request headers
                .addHeader("User-Agent", "OkHttp Bot")
                .build();

        Call call = httpClient.newCall(request);        

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // Get response headers
                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    // Get response body
                    System.out.println(responseBody.string());
                }
            }
        });
    }

    public static void sendPostForm() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        // form parameters
        RequestBody formBody = new FormBody.Builder()
                .add("username", "abc")
                .add("password", "123")
                .add("custom", "secret")
                .build();

        Request request = new Request.Builder()
                .url("https://httpbin.org/post")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());
        }

    }

    public static void sendPostJson() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();

        // json formatted data
        String json = new StringBuilder()
                .append("{")
                .append("\"name\":\"mkyong\",")
                .append("\"notes\":\"hello\"")
                .append("}").toString();

		// json request body
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://httpbin.org/post")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());
        }

    }

}
