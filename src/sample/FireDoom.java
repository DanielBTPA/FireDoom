package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FireDoom extends Application {

    private Color[] doomFirePallete = new Color[]{
            Color.rgb(7, 7, 7),       //  0
            Color.rgb(31, 7, 7),      //  1
            Color.rgb(47, 15, 7),     //  2
            Color.rgb(71, 15, 7),     //  3
            Color.rgb(87, 23, 7),     //  4
            Color.rgb(103, 31, 7),    //  5
            Color.rgb(119, 31, 7),    //  6
            Color.rgb(143, 39, 7),    //  7
            Color.rgb(159, 47, 7),    //  8
            Color.rgb(175, 63, 7),    //  9
            Color.rgb(191, 71, 7),    // 10
            Color.rgb(199, 71, 7),    // 11
            Color.rgb(223, 79, 7),    // 12
            Color.rgb(223, 87, 7),    // 13
            Color.rgb(223, 87, 7),    // 14
            Color.rgb(215, 95, 7),    // 15
            Color.rgb(215, 95, 7),    // 16
            Color.rgb(215, 103, 15),  // 17
            Color.rgb(207, 111, 15),  // 18
            Color.rgb(207, 119, 15),  // 19
            Color.rgb(207, 127, 15),  // 20
            Color.rgb(207, 135, 23),  // 21
            Color.rgb(199, 135, 23),  // 22
            Color.rgb(199, 143, 23),  // 23
            Color.rgb(199, 151, 31),  // 24
            Color.rgb(191, 159, 31),  // 25
            Color.rgb(191, 159, 31),  // 26
            Color.rgb(191, 167, 39),  // 27
            Color.rgb(191, 167, 39),  // 28
            Color.rgb(191, 175, 47),  // 29
            Color.rgb(183, 175, 47),  // 30
            Color.rgb(183, 183, 47),  // 31
            Color.rgb(183, 183, 55),  // 32
            Color.rgb(207, 207, 111), // 33
            Color.rgb(223, 223, 159), // 34
            Color.rgb(239, 239, 199), // 35
            Color.rgb(255, 255, 255), // 36
    };

    private int fireWidth = 50, fireHeight = 50;

    private List<Integer> firePixelsArray = new ArrayList<>();

    public void createFireDataStructure() {
        int numOfPixels = (fireWidth * fireHeight);

        for (int i = 0; i < numOfPixels; i++) {
            firePixelsArray.add(0);
        }
    }

    public void calculationFirePropagation() {

        for (int column = 0; column < fireWidth; column++) {
            for (int row = 0; row < fireHeight; row++) {
                int pixelIndex = column + (fireWidth * row);
                updateFireIntensifyPerPixel(pixelIndex);
            }
        }

    }

    public void updateFireIntensifyPerPixel(int currentPixelIndex) {
        int bellowPixelIndex = currentPixelIndex + fireWidth;

        if (bellowPixelIndex >= (fireWidth * fireHeight)) return;

        int decay = (int) Math.floor(Math.random() * 3);
        int newFireIntensify = Math.max(0, firePixelsArray.get(bellowPixelIndex) - decay);
        firePixelsArray.set(Math.max(0, currentPixelIndex - decay), newFireIntensify);
    }

    private void createFireSource() {
        for (int column = 0; column <= fireWidth; column++) {
            int overflowPixelIndex = (fireWidth * fireHeight);
            int pixelIndex = (overflowPixelIndex - fireWidth) + column;

            System.out.println(pixelIndex);
            firePixelsArray.set(pixelIndex - 1, 36);
        }
    }

    private final double sizePixel = 10;
    private final double spacing = 1;

    public void renderFire() {

        for (int row = 0; row < fireHeight; row++) {
            for (int column = 0; column < fireWidth; column++) {
                pane.getChildren().add(createCell(row, column));
            }
        }

    }

    private Rectangle createCell(int r, int c) {
        Rectangle rectangle = new Rectangle((sizePixel * c *spacing), (sizePixel * r * spacing), sizePixel, sizePixel);
        rectangle.setFill(Color.WHITE);

        return rectangle;
    }

    private Pane pane;

    private void initialize() {
        createFireDataStructure();
        createFireSource();
        renderFire();

        Thread thread = new Thread(threadRender);
        thread.start();
    }

    private Task threadRender = new Task() {

        @Override
        protected Object call() throws Exception {

            while (!isCancelled()) {
                calculationFirePropagation();

                Platform.runLater(() -> {
                    for (int i = 0; i < (fireWidth * fireHeight); i++) {
                        int colorIndex = firePixelsArray.get(i);
                        Rectangle rectangle = (Rectangle) pane.getChildren().get(i);
                        rectangle.setFill(doomFirePallete[colorIndex]);
                    }
                });

                Thread.sleep(60);
            }

            return null;
        }

    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane = new Pane();
        pane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(pane, fireWidth * 10, fireHeight * 10);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Doom Fire!!!");
        primaryStage.setOnCloseRequest(event -> threadRender.cancel());
        primaryStage.show();

        initialize();
    }
}
