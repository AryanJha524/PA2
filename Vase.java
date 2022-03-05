import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Vase {
public static final Semaphore lock = new Semaphore(1, true);
public static final int numGuests = 10;
// true means vase is available, false means another resource is using it
public static boolean vase = false;
private static ArrayList<Thread> threadList;

public static void main(String args[]) {
        Vase alphaThread = new Vase();
        alphaThread.threadList = new ArrayList<Thread>();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numGuests);

        // spawn threads and execute
        for (int i = 0; i < numGuests; i++) {
                Thread temp = new Thread(new Guest(alphaThread, i));
                threadList.add(temp);
                temp.start();
        }

        // terminate threads
        for (int i = 0; i < numGuests; i++) {
                try {
                        alphaThread.threadList.get(i).join();
                }
                catch (Exception e) {
                        System.out.println(e);
                }
        }
}
}

class Guest implements Runnable {
public int guestId;
private Vase sharedThread;
private AtomicInteger visitedCount;
public Guest(Vase inputThread, int id) {
        this.guestId = id;
        this.sharedThread = inputThread;
        this.visitedCount = new AtomicInteger(0);
}

@Override
public void run() {
        try {
                this.sharedThread.lock.acquire();
                // true means its busy
                this.sharedThread.vase ^= true;
                System.out.println("Hello, I'm guest: " + this.guestId + " and I'm viewing the vase!");
                // simulate viewing
                for (int i = 0; i < (int)(Math.random() * 1000); i++) {

                }
                // setting back to false to symbolize vase is ready to view
                this.sharedThread.vase ^= false;
                this.sharedThread.lock.release();
        }
        catch (Exception e) {
                System.out.println(e);
        }
}

}
