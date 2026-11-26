/*
 * Copyright (C) 2026 Pufferfish
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.plazmamc.plazma.util;

import com.google.common.collect.Queues;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@NullMarked
public class AsyncExecutor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutor.class);

    private final Queue<Runnable> jobs = Queues.newArrayDeque();
    private final Lock mutex = new ReentrantLock();
    private final Condition cond = mutex.newCondition();
    private final Thread thread;
    private volatile boolean killSwitch = false;

    public AsyncExecutor(final String threadName) {
        this.thread = new Thread(this, threadName);
    }

    public void start() {
        thread.start();
    }

    public void kill() {
        killSwitch = true;
        cond.signalAll();
    }

    public void submit(Runnable runnable) {
        mutex.lock();
        try {
            jobs.offer(runnable);
            cond.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void run() {
        while (!killSwitch) {
            try {
                final Runnable runnable = takeRunnable();
                if (runnable != null) {
                    runnable.run();
                }
            } catch (final InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                LOGGER.error("Failed to execute async job for thread {}", thread.getName(), e);
            }
        }
    }

    @Nullable
    private Runnable takeRunnable() throws InterruptedException {
        mutex.lock();
        try {
            while (jobs.isEmpty() && !killSwitch) {
                cond.await();
            }

            return jobs.isEmpty() ? null : jobs.remove(); // We've set killSwitch
        } finally {
            mutex.unlock();
        }
    }

}
