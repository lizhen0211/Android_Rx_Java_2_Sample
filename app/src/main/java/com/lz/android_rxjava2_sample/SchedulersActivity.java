package com.lz.android_rxjava2_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lz.android_rxjava2_sample.net.LoginRequest;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SchedulersActivity extends AppCompatActivity {

    private static final String TAG = SchedulersActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulers);
    }

    /**
     * 在主线程中分别创建上游和下游, 然后将他们连接在一起
     *
     * @param view
     */
    public void onDefaultClick(View view) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.e(TAG, "onNext: " + integer);
            }
        };

        observable.subscribe(consumer);
    }

    /**
     * subscribeOn() 指定的是上游发送事件的线程
     * observeOn() 指定的是下游接收事件的线程
     *
     * @param view
     */
    public void onSchedulerSwitchClick1(View view) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //此处在子线程 RxNewThreadScheduler 执行
                Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //此处在主线程执行
                Log.e(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.e(TAG, "onNext: " + integer);
            }
        };
        //指定上游发送事件的线程
        observable.subscribeOn(Schedulers.newThread())
                //指定下游接收时间线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 多次指定上游的线程只有第一次指定的有效，其余的会被忽略
     * 多次指定下游的线程是可以的，也就是说每调用一次observeOn()，下游的线程就会切换一次.
     *
     * @param view
     */
    public void onSchedulerSwitchClick2(View view) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.e(TAG, "onNext: " + integer);
            }
        };

        //上游虽然指定了两次线程, 但只有第一次指定的有效, 依然是在RxNewThreadScheduler 线程中
        //而下游则是在 RxCachedThreadScheduler 中执行
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(consumer);


        /*observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                //每调用一次observeOn() 线程便会切换一次
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                })
                //每调用一次observeOn() 线程便会切换一次
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);*/


        /**
         * Rxjava常用的线程池
         *
         * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
         * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
         * Schedulers.newThread() 代表一个常规的新线程
         * AndroidSchedulers.mainThread() 代表Android的主线程
         */
    }

    /**
     * 网络请求与UI刷新通过 scheduler 切换
     *
     * @param view
     */
    public void onSchedulerNetClick2(View view) throws IOException {
        LoginRequest request = new LoginRequest();
        Observable<ResponseBody> observable = request.login("lz", "123456");
        observable.subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SchedulersActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(SchedulersActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
