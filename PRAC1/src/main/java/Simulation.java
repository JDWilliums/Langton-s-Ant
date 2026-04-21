
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;



public class Simulation {
    private final Grid grid;
    private final List<Ant> ants;
    private final ExecutorService executor;

    public Simulation(Grid grid, List<Ant> ants) {
        this.grid = grid;
        this.ants = ants;

        // Create a thread pool with a number of threads equal to the number of available CPU cores
        int cores = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(cores);
    }

    public void run(int steps) {
        for (int i = 0; i < steps; i++) {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (Ant ant : ants) { // Create a task for each ant to perform its step
                tasks.add(() -> {
                    Point currentPosition = ant.getPosition();

                    boolean wasWhite = grid.flipAndReturnOldColour(currentPosition); // Atomically read and flip the cell

                    ant.step(wasWhite); // Move the ant based on the old colour

                    return null;
                });
            }

            try {
                // Now, submit all tasks and wait for them all to finish before proceeding to the next step
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Simulation interrupted: " + e.getMessage());
                break;
            }

        
        }

        
    }

    public void shutdown() {
        executor.shutdown();
    }

    public Grid getGrid() {
        return grid;
    }
    public List<Ant> getAnts() {
        return ants;
    }
}