import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Grid {
    // We store only the black cells (sparse data structure)
    private final ConcurrentHashMap<Point, Boolean> grid = new ConcurrentHashMap<>();

    private final int REGION_SIZE = 100; // 100x100 region size
    private final ConcurrentHashMap<Point, ReentrantLock> regionLocks = new ConcurrentHashMap<>();

    private Point getRegionCoordinate(Point p) {
        int regionX = p.x() / REGION_SIZE;
        int regionY = p.y() / REGION_SIZE;
        return new Point(regionX, regionY);
    }

    public boolean isWhite(Point p) {
        // If the cell is in the grid, return its colour (true for black, false for white)
        return ! grid.containsKey(p);
    }

    public void flipCell (Point p) {
        Point regionCoord = getRegionCoordinate(p);

        // Creates a lock for the region
        ReentrantLock lock = regionLocks.computeIfAbsent(regionCoord, k -> new ReentrantLock());

        lock.lock();
        try {
            if (grid.containsKey(p)) {
                grid.remove(p); // Flip to white
            } else {
                grid.put(p, true); // Flip to black
            }
        } finally {
            lock.unlock();
        }
    }
}
