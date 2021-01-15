package sample;

import eu.hansolo.enzo.notification.Notification;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
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

    //function to create a rectangle with event when user react on it
    public Rectangle createRectangle(Task<Void> task, boolean isPdf) {
        rectangle = new Rectangle(10,200,100, 30);
        rectangle.setArcHeight(15);
        rectangle.setArcWidth(15);
        rectangle.setFill(Color.web("#cfe2fb"));
        rectangle.setSmooth(true);

        rectangle.setOnMousePressed(event -> {
            if (!isTaskRunning()){
                preload(task,isPdf);
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

    //function to preload a task
    private void preload(Task <Void> task, boolean isPdf) {
        try {
            setTaskRunning(true);
            rectangle.setCursor(Cursor.WAIT);
            text.setVisible(false);
            runAnimation(task,isPdf);
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void setTaskRunning(boolean running) { isRunning = running; }

    private boolean isTaskRunning() { return isRunning; }

    //function to run the animation
    public void runAnimation(Task<Void> task,boolean isPdf) throws InterruptedException {
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
            task.setOnSucceeded(e -> runResultAnimation(true,isPdf));
            task.setOnFailed(e -> runResultAnimation(false,isPdf));
        });
        sequential.play();
    }

    //function to run the result animation and show the text after finish
    private void runResultAnimation(boolean flag, boolean isPdf) {
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
        sequential.setOnFinished(event -> onComplete(flag ? "Success" : "Failed",isPdf));
        sequential.getChildren().addAll(strokeSuccess, ptr);
        sequential.play();
    }

    //function to set the message when the animation done running
    private void onComplete(String textMessage,boolean isPdf) {
        text.setText(textMessage);
        text.setVisible(true);
        rectangle.setCursor(Cursor.HAND);
        setTaskRunning(false);
        if(isPdf && textMessage.equals("Success")){
            Notification.Notifier.setPopupLocation(null, Pos.BOTTOM_RIGHT);
            Notification.Notifier.INSTANCE.notifySuccess("Pdf Location","Saved to Evergreen.exe directory\nEmail to centralevergreeninc@gmail.com");
        }
        else if(isPdf && textMessage.equals("Failed")){
            Notification.Notifier.setPopupLocation(null, Pos.BOTTOM_RIGHT);
            Notification.Notifier.INSTANCE.notifyError("Error message","Input error detected");
        }
    }

    //function to create a text
    public Text createText(Task<Void> task,boolean isPdf) {
        text = new Text();
        text.setText("Generate");
        text.setFont(javafx.scene.text.Font.font("Open Sans"));
        text.setFill(Color.BLACK);
        text.setSmooth(true);
        text.setBoundsType(TextBoundsType.VISUAL);
        text.setOnMousePressed(t -> {
            if (!isTaskRunning()){
                preload(task,isPdf);
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
