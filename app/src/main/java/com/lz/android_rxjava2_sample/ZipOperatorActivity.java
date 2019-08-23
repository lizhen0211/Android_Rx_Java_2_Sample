package com.lz.android_rxjava2_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lz.android_rxjava2_sample.net.UserRequest;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ZipOperatorActivity extends AppCompatActivity {

    private static final String TAG = ZipOperatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gzip_operator);
    }

    /**
     * zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件.
     * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
     *
     * @param view
     */
    public void onTestZipClick(View view) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "observable1 subscribe " + Thread.currentThread().getName());
                Log.e(TAG, "observable1 emit 1");
                emitter.onNext(1);
                Log.e(TAG, "observable1 emit 2");
                emitter.onNext(2);
                Log.e(TAG, "observable1 emit 3");
                emitter.onNext(3);
                Log.e(TAG, "observable1 emit 4");
                emitter.onNext(4);
                Log.e(TAG, "observable1 emit complete1");
                emitter.onComplete();
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "observable2 subscribe " + Thread.currentThread().getName());
                Log.e(TAG, "observable2 emit A");
                emitter.onNext("A");
                Log.e(TAG, "observable2 emit B");
                emitter.onNext("B");
                Log.e(TAG, "observable2 emit C");
                emitter.onNext("C");
                Log.e(TAG, "observable2 emit complete2");
                emitter.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "--observer onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.e(TAG, "--observer onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "--observer onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "--observer onComplete");
            }
        });

        /*这里的执行结果是：
        observer onSubscribe
        observable1 subscribe main
        observable1 emit 1
        observable1 emit 2
        observable1 emit 3
        observable1 emit 4
        observable1 emit complete1
        observable2 subscribe main
        observable2 emit A
        observer onNext: 1A
        observable2 emit B
        observer onNext: 2B
        observable2 emit C
        observer onNext: 3C
        observable2 emit complete2
        observer onComplete*/

        /*先执行observable1 ->再执行 observable2
        由于两个observable 都是在主线程执行，所以有了先后顺序*/
    }

    /**
     * zip 让两个Observable分别在两个IO线程中执行
     *
     * @param view
     */
    public void onTestZipClick2(View view) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "observable1 subscribe " + Thread.currentThread().getName());
                Log.e(TAG, "observable1 emit 1");
                emitter.onNext(1);
                Log.e(TAG, "observable1 emit 2");
                emitter.onNext(2);
                Log.e(TAG, "observable1 emit 3");
                emitter.onNext(3);
                Log.e(TAG, "observable1 emit 4");
                emitter.onNext(4);
                Log.e(TAG, "observable1 emit complete1");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());// observable1和observable2 分别在两个线程执行

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.e(TAG, "observable2 subscribe " + Thread.currentThread().getName());
                Log.e(TAG, "observable2 emit A");
                emitter.onNext("A");
                Log.e(TAG, "observable2 emit B");
                emitter.onNext("B");
                Log.e(TAG, "observable2 emit C");
                emitter.onNext("C");
                Log.e(TAG, "observable2 emit complete2");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());// observable1和observable2 分别在两个线程执行

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "--observer onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.e(TAG, "--observer onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "--observer onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "--observer onComplete");
            }
        });
    }

    public void onGetUserDemoClick(View view) throws Exception {
        UserRequest request = new UserRequest();
        Observable<Response<ResponseBody>> observable1 =
                request.getUserInfo().subscribeOn(Schedulers.io());

        Observable<Response<ResponseBody>> observable2 =
                request.getUserExtraInfo().subscribeOn(Schedulers.io());

        Disposable disposable = Observable.zip(observable1, observable2,
                new BiFunction<Response<ResponseBody>, Response<ResponseBody>, String>() {
                    @Override
                    public String apply(Response<ResponseBody> userInfoResponse,
                                        Response<ResponseBody> userExtraInfoResponse) throws Exception {

                        return userInfoResponse.body().string() + userExtraInfoResponse.body().string();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String infoAndExtraInfo) throws Exception {
                        //do something;
                        Log.e(TAG, infoAndExtraInfo);
                    }
                });
    }
}
