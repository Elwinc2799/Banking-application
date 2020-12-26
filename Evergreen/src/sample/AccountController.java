package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;
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

    @FXML
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis balanceAxis;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        String[] month = new String[7];

        for (int i = 0, j = 7; i < 7; i++, j--)
            month[i] = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH) - j];

        dateLabel.setText(new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)] + ", " + day);
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

        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < 7; i++)
            series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.savingsAccount.getBalanceRecorder(i)));

        balanceChart.getData().addAll(series);
    }

    @FXML
    private void creditCardButtonPressed() {
        loadNextScene("creditCardScene.fxml");
    }

    @FXML
    private void debitCardButtonPressed() {
        loadNextScene("debitCardScene.fxml");
    }

    @FXML
    public void transactionHistoryButtonPushed() {
        loadNextScene("transactionHistoryScene.fxml");
    }

    @FXML
    public void transferButtonPushed() { loadNextScene("transferScene.fxml"); }

    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "loanScene.fxml" : "noLoanScene.fxml");
    }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("aboutUsScene.fxml");
    }

    private void loadNextScene(String fxml) {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource(fxml));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
