public class Ant {
    private Point position;
    private Direction direction;

    public Ant(Point startPosition, Direction startDirection) {
        this.position = startPosition;
        this.direction = startDirection;
    }
    
    // Getters and setters for position and direction
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }
    public Direction getDirection() {
        return direction;
    }

    public void step(boolean isWhiteCell) {
        if (isWhiteCell) {
            direction = direction.turnRight();
        } else {
            direction = direction.turnLeft();
        }
        moveForward();
    }

    private void moveForward() {
        switch (direction) {
            case UP -> position = new Point(position.x(), position.y() - 1);
            case DOWN -> position = new Point(position.x(), position.y() + 1);
            case LEFT -> position = new Point(position.x() - 1, position.y());
            case RIGHT -> position = new Point(position.x() + 1, position.y());
        }
    }

}
