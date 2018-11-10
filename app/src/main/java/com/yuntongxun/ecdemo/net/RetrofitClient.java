package com.yuntongxun.ecdemo.net;

import com.yuntongxun.ecdemo.net.utils.CookieUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit client;

    private RetrofitClient() {
    }

    public static Retrofit getInstance() {
        if (client == null) {
            synchronized (RetrofitClient.class) {
                if (client == null) {
                    client = create();
                }
            }
        }
        return client;
    }

    private static Retrofit create() {

        return new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Api.BASE_URL)
                .client(createOk())
                .build();
    }

    private static OkHttpClient createOk() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .writeTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .cookieJar(CookieUtils.jar)
                .build();
    }

    public static <T> T create(Class<T> clazz) {
        return RetrofitClient.getInstance().create(clazz);
    }

    public static <T> void request(Observable<T> observable, Observer<T> observer) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
