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
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author jimmy
 * @date 2017/12/30
 */

public class AndroidExecutors {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static class ExecutorHolder {
        static final ExecutorService MAIN_EXECUTOR = newExecutor();
    }

    public static Executor mainExecutor() {
        return ExecutorHolder.MAIN_EXECUTOR;
    }

    /**
     * if you want to shutdown executor, please use it.
     */
    public static ExecutorService newExecutor() {
        return new HandlerExecutorService(HANDLER);
    }

    public static ExecutorService from(Looper looper) {
        if (looper == null) throw new NullPointerException("looper == null");
        return new HandlerExecutorService(new Handler(looper));
    }

    private AndroidExecutors() {
        throw new AssertionError("No instances.");
    }
}
