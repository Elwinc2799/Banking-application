package sample.Semaphore;

import sample.ReadFile;

public class Producer implements Runnable {

    public Producer() {
        new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
        ReadFile.put();
    }
}
