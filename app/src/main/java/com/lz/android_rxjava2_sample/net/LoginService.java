package com.lz.android_rxjava2_sample.net;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface LoginService {

    /**
     * http://httpbin.org/post
     * <p>
     * body {"username":"lz","password":"123"}
     */
    @POST("post")
    Observable<ResponseBody> login(@Body Map<String ,Object> paramMap);

    /**
     * http://httpbin.org/post
     * <p>
     * body {"username":"lz","password":"123","tel":13900002222}
     */
    @POST("post")
    Observable<ResponseBody> register(@Body Map<String ,Object> paramMap);

}
