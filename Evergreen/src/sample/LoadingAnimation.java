package sample;

import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.util.Duration;

public class LoadingAnimation {

    private Rectangle rectangle;
    private Text text;
    private boolean isRunning;

    public Rectangle createRectangle(Task<Void> task) {
        rectangle = new Rectangle(10,200,100, 30);
        rectangle.setArcHeight(15);
        rectangle.setArcWidth(15);
        rectangle.setFill(Color.web("#cfe2fb"));
        rectangle.setSmooth(true);

        rectangle.setOnMousePressed(event -> {
            if (!isTaskRunning()){
                preload(task);
            }
        });
        rectangle.setOnMouseEntered(event -> {
            if (!isTaskRunning()) {
                rectangle.setOpacity(0.7);
                rectangle.setCursor(Cursor.HAND);
            }
        });
        rectangle.setOnMouseExited(event -> {
            if (!isTaskRunning()) {
                rectangle.setOpacity(1);
                rectangle.setCursor(Cursor.DEFAULT);
            }
        });

        return rectangle;
    }

    private void preload(Task <Void> task) {
        try {
            setTaskRunning(true);
            rectangle.setCursor(Cursor.WAIT);
            text.setVisible(false);
            runAnimation(task);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void setTaskRunning(boolean running) { isRunning = running; }

    private boolean isTaskRunning() { return isRunning; }

    public void runAnimation(Task<Void> task) throws InterruptedException {
        KeyValue kv = new KeyValue(rectangle.widthProperty(), 30);
        KeyValue kv2 = new KeyValue(rectangle.xProperty(), 10);
        KeyValue kv3 = new KeyValue(rectangle.arcHeightProperty(), 50);
        KeyValue kv4 = new KeyValue(rectangle.arcWidthProperty(), 50);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), kv, kv2, kv3, kv4);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame);

        FillTransition fillTransition = new FillTransition(Duration.millis(500), rectangle, Color.web("#cfe2fb"), Color.TRANSPARENT);
        StrokeTransition stroke = new StrokeTransition(Duration.millis(500), rectangle, Color.TRANSPARENT, Color.DARKGRAY);

        ParallelTransition ptr = new ParallelTransition();
        ptr.getChildren().addAll(fillTransition, timeline, stroke);

        RotateTransition rotation = new RotateTransition(Duration.millis(2000), rectangle);
        rotation.setFromAngle(0);
        rotation.setToAngle(360);

        StrokeTransition strokeSuccess = new StrokeTransition(Duration.millis(500), rectangle, Color.DARKGRAY,  Color.web("#cfe2fb"));

        SequentialTransition sequential = new SequentialTransition();
        sequential.getChildren().addAll(ptr, rotation, strokeSuccess );

        sequential.setOnFinished(event -> {
            new Thread(task).start();
            task.setOnSucceeded(e -> runResultAnimation(true));
            task.setOnFailed(e -> runResultAnimation(false));
        });
        sequential.play();
    }

    private void runResultAnimation(boolean flag) {
        KeyValue kv = new KeyValue(rectangle.widthProperty(), 100);
        KeyValue kv2 = new KeyValue(rectangle.xProperty(), 10);
        KeyValue kv3 = new KeyValue(rectangle.arcHeightProperty(), 15);
        KeyValue kv4 = new KeyValue(rectangle.arcWidthProperty(), 15);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), kv, kv2, kv3, kv4);
        FillTransition fillTransition;
        StrokeTransition strokeSuccess;

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame);

        if (flag) {
            fillTransition = new FillTransition(Duration.millis(500), rectangle, Color.LIGHTGRAY, Color.web("#20b2aa"));
            strokeSuccess = new StrokeTransition(Duration.millis(500), rectangle, Color.web("#cfe2fb"), Color.web("#20b2aa"));
        } else {
            fillTransition = new FillTransition(Duration.millis(500), rectangle,  Color.LIGHTGRAY, Color.RED.brighter());
            strokeSuccess = new StrokeTransition(Duration.millis(500), rectangle,  Color.web("#cfe2fb"), Color.RED);
        }

        ParallelTransition ptr = new ParallelTransition();
        ptr.getChildren().addAll(fillTransition, timeline);

        SequentialTransition sequential = new SequentialTransition();
        sequential.setOnFinished(event -> onComplete(flag ? "Success" : "Failed"));
        sequential.getChildren().addAll(strokeSuccess, ptr);
        sequential.play();
    }

    private void onComplete(String textMessage) {
        text.setText(textMessage);
        text.setVisible(true);
        rectangle.setCursor(Cursor.HAND);
        setTaskRunning(false);
    }

    public Text createText(Task<Void> task) {
        text = new Text();
        text.setText("Generate");
        text.setFont(javafx.scene.text.Font.font("Open Sans"));
        text.setFill(Color.BLACK);
        text.setSmooth(true);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setOnMousePressed(t -> {
            if (!isTaskRunning()){
                preload(task);
            }
        });
        text.setOnMouseEntered(event -> {
            rectangle.setOpacity(0.7);
            text.setCursor(Cursor.HAND);
        });
        text.setOnMouseExited(event -> {
            rectangle.setOpacity(1);
            text.setCursor(Cursor.DEFAULT);
        });
        text.setTextAlignment(TextAlignment.CENTER);
        return text;
    }
}
