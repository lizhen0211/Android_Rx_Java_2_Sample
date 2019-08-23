package com.lz.android_rxjava2_sample.net;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRequest {

    public Observable<Response<ResponseBody>> getUserInfo() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://httpbin.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);
        Observable<Response<ResponseBody>> observable = service.getUserInfo();
        return observable;
    }

    public Observable<Response<ResponseBody>> getUserExtraInfo() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://httpbin.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);
        Observable<Response<ResponseBody>> observable = service.getUserExtraInfo();
        return observable;
    }
}
