package com.example.doan;

import com.google.gson.Gson; //Thư viện để convert JSON ↔ Java objects
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient; //OkHttpClient: HTTP client underlying của Retrofit
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit; //Retrofit: Thư viện để thực hiện REST API calls
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static final String TAG = "ApiClient";
    // Thay đổi IP này thành IP thực tế của máy chủ
    // Địa chỉ IP của server backend
    private static final String BASE_URL = "http://192.168.1.5/DoAnAndroid/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating new Retrofit instance with BASE_URL: " + BASE_URL);
            
            // Logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                if (!message.startsWith("{") && !message.startsWith("[")) {
                    Log.d(TAG, "API Log: " + message);
                } else {
                    Log.d(TAG, "API Response: " + message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Network interceptor
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) //connectTimeout: Thời gian chờ để establish connection
                    .readTimeout(30, TimeUnit.SECONDS) //readTimeout: Thời gian chờ để đọc response
                    .writeTimeout(30, TimeUnit.SECONDS) //writeTimeout: Thời gian chờ để gửi request
                    .addInterceptor(logging)
                    .addNetworkInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        
                        // Thêm Content-Type header vào tất cả requests
                        Request newRequest = originalRequest.newBuilder()
                            .header("Content-Type", "application/json")
                            .method(originalRequest.method(), originalRequest.body())
                            .build();
                            
                        Log.d(TAG, String.format("Sending %s request to %s", 
                            newRequest.method(), newRequest.url()));
                        Log.d(TAG, "Request headers: " + newRequest.headers());
                        
                        Response response = chain.proceed(newRequest);
                        Log.d(TAG, String.format("Received response for %s: %d %s", 
                            response.request().url(), response.code(), response.message()));
                        
                        return response;
                    });

            // Cấu hình Gson để xử lý JSON không đúng định dạng
            //setLenient(): Cho phép parse JSON không đúng format strict
            //Giúp xử lý các response JSON "lỏng lẻo" từ server
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            Log.d(TAG, "Retrofit instance created successfully");
        }
        return retrofit;
    }
}