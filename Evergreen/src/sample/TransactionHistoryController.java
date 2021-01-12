package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.Compare.CustomComparator;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionHistoryController implements Initializable {

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

    ObservableList<String> list = FXCollections.observableArrayList("General", "Deposits", "Payment");

    //initialize and set the choice box and label
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeChoiceBox.setItems(list);
        typeChoiceBox.setValue("General");

        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<>("paymentRecipient"));
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    //load to account scene when account button pressed
    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

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

    //action when proceed button pushed
    @FXML
    public void proceedButtonPushed() {
        double monthlyExpenses = 0, weeklyExpenses, dailyExpenses;
        int foodAndBeverages = 0, health = 0, entertainment = 0, lifestyle = 0, education = 0, utilities = 0, clothes = 0, transport = 0;

        LocalDate start = beforeDatePicker.getValue();
        LocalDate end = afterDatePicker.getValue();

        ArrayList<TransactionHistory> generalList = searchIn(ReadFile.DataStorage.transactionHistoryArrayList, transactionHistory -> (transactionHistory.getTransactionDate().equals(start) || transactionHistory.getTransactionDate().equals(end) ||
                (transactionHistory.getTransactionDate().isAfter(start)) && transactionHistory.getTransactionDate().isBefore(end)));

        generalList.sort(new CustomComparator());

        //choose related category of transaction
        for (TransactionHistory transactionHistory : generalList)
            if (!transactionHistory.getTransactionType().equals("Deposits")) {
                monthlyExpenses += transactionHistory.getAmount();

                String category = transactionHistory.getTransactionType();

                switch (category) {
                    case "Food and Beverages":
                        foodAndBeverages += transactionHistory.getAmount();
                        break;

                    case "Health":
                        health += transactionHistory.getAmount() ;
                        break;

                    case "Entertainment":
                        entertainment += transactionHistory.getAmount();
                        break;

                    case "Lifestyle":
                        lifestyle += transactionHistory.getAmount();
                        break;

                    case "Education":
                        education += transactionHistory.getAmount();
                        break;

                    case "Utilities":
                        utilities += transactionHistory.getAmount();
                        break;

                    case "Clothes":
                        clothes += transactionHistory.getAmount();
                        break;

                    case "Transport":
                        transport += transactionHistory.getAmount();
                        break;

                    default:
                        break;
                }
            }

        //set label
        weeklyExpenses = monthlyExpenses / 4;
        dailyExpenses = weeklyExpenses / 7;
        dailyExpenseLabel.setText(String.format("%.2f", dailyExpenses));
        weeklyExpenseLabel.setText(String.format("%.2f", weeklyExpenses));
        monthlyExpenseLabel.setText(String.format("%.2f", monthlyExpenses));

        //set pie chart data
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

        //show pie chart
        pieChart.setData(pieChartData);
        pieChart.setStartAngle(90);

        switch (typeChoiceBox.getValue()) {
            case "General":
                tableView.setItems(getTransactionHistory(generalList));
                break;

            case "Deposits":
                ArrayList<TransactionHistory> depositList = searchIn(generalList, transactionHistory -> transactionHistory.getTransactionType().equals("Deposits"));
                tableView.setItems(getTransactionHistory(depositList));
                break;

            case "Payment":
                ArrayList<TransactionHistory> paymentList = searchIn(generalList, transactionHistory -> !(transactionHistory.getTransactionType().equals("Deposits")));
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

    public interface Matcher<TransactionHistory> {
        boolean matches(TransactionHistory transactionHistory);
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
            Logger.getLogger(TransactionHistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<TransactionHistory> getTransactionHistory(ArrayList<TransactionHistory> arrayList) {
        ObservableList<TransactionHistory> transactionHistories = FXCollections.observableArrayList();

        transactionHistories.addAll(arrayList);

        return transactionHistories;
    }
}
