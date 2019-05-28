/*
* Copyright 2017 The Netty Project
*
* The Netty Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

/**
 * 对Runnable进行封装/代理，可以保证安全的释放使用到的ThreadLocal变量。
 */
final class FastThreadLocalRunnable implements Runnable {
    /**
     * 被代理的Runnable
     */
    private final Runnable runnable;

    private FastThreadLocalRunnable(Runnable runnable) {
        this.runnable = ObjectUtil.checkNotNull(runnable, "runnable");
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } finally {
            // 移除该任务使用的FastThreadLocal变量，保证内存释放
            FastThreadLocal.removeAll();
        }
    }

    /**
     * 将普通的Runnable封装为{@link FastThreadLocalRunnable}。
     * @param runnable 普通的任务
     * @return FastThreadLocalRunnable
     */
    static Runnable wrap(Runnable runnable) {
        return runnable instanceof FastThreadLocalRunnable ? runnable : new FastThreadLocalRunnable(runnable);
    }
}
