package sample;

public class Producer implements Runnable {

    Producer() {
        new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
        ReadFile.put();
    }
}
