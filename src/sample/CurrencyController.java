package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrencyController implements Initializable {

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
    private Label dateLabel;

    @FXML
    private Label BND, buyBND, sellBND;

    @FXML
    private Label CNY, buyCNY, sellCNY;

    @FXML
    private Label EUR, buyEUR, sellEUR;

    @FXML
    private Label INR, buyINR, sellINR;

    @FXML
    private Label JPY, buyJPY, sellJPY;

    @FXML
    private Label KRW, buyKRW, sellKRW;

    @FXML
    private Label SGD, buySGD, sellSGD;

    @FXML
    private Label GBP, buyGBP, sellGBP;

    @FXML
    private Label USD,buyUSD, sellUSD;

    //initialize daily currency buy and sell rate with their respective flags
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateLabel.setText(LocalDate.now().getMonth() + ", " + LocalDate.now().getDayOfMonth() + " " + LocalDate.now().getYear());

        try {
            for (Map.Entry<String, ArrayList<Double>> map : ReadFile.DataStorage.currencyMap.entrySet()) {
                String key = map.getKey();
                ArrayList<Double> rates = map.getValue();

                switch (key) {
                    case "1 Brunei Dollar":
                        buyBND.setText(Double.toString(rates.get(0)));
                        sellBND.setText(Double.toString(rates.get(1)));
                        BND.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;

                    case "100 Chinese Renminbi":
                        buyCNY.setText(String.format("%.2f", rates.get(0)));
                        sellCNY.setText(String.format("%.2f", rates.get(1)));
                        CNY.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;

                    case "1 Euro":
                        buyEUR.setText(Double.toString(rates.get(0)));
                        sellEUR.setText(Double.toString(rates.get(1)));
                        EUR.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;

                    case "100 Indian Rupee":
                        buyINR.setText(String.format("%.2f", rates.get(0) * 100));
                        sellINR.setText(String.format("%.2f", rates.get(1) * 100));
                        INR.setText(String.format("%.2f", (1 / rates.get(1)) * 100));
                        break;

                    case "100 Japanese Yen":
                        buyJPY.setText(String.format("%.2f", rates.get(0) * 100));
                        sellJPY.setText(String.format("%.2f", rates.get(1) * 100));
                        JPY.setText(String.format("%.2f", (1 / rates.get(1) * 100)));
                        break;

                    case "100 Swedish Krona":
                        buyKRW.setText(String.format("%.2f", rates.get(0) * 100));
                        sellKRW.setText(String.format("%.2f", rates.get(1) * 100));
                        KRW.setText(String.format("%.2f", (1 / rates.get(1)) * 100));
                        break;

                    case "1 Singapore Dollar":
                        buySGD.setText(Double.toString(rates.get(0)));
                        sellSGD.setText(Double.toString(rates.get(1)));
                        SGD.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;

                    case "1 Sterling Pound":
                        buyGBP.setText(Double.toString(rates.get(0)));
                        sellGBP.setText(Double.toString(rates.get(1)));
                        GBP.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;

                    case "1 US Dollar":
                        buyUSD.setText(Double.toString(rates.get(0)));
                        sellUSD.setText(Double.toString(rates.get(1)));
                        USD.setText(String.format("%.2f", (1 / rates.get(1))));
                        break;
                }
            }
        } catch (Exception ignored) { }
    }

    //reload the scene when refresh button pushed
    @FXML
    public void refreshButtonPushed() {
        new Thread(ReadFile.task).start();
        loadNextScene("/sample/Scene/currencyExchangeScene.fxml");
    }

    //load to account scene when account button pressed
    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

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
            Logger.getLogger(CurrencyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
