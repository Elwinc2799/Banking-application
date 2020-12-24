package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class NoLoanController{

    @FXML
    private StackPane root;

    @FXML
    public void transferButtonPushed() { loadNextScene("transferScene.fxml"); }

    @FXML
    public void transactionHistoryButtonPushed() { loadNextScene("transactionHistoryScene.fxml");}

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("aboutUsScene.fxml");
    }

    @FXML
    public void accountButtonPushed() { loadNextScene("accountScene.fxml");}

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

    @FXML
    public void businessLoanButtonPushed() { loadNextScene("businessLoanForm.fxml");}

    @FXML
    public void personalLoanButtonPushed() { loadNextScene("personalLoanForm.fxml");}
}
