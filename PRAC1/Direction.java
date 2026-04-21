public enum Direction {
    UP, RIGHT, DOWN, LEFT; // Order is important for turning logic

    public Direction turnRight() {
        return values()[(this.ordinal() + 1) % 4]; // Turns right by moving to the next enum value
    }

    public Direction turnLeft() {
        return values()[(this.ordinal() + 3) % 4]; // Turns left by moving to the previous enum value
    }
}