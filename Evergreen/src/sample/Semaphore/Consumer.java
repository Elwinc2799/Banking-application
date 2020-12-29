package sample.Semaphore;

import sample.ReadFile;

public class Consumer implements Runnable {

    public Consumer() {
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
        ReadFile.get();
    }
}
