package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultithreadedClient {
    private static final int NUM_THREADS_INITIAL = 32;
    private static final int NUM_REQUESTS_PER_THREAD_INITIAL = 1000;
    private static final int TOTAL_REQUESTS = 10000;

    private static final BlockingQueue<SkierLiftRideEventGenerator.SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>();
    
    public static void main(String[] args) {
        // Start event generation thread
        Thread eventGenerationThread = new Thread(new EventGenerationTask());
        eventGenerationThread.start();
        
        // Create initial thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS_INITIAL);
        for (int i = 0; i < NUM_THREADS_INITIAL; i++) {
            executor.execute(new APIClientTask(NUM_REQUESTS_PER_THREAD_INITIAL));
        }
        
        // Shutdown the executor once all tasks are complete
        executor.shutdown();
    }

    static class EventGenerationTask implements Runnable {
        private final SkierLiftRideEventGenerator generator = new SkierLiftRideEventGenerator();

        @Override
        public void run() {
            while (true) {
                try {
                    SkierLiftRideEventGenerator.SkierLiftRideEvent event = generator.generateEvent();
                    eventQueue.put(event); // Put generated event into the queue
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class APIClientTask implements Runnable {
        private final int numRequests;

        public APIClientTask(int numRequests) {
            this.numRequests = numRequests;
        }

        @Override
        public void run() {
            for (int i = 0; i < numRequests; i++) {
                try {
                    SkierLiftRideEventGenerator.SkierLiftRideEvent event = eventQueue.take(); // Take an event from the queue
                    // Send POST request with the event data
                    // If response status code is 201, send the next request
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
