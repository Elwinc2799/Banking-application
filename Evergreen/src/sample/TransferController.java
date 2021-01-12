package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferController implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private ChoiceBox<String> bank;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextField recipientAccount;

    @FXML
    private TextField recipientName;

    @FXML
    private TextField messageLabel;

    @FXML
    private TextField fieldOTP;

    @FXML
    private Label accountLabel;

    @FXML
    private StackPane pane2;

    @FXML
    private RadioButton fb, health, entertainment, lifestyle, utilities, clothes, education, transportation;

    private String OTP;
    private String categoryText;
    private String recipientEmail;
    double x = 0;
    double y = 0;
    ObservableList<String> list = FXCollections.observableArrayList("Central Evergreen Inc.");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bank.setItems(list);
        bank.setValue("Central Evergreen Inc.");
        accountLabel.setText(ReadFile.DataStorage.savingsAccount.getAccountNum());
        LoadingAnimation loadingAnimation = new LoadingAnimation();
        pane2.getChildren().addAll(loadingAnimation.createRectangle(sendOTPTask,false), loadingAnimation.createText(sendOTPTask,false));
    }

    public String getOTP() { return OTP; }

    public void setOTP(String OTP) { this.OTP = OTP; }

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

    //task to deduct balance in account table in database
    Task<Void> deductBalanceTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                ReadFile.DataStorage.savingsAccount.savingsAccountPayment(Double.parseDouble(amountTextField.getText()));
                statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() + "WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //task to add transaction history to the database
    Task<Void> writeTransactionHistoryTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");

                PreparedStatement preparedStatement = ReadFile.connect.prepareStatement
                        ("INSERT INTO TRANSACTION_HISTORY (USERNAME, TRANS_DATE, TRANS_AMOUNT, TRANS_RECIPIENT, TRANS_TYPE, TRANS_PAYMENT_TYPE) VALUES (?, ?, ?, ?, ? ,?)");
                preparedStatement.setString(1, ReadFile.DataStorage.getUsername());
                preparedStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                preparedStatement.setDouble(3, Double.parseDouble(amountTextField.getText()));
                preparedStatement.setString(4, recipientName.getText());
                preparedStatement.setString(5, categoryText);
                preparedStatement.setString(6, "Savings");
                preparedStatement.execute();

                TransactionHistory transactionHistory = new TransactionHistory();
                transactionHistory.setTransactionDate(LocalDate.now());
                transactionHistory.setAmount(Double.parseDouble(amountTextField.getText()));
                transactionHistory.setPaymentRecipient(recipientName.getText());
                transactionHistory.setTransactionType(categoryText);
                transactionHistory.setPaymentType("Savings");
                ReadFile.DataStorage.transactionHistoryArrayList.add(transactionHistory);

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //action when next button pushed
    @FXML
    public void nextButtonPushed() {
        //prompt message when OTP input does not match the OTP sent by the application
        if (!OTP.equals(fieldOTP.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Invalid OTP");
            alert.showAndWait();
            return;
        }

        //prompt message when the amount input is less than 1
        if (Double.parseDouble(amountTextField.getText()) < 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Invalid Amount");
            alert.showAndWait();
            return;
        }


        if (bank.getValue().equals("Central Evergreen Inc.")) {
            double newBalance;
            boolean transfer = false;

            if (fb.isSelected())
                categoryText = "Food and Beverages";
            else if (health.isSelected())
                categoryText = "Health";
            else if (entertainment.isSelected())
                categoryText = "Entertainment";
            else if (lifestyle.isSelected())
                categoryText = "Lifestyle";
            else if (utilities.isSelected())
                categoryText = "Utilities";
            else if (clothes.isSelected())
                categoryText = "Clothes";
            else if (education.isSelected())
                categoryText = "Education";
            else if (transportation.isSelected())
                categoryText = "Transportation";
            else
                categoryText = "NULL";

            //Update account balance to cloud database
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = '" + recipientAccount.getText() + "'");
                while (resultSet.next())
                    if (recipientName.getText().equals(resultSet.getString(3))) {
                        newBalance = resultSet.getDouble(7) + Double.parseDouble(amountTextField.getText());
                        recipientEmail = resultSet.getString(4);
                        statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + newBalance + "WHERE ACCOUNT_ID = '" + recipientAccount.getText() + "'");
                        transfer = true;
                    }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            if (!transfer) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("The account does not exist or is invalid");
                alert.showAndWait();
                return;
            }

            new Thread(deductBalanceTask).start();
            new Thread(writeTransactionHistoryTask).start();
            new Thread(sendEmailTask).start();
            OTP = "";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("The transaction is successful.");
        alert.showAndWait();

    }

    //load to account scene when account button pressed
    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

    //load to transaction history scene when transaction history button button pressed
    @FXML
    public void transactionHistoryButtonPushed() {
        loadNextScene("/sample/Scene/transactionHistoryScene.fxml");
    }

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
            Logger.getLogger(TransferController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //task to send OTP
    public Task<Void> sendOTPTask = new Task<Void>() {
        @Override
        protected Void call() {
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            final String username = "centralEvergreenInc@gmail.com";
            final String password = "CAT201java!@#";
            generateOTP();

            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.store.protocol", "pop3");
            props.put("mail.transport.protocol", "smtp");

            try {
                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("centralEvergreenInc@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ReadFile.DataStorage.savingsAccount.getEmail(),false));
                message.setSubject("Automated Message Transfer Authentication");
                message.setText("Dear " + ReadFile.DataStorage.savingsAccount.getName() + ",\n\nThis is an automated generated email to authenticate your transfer." +
                        " Attached with this is your OTP.\n" + "OTP: " + getOTP() + "\n\nIf you did not request this OTP, please reach out to us at (+604) 653 4758.");

                message.setSentDate(new Date());
                Transport.send(message);
            } catch (MessagingException e){ e.printStackTrace(); }

            return null;
        }
    };

    //task to send email
    Task<Void> sendEmailTask = new Task<Void>() {
        @Override
        protected Void call() {
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            final String username = "centralEvergreenInc@gmail.com";
            final String password = "CAT201java!@#";

            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.store.protocol", "pop3");
            props.put("mail.transport.protocol", "smtp");

            try {
                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("centralEvergreenInc@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail,false));
                message.setSubject("Automated Message Transfer Authentication");
                message.setText("Dear " + recipientName.getText() + ",\n\nThis is an automated generated email to notify you of the transfer." +
                        " Attached with this is the details of the transfer.\n\n" + "Sender Name: " + ReadFile.DataStorage.savingsAccount.getName()
                        + "\nAmount: " + amountTextField.getText() + "\nMessage: " + messageLabel.getText() + ".");

                message.setSentDate(new Date());
                Transport.send(message);
            } catch (MessagingException e){ e.printStackTrace(); }

            return null;
        }
    };

    //function to generate OTP
    public void generateOTP() {
        char[] password = new char[6];
        String numbers = "0123456789";
        Random randomNumber = new Random();


        for (int i = 0; i < 6; i++)
            password[i] += numbers.charAt(randomNumber.nextInt(numbers.length()));

        setOTP(String.valueOf(password));
    }
}
