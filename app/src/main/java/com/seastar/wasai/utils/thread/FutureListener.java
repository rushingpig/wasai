
package com.seastar.wasai.utils.thread;


public interface FutureListener<T> {
    public void onFutureBegin(Future<T> future);

    public void onFutureDone(Future<T> future);
}
