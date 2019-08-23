package com.lz.android_rxjava2_sample.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;

public interface UserService {

    /**
     * http://httpbin.org/get
     */
    @GET("get")
    Observable<Response<ResponseBody>> getUserInfo();

    /**
     * http://httpbin.org/get
     */
    @GET("get")
    Observable<Response<ResponseBody>> getUserExtraInfo();
}
