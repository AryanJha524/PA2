import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;


public class Cupcake {
// num guests in simulation
public static final int numGuests = 20;
// declaring state of room after guest exits maze
public static AtomicBoolean cupcakePresent = new AtomicBoolean(false);
public final AtomicBoolean running = new AtomicBoolean(true);
public static AtomicInteger guestCount = new AtomicInteger(0);
// create a array list to store guests
public static ArrayList<Thread> guests = new ArrayList<>(20);


public static void main(String args[]) {
        Cupcake mainThread = new Cupcake();

        // create thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

        // create guests, add to thread
        for (int i = 0; i < numGuests; i++) {
                guests.add(new Thread(new Guest(mainThread, i, false)));
        }

        // execute threads
        for (int i = 0; i < numGuests; i++) {
                executor.execute(guests.get(i));
        }

        executor.shutdown();
        System.out.println("Program terminated");

}
}


class Guest implements Runnable {

public int guestId;
public boolean eatenCake;
public boolean isCounter;
private Cupcake sharedThread;

public Guest(Cupcake alpha, int id, boolean eatenCake) {
        this.guestId = id;
        this.eatenCake = eatenCake;
        this.sharedThread = alpha;
        if (id == 0) {
                this.isCounter = true;
        }
        else {
                this.isCounter =  false;
        }

}

@Override
public void run() {
        try {
                while (sharedThread.running.get()) {
                        if (sharedThread.guestCount.compareAndSet(this.sharedThread.numGuests, 20)) {
                                System.out.println("All guests have eaten the cupcake");
                                this.sharedThread.running.set(false);
                        }
                        else {
                                // guest escapes maze
                                if (this.isCounter) {
                                        // guest is counter, check if cupcake present (do not increase count)
                                        if (sharedThread.cupcakePresent.get() == true) {
                                                // last person to enter was counter themself, so don't do anything
                                        }
                                        else if (sharedThread.cupcakePresent.get() == false) {
                                                // cupcake missing, previous guest has eaten the cupcake, increase count
                                                sharedThread.guestCount.getAndIncrement();
                                                // ask for new cupcake for next guest
                                                sharedThread.cupcakePresent.set(true);
                                        }
                                        System.out.println("Guest count is NOW: " + this.sharedThread.guestCount.get() + " guest count = " + sharedThread.guestCount);
                                }
                                else {
                                        // a regular guest
                                        if (this.eatenCake) {
                                                // do nothing if already eaten Cake
                                        }
                                        else if (!this.eatenCake) {
                                                // a cake was present and this guest eats it
                                                if (sharedThread.cupcakePresent.compareAndSet(true, false)) {
                                                        this.eatenCake = true;
                                                        this.sharedThread.cupcakePresent.set(false);
                                                        System.out.println("Reg guest JUST ate id:" + this.guestId);
                                                }
                                                else {
                                                        // cupcake is being eaten, go wait a random amount till available
                                                        Thread.sleep((long)(Math.random() * 1000));
                                                }
                                        }
                                }
                                // sleep for random amount of time to simulate randomly
                                // selecting guests that make it out the maze
                                Thread.sleep((long)(Math.random() * 1000));
                        }
                }
        }
        catch (Exception e) {
                System.out.println(e);
        }
}
}
