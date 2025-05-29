package com.example.doan;

import com.google.gson.Gson; //Thư viện để convert JSON ↔ Java objects
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient; //OkHttpClient: HTTP client underlying của Retrofit
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit; //Retrofit: Thư viện để thực hiện REST API calls
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // Thay đổi IP này thành IP thực tế của máy chủ
    // Địa chỉ IP của server backend
    private static final String BASE_URL = "http://192.168.1.12/DoAnAndroid/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            //Ghi log tất cả HTTP requests/responses (bao gồm body)
            //Hữu ích cho việc debug API calls
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cấu hình OkHttpClient với timeout
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) //connectTimeout: Thời gian chờ để establish connection
                    .readTimeout(30, TimeUnit.SECONDS) //readTimeout: Thời gian chờ để đọc response
                    .writeTimeout(30, TimeUnit.SECONDS) //writeTimeout: Thời gian chờ để gửi request
                    .addInterceptor(logging);

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
        }
        return retrofit;
    }
}