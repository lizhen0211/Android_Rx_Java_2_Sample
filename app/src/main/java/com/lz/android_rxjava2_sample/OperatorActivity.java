package com.lz.android_rxjava2_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lz.android_rxjava2_sample.net.LoginRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OperatorActivity extends AppCompatActivity {

    private static String TAG = OperatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);
    }

    /**
     * map 操作符
     * <p>
     * 对上游发送的每一个事件应用一个函数
     * 将上游接口数据，转换成下游接口需要的数据
     *
     * @param view
     */
    public void onMapOperatorClick(View view) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {//Integer 源类型；String 目标类型
            @Override
            public String apply(Integer integer) throws Exception {
                //这里将Integer 转换成了 String
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }

    /**
     * flatMap 操作符
     * <p>
     * 将一个发送事件的上游Observable变换为多个发送事件的Observables
     * 然后将它们发射的事件合并后放进一个单独的Observable里
     *
     * @param view
     */
    public void onFlatMapOperatorClick(View view) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                //此处将上游的一个Observable 转换成了一组Observable(新创建)
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }

    /**
     * concatMap 操作符
     * <p>
     * 注意: flatMap并不保证事件的顺序，并不是事件1就在事件2的前面.
     * concatMap 可以保证事件的顺序 结果是严格按照上游发送的顺序来发送
     *
     * @param view
     */
    public void onConcatMapOperatorClick(View view) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                //此处将上游的一个Observable 转换成了一组Observable(新创建)
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, s);
            }
        });
    }

    /**
     * 不使用操作符的注册方法
     *
     * @throws IOException
     */
    private void register() throws IOException {
        LoginRequest request = new LoginRequest();
        Observable<Response<ResponseBody>> observable = request.register("lz", "123456", "13900000000");
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {

                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        Log.e(TAG, "注册成功");
                        if (response.isSuccessful()) {
                            //注册成功里调用登陆
                            login();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "注册失败");
                    }
                });
    }

    /**
     * 不适用操作符的登陆方法
     *
     * @throws IOException
     */
    private void login() throws IOException {
        LoginRequest request = new LoginRequest();
        Observable<Response<ResponseBody>> observable = request.login("lz", "123456");
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {

                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        Log.e(TAG, "登录成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "登录失败");
                    }
                });
    }

    public void onRegisterDemoClick(View view) throws IOException {
        //不使用操作符的注册及登陆调用
        //register();

        //使用flatmap 操作符的注册及登陆调用
        final LoginRequest request = new LoginRequest();
        Disposable disposable = request.register("lz", "123456", "13900000000")
                //在IO线程进行网络请求
                .subscribeOn(Schedulers.io())
                //回到主线程去处理请求注册结果
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> responseBody) throws Exception {
                        if (responseBody.isSuccessful()) {
                            Log.e(TAG, "注册成功");
                        }
                    }
                })
                //回到IO线程去发起登录请求
                .observeOn(Schedulers.io())
                .flatMap(new Function<Response<ResponseBody>, ObservableSource<Response<ResponseBody>>>() {
                    @Override
                    public ObservableSource<Response<ResponseBody>> apply(Response<ResponseBody> responseBody) throws Exception {
                        //将上游的注册结果 转换成 登陆结果
                        return request.login("lz", "123456");
                    }
                })
                //回到主线程去处理请求登录的结果
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> responseBody) throws Exception {
                        if(responseBody.isSuccessful()){
                            Log.e(TAG, "登录成功");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "登录失败");
                    }
                });

    }
}
