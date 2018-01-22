/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jimmy.android.executors;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jimmy
 * @date 2017/12/30
 */

class HandlerExecutorService extends AbstractExecutorService {

    private final Handler handler;
    private final List<ExecutorMessage> executors = new Vector<>();
    private final AtomicBoolean mIsShutdown = new AtomicBoolean(false);

    HandlerExecutorService(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void shutdown() {
        shutdownNow();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        mIsShutdown.set(true);
        handler.removeCallbacksAndMessages(null);
        List<Runnable> dest = new ArrayList<>();
        for (ExecutorMessage msg : executors) {
            msg.dispose();
            dest.add(msg);
        }
        executors.clear();
        return dest;
    }

    @Override
    public boolean isShutdown() {
        return mIsShutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return isShutdown();
    }

    @Override
    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        wait(timeout);
        return isTerminated();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (isShutdown()) {
            return;
        }

        ExecutorMessage run = ExecutorMessage.create(handler, command)
                .onComplete(executors::remove)
                .onError(Throwable::printStackTrace)
                .sendToTarget();
        executors.add(run);

    }

    private static class ExecutorMessage implements Runnable {

        private final Handler handler;
        private final Runnable delegate;
        private final AtomicBoolean mDisposed = new AtomicBoolean(false);
        private Consumer<? super ExecutorMessage> onComplete;
        private Consumer<? super Throwable> onError;

        static ExecutorMessage create(Handler handler, Runnable delegate) {
            return new ExecutorMessage(handler, delegate);
        }

        private ExecutorMessage(Handler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            if (isDisposed()) {
                return;
            }

            try {
                delegate.run();
            } catch (Throwable e) {
                if (isDisposed() && onError != null) {
                    onError.accept(e);
                }
                return;
            }

            if (!isDisposed() && onComplete != null) {
                onComplete.accept(this);
            }
        }

        ExecutorMessage onComplete(Consumer<? super ExecutorMessage> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        ExecutorMessage onError(Consumer<? super Throwable> onError) {
            this.onError = onError;
            return this;
        }

        ExecutorMessage sendToTarget() {
            if (!isDisposed()) {
                Message.obtain(handler, this).sendToTarget();
            }
            return this;
        }

        void dispose() {
            mDisposed.set(true);
            handler.removeCallbacks(this);
            onComplete = null;
            onError = null;
        }

        boolean isDisposed() {
            return mDisposed.get();
        }
    }

}
