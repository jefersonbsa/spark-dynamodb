package com.audienceproject.com.google.common.util.concurrent;

/*
 * Notice:
 * This file was modified at AudienceProject ApS by Cosmin Catalin Sanda (cosmin@audienceproject.com)
 */

/*
 * Copyright (C) 2011 The Guava Authors
 *
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

import com.audienceproject.com.google.common.base.Preconditions;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Utilities for treating interruptible operations as uninterruptible.
 * In all cases, if a thread is interrupted during such a call, the call
 * continues to block until the result is available or the timeout elapses,
 * and only then re-interrupts the thread.
 *
 * @author Anthony Zana
 * @since 10.0
 */
public final class Uninterruptibles {

    // Implementation Note: As of 3-7-11, the logic for each blocking/timeout
    // methods is identical, save for method being invoked.

    /**
     * Invokes {@code latch.}{@link CountDownLatch#await() await()}
     * uninterruptibly.
     */
    public static void awaitUninterruptibly(CountDownLatch latch) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    latch.await();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes
     * {@code latch.}{@link CountDownLatch#await(long, TimeUnit)
     * await(timeout, unit)} uninterruptibly.
     */
    public static boolean awaitUninterruptibly(CountDownLatch latch,
                                               long timeout, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;

            while (true) {
                try {
                    // CountDownLatch treats negative timeouts just like zero.
                    return latch.await(remainingNanos, NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes {@code toJoin.}{@link Thread#join() join()} uninterruptibly.
     */
    public static void joinUninterruptibly(Thread toJoin) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    toJoin.join();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes {@code future.}{@link Future#get() get()} uninterruptibly.
     * To get uninterruptibility and remove checked exceptions.
     *
     * <p>If instead, you wish to treat {@link InterruptedException} uniformly
     * with other exceptions.
     *
     * @throws ExecutionException if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     */
    public static <V> V getUninterruptibly(Future<V> future)
        throws ExecutionException {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes
     * {@code future.}{@link Future#get(long, TimeUnit) get(timeout, unit)}
     * uninterruptibly.
     *
     * <p>If instead, you wish to treat {@link InterruptedException} uniformly
     * with other exceptions.
     *
     * @throws ExecutionException if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     * @throws TimeoutException if the wait timed out
     */
    public static <V> V getUninterruptibly(
        Future<V> future, long timeout,  TimeUnit unit)
        throws ExecutionException, TimeoutException {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;

            while (true) {
                try {
                    // Future treats negative timeouts just like zero.
                    return future.get(remainingNanos, NANOSECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes
     * {@code unit.}{@link TimeUnit#timedJoin(Thread, long)
     * timedJoin(toJoin, timeout)} uninterruptibly.
     */
    public static void joinUninterruptibly(Thread toJoin,
                                           long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(toJoin);
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    // TimeUnit.timedJoin() treats negative timeouts just like zero.
                    NANOSECONDS.timedJoin(toJoin, remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes {@code queue.}{@link BlockingQueue#take() take()} uninterruptibly.
     */
    public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Invokes {@code queue.}{@link BlockingQueue#put(Object) put(element)}
     * uninterruptibly.
     *
     * @throws ClassCastException if the class of the specified element prevents
     *     it from being added to the given queue
     * @throws IllegalArgumentException if some property of the specified element
     *     prevents it from being added to the given queue
     */
    public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    queue.put(element);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // TODO(user): Support Sleeper somehow (wrapper or interface method)?
    /**
     * Invokes {@code unit.}{@link TimeUnit#sleep(long) sleep(sleepFor)}
     * uninterruptibly.
     */
    public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(sleepFor);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    // TimeUnit.sleep() treats negative timeouts just like zero.
                    NANOSECONDS.sleep(remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // TODO(user): Add support for waitUninterruptibly.

    private Uninterruptibles() {}
}

