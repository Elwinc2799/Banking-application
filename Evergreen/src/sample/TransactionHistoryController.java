package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionHistoryController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private TableView<TransactionHistory> tableView;

    @FXML
    private TableColumn<TransactionHistory, LocalDate> transactionDateColumn;

    @FXML
    private TableColumn<TransactionHistory, String> recipientColumn;

    @FXML
    private TableColumn<TransactionHistory, String> transactionTypeColumn;

    @FXML
    private TableColumn<TransactionHistory, String> paymentTypeColumn;

    @FXML
    private TableColumn<TransactionHistory, Double> amountColumn;

    @FXML
    private Label dailyExpenseLabel;

    @FXML
    private Label weeklyExpenseLabel;

    @FXML
    private Label monthlyExpenseLabel;

    @FXML
    private DatePicker beforeDatePicker;

    @FXML
    private DatePicker afterDatePicker;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private PieChart pieChart;

    private ArrayList<TransactionHistory> generalList = new ArrayList<>();
    private ArrayList<TransactionHistory> depositList = new ArrayList<>();
    private ArrayList<TransactionHistory> paymentList = new ArrayList<>();

    ObservableList<String> list = FXCollections.observableArrayList("General", "Deposits", "Payment");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeChoiceBox.setItems(list);
        typeChoiceBox.setValue("General");

        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<TransactionHistory, LocalDate>("transactionDate"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<TransactionHistory, String>("paymentRecipient"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<TransactionHistory, String>("transactionType"));
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<TransactionHistory, String>("paymentType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<TransactionHistory, Double>("amount"));
    }

    @FXML
    public void accountButtonPushed() { loadNextScene("accountScene.fxml"); }

    @FXML
    public void transferButtonPushed() { loadNextScene("transferScene.fxml"); }

    @FXML
    public void loanButtonPushed() { loadNextScene((ReadFile.DataStorage.loan) ? "loanScene.fxml" : "noLoanScene.fxml"); }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() { loadNextScene("aboutUsScene.fxml"); }

    @FXML
    public void proceedButtonPushed() {
        double monthlyExpenses = 0, weeklyExpenses, dailyExpenses;
        int foodAndBeverages = 0, health = 0, entertainment = 0, lifestyle = 0, education = 0, utilities = 0, clothes = 0, transport = 0;

        LocalDate start = beforeDatePicker.getValue();
        LocalDate end = afterDatePicker.getValue();

        generalList = searchIn(ReadFile.DataStorage.transactionHistoryArrayList, transactionHistory -> (transactionHistory.getTransactionDate().equals(start) || transactionHistory.getTransactionDate().equals(end) ||
                (transactionHistory.getTransactionDate().isAfter(start)) && transactionHistory.getTransactionDate().isBefore(end)));

        generalList.sort(new CustomComparator());

        for (TransactionHistory transactionHistory : generalList)
            if (!transactionHistory.getTransactionType().equals("Deposits")) {
                monthlyExpenses += transactionHistory.getAmount();

                String category = transactionHistory.getTransactionType();

                switch (category) {
                    case "Food and Beverages":
                        foodAndBeverages++;
                        break;

                    case "Health":
                        health++;
                        break;

                    case "Entertainment":
                        entertainment++;
                        break;

                    case "Lifestyle":
                        lifestyle++;
                        break;

                    case "Education":
                        education++;
                        break;

                    case "Utilities":
                        utilities++;
                        break;

                    case "Clothes":
                        clothes++;
                        break;

                    case "Transport":
                        transport++;
                        break;

                    default:
                        break;
                }
            }

        weeklyExpenses = monthlyExpenses / 4;
        dailyExpenses = weeklyExpenses / 7;
        dailyExpenseLabel.setText(String.format("%.2f", dailyExpenses));
        weeklyExpenseLabel.setText(String.format("%.2f", weeklyExpenses));
        monthlyExpenseLabel.setText(String.format("%.2f", monthlyExpenses));

        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                new PieChart.Data("Food and Beverages", foodAndBeverages),
                new PieChart.Data("Health", health),
                new PieChart.Data("Entertainment", entertainment),
                new PieChart.Data("Lifestyle", lifestyle),
                new PieChart.Data("Education", education),
                new PieChart.Data("Utilities", utilities),
                new PieChart.Data("Clothes", clothes),
                new PieChart.Data("Transport", transport)
        );

        pieChart.setData(pieChartData);
        pieChart.setStartAngle(90);

        switch (typeChoiceBox.getValue()) {
            case "General":
                tableView.setItems(getTransactionHistory(generalList));
                break;

            case "Deposits":
                depositList = searchIn(generalList, transactionHistory -> transactionHistory.getTransactionType().equals("Deposits"));
                tableView.setItems(getTransactionHistory(depositList));
                break;

            case "Payment":
                paymentList = searchIn(generalList, transactionHistory -> !(transactionHistory.getTransactionType().equals("Deposits")));
                tableView.setItems(getTransactionHistory(paymentList));
                break;

            default: break;
        }
    }

    public static <TransactionHistory> ArrayList<TransactionHistory> searchIn(ArrayList<TransactionHistory> list, Matcher<TransactionHistory> matcher) {
        ArrayList<TransactionHistory> transactionHistoryArrayList = new ArrayList<>();

        for (TransactionHistory newList : list) {
            if (matcher.matches(newList))
                transactionHistoryArrayList.add(newList);
        }

        return transactionHistoryArrayList;
    }

    interface Matcher<TransactionHistory> {
        boolean matches(TransactionHistory transactionHistory);
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

    public ObservableList<TransactionHistory> getTransactionHistory(ArrayList<TransactionHistory> arrayList) {
        ObservableList<TransactionHistory> transactionHistories = FXCollections.observableArrayList();

        for (int i = 0; i < arrayList.size(); i++)
            transactionHistories.add(arrayList.get(i));

        return transactionHistories;
    }
}
