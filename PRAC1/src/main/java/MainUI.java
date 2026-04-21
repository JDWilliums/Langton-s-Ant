import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainUI extends Application {
    private Simulation engine;
    private Canvas canvas;
    private boolean isRunning = false;
    private int stepsPerFrame = 10;
    
    // Viewport settings: 800x800 window
    private final int CANVAS_SIZE = 800;
    
    // Offset to map the logical grid to the screen. 
    // If ant starts at 5000,5000, we offset by 4600 so it appears at 400,400 (dead center).
    private final int OFFSET_X = 4600;
    private final int OFFSET_Y = 4600;

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize our models
        Grid grid = new Grid();
        List<Ant> ants = new ArrayList<>();
        
        // Drop the first ant directly in the middle of the 10k x 10k grid
        ants.add(new Ant(new Point(5000, 5000), Direction.UP)); 
        
        engine = new Simulation(grid, ants);

        // 2. Set up the UI
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        
        Button btnPlayPause = new Button("Play");
        btnPlayPause.setOnAction(e -> {
            isRunning = !isRunning;
            btnPlayPause.setText(isRunning ? "Pause" : "Play");
        });

        Slider speedSlider = new Slider(1, 1000, 10); // Min 1, Max 1000, Default 10
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> stepsPerFrame = newVal.intValue());
        
        HBox controls = new HBox(15, btnPlayPause, new Label("Simulation Speed (Steps/Frame):"), speedSlider);
        controls.setPadding(new Insets(10));
        
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(controls);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Langton's Ant - Parallel Simulation");
        
        // Critical: Shutdown the ExecutorService when the window is closed
        primaryStage.setOnCloseRequest(e -> engine.shutdown());
        primaryStage.show();

        // 3. The Render Loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isRunning) {
                    engine.run(stepsPerFrame);
                }
                draw(); // Redraw screen every frame regardless of pause state
            }
        };
        timer.start();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Clear screen with white background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw the black cells
        gc.setFill(Color.BLACK);
        Set<Point> blackCells = engine.getGrid().getBlackCells();
        for (Point p : blackCells) {
            int screenX = p.x() - OFFSET_X;
            int screenY = p.y() - OFFSET_Y;
            
            // Only draw the pixel if it is currently inside the visible viewport
            if (screenX >= 0 && screenX < CANVAS_SIZE && screenY >= 0 && screenY < CANVAS_SIZE) {
                gc.fillRect(screenX, screenY, 1, 1);
            }
        }

        // Draw the ant(s) in bright red so they are easy to spot
        gc.setFill(Color.RED);
        for (Ant ant : engine.getAnts()) {
            int screenX = ant.getPosition().x() - OFFSET_X;
            int screenY = ant.getPosition().y() - OFFSET_Y;
            
            if (screenX >= 0 && screenX < CANVAS_SIZE && screenY >= 0 && screenY < CANVAS_SIZE) {
                gc.fillRect(screenX, screenY, 2, 2); // Draw 2x2 square so it stands out
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}