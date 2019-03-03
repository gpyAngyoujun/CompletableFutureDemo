# CompletableFutureDemo
AndroidExecutors 的demo

这个AndroidExecutors是针对java8中CompletableFuture所设计的一个executor，其功能类似RxAndroid。

- 使用方法的代码：

```java
mFuture = CompletableFuture.supplyAsync(() -> {
                // 异步子线程
                Log.d(TAG, "supply:" + Thread.currentThread());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "123321";
            }).thenAcceptAsync(s -> {
                // Android主线程回掉
                Log.d(TAG, s + " - " + Thread.currentThread() + " - " + System.currentTimeMillis());
                // throw new AndroidRuntimeException("jeruowieur");
            }, AndroidExecutors.mainExecutor());
```
