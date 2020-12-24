package sample;

public class Consumer implements Runnable {

    Consumer() {
        new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
        ReadFile.get();
    }
}
