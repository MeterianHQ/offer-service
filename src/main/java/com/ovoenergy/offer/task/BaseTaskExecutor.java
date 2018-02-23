package com.ovoenergy.offer.task;

public abstract class BaseTaskExecutor implements Runnable {

    protected abstract void runTask();

    @Override
    public void run() {
        runTask();
    }
}
