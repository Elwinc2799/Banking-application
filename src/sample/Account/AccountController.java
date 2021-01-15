package sample.Account;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.ReadFile;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountController extends Thread implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private Label nameLabel, emailLabel, savingsAccountLabel, depositAccountLabel, dateLabel, savingsAmountLabel, depositAmountLabel;

    @FXML
    private Label creditNumLabel, debitNumLabel, creditDateLabel, debitDateLabel;

    @FXML
    private LineChart<?, ?> balanceChart;

    double x = 0;
    double y = 0;

    //allow user to drag and move the application
    @FXML
    void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar calendar = Calendar.getInstance();
        String[] month = new String[7];
        LocalDate now = LocalDate.now();
        String dateSuffix;

        //set the suffix for date
        switch (now.getDayOfMonth()) {
            case 1: case 21: case 31: dateSuffix = "st";
                break;

            case 2: case 22: dateSuffix = "nd";
                break;

            case 3: case 23: dateSuffix = "rd";
                break;

            default: dateSuffix = "th";
        }

        //get and store the last 7 month into month array
        for (int i = 0, j = 8; i < 7; i++, j--) {
            month[i] = new DateFormatSymbols().getMonths()[now.minusMonths(j).getMonthValue()];
        }

        //set text to the respective label
        dateLabel.setText(new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.DATE) + dateSuffix);
        nameLabel.setText(ReadFile.DataStorage.savingsAccount.getName());
        emailLabel.setText(ReadFile.DataStorage.savingsAccount.getEmail());

        savingsAccountLabel.setText(ReadFile.DataStorage.savingsAccount.getAccountNum());
        savingsAmountLabel.setText(String.format("%.2f", ReadFile.DataStorage.savingsAccount.getBalance()));
        depositAccountLabel.setText(ReadFile.DataStorage.depositAccount.getAccountNum());
        depositAmountLabel.setText(String.format("%.2f", ReadFile.DataStorage.depositAccount.getBalance()));

        creditNumLabel.setText("**** **** **** " + ReadFile.DataStorage.creditCard.getCardID().substring(15));
        creditDateLabel.setText(ReadFile.DataStorage.creditCard.getExpiryDate());
        debitNumLabel.setText("**** **** **** " + ReadFile.DataStorage.debitCard.getCardID().substring(15));
        debitDateLabel.setText(ReadFile.DataStorage.debitCard.getExpiryDate());

        //create a XYChart to show the balance to the respective month
        XYChart.Series series = new XYChart.Series();

        for (int i = 0, j = 6; i < 7; i++, j--)
            series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.savingsAccount.getBalanceRecorder(j)));

        balanceChart.getData().addAll(series);
    }

    //load to credit card scene when credit card button pressed
    @FXML
    private void creditCardButtonPressed() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.15));
        pause.setOnFinished(event -> loadNextScene("/sample/Scene/creditCardScene.fxml"));
        pause.play();
    }

    //load to debit card scene when debit card button pressed
    @FXML
    private void debitCardButtonPressed() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.15));
        pause.setOnFinished(event -> loadNextScene("/sample/Scene/debitCardScene.fxml"));
        pause.play();
    }

    //load to transaction history scene when transaction history button button pressed
    @FXML
    public void transactionHistoryButtonPushed() {
        loadNextScene("/sample/Scene/transactionHistoryScene.fxml");
    }

    //load to transfer scene when transfer button pressed
    @FXML
    public void transferButtonPushed() { loadNextScene("/sample/Scene/transferScene.fxml"); }

    //load to loan scene if the users' account has loan taken or load to no loan scene if the user does not have a loan when loan button pressed
    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "/sample/Scene/loanScene.fxml" : "/sample/Scene/noLoanScene.fxml");
    }

    //load to currency exchange scene when dashboard button pressed
    @FXML
    public void dashBoardButtonPushed() { loadNextScene("/sample/Scene/currencyExchangeScene.fxml"); }

    //load to about us scene when about us button pressed
    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("/sample/Scene/aboutUsScene.fxml");
    }

    //the function to allow the application to change from one scene to another scene
    private void loadNextScene(String fxml) {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource(fxml));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
