package sample.Loan;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sample.LoadingAnimation;
import sample.ReadFile;
import sample.Validation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessLoanController implements Initializable {

    double x = 0;
    double y = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LoadingAnimation loadingAnimation = new LoadingAnimation();
        panebutton.getChildren().addAll(loadingAnimation.createRectangle(generateBpdf,true), loadingAnimation.createText(generateBpdf,true));
    }

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
    private TextField name;
    @FXML
    private TextField ICno;
    @FXML
    private TextField businessname;
    @FXML
    private TextField email;
    @FXML
    private TextField address1;
    @FXML
    private TextField address2;
    @FXML
    private TextField address3;
    @FXML
    private TextField phoneno;

    @FXML
    private TextField businessType;
    @FXML
    private TextField companyEmail;
    @FXML
    private TextField companyAddress1;
    @FXML
    private TextField companyAddress2;
    @FXML
    private TextField companyAddress3;

    @FXML
    private TextField bankName;
    @FXML
    private TextField loanAmount;
    @FXML
    private TextField loanPurpose;
    @FXML
    private TextField items;
    @FXML
    private TextField price;

    @FXML
    private StackPane panebutton;

    //load to transaction history scene when transaction history button button pressed
    @FXML
    public void transactionHistoryButtonPushed() {
        loadNextScene("/sample/Scene/transactionHistoryScene.fxml");
    }

    //load to transfer scene when transfer button pressed
    @FXML
    public void transferButtonPushed() { loadNextScene("/sample/Scene/transferScene.fxml"); }

    //load to account scene when account button pressed
    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

    //load to currency exchange scene when dashboard button pressed
    @FXML
    public void dashBoardButtonPushed() { loadNextScene("/sample/Scene/currencyExchangeScene.fxml"); }

    //load to about us scene when about us button pressed
    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("/sample/Scene/aboutUsScene.fxml");
    }

    //load to loan scene if the users' account has loan taken or load to no loan scene if the user does not have a loan when loan button pressed
    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "/sample/Scene/loanScene.fxml" : "/sample/Scene/noLoanScene.fxml");
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
            Logger.getLogger(BusinessLoanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //generate a PDF form when execute
    private final Task<Void> generateBpdf = new Task<>(){
        @Override
        public Void call(){
            Font formTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
            Font title = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
            Font content = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
            Font signBoxContent = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.ITALIC);
            Document document = new Document();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            //validation for the information filled
            Validation validation = new Validation();
            if (!validation.intValidation2(price.getText(), loanAmount.getText())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Integer input wrongly");
                alert.showAndWait();
                return null;
            }

            //create a pdf file with all the information filled in the form previously
            try {
                String filename = "BusinessLoan - " + name.getText();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename + ".pdf"));
                document.open();
                Image image1 = Image.getInstance("src/sample/Scene/Resources/OrganicLogo.png");
                image1.setAbsolutePosition(400f, 650f);
                image1.scaleAbsolute(200, 200);
                document.add(image1);
                document.add(new Paragraph("BUSINESS LOAN APPLICATION E-FORM", formTitle));
                document.add(new Paragraph("BASIC DETAILS", title));
                document.add(new Paragraph("Name: " + name.getText(), content));
                document.add(new Paragraph("I/C No.: " + ICno.getText(), content));
                document.add(new Paragraph("Name of Business: " + businessname.getText(), content));
                document.add(new Paragraph("Email Address: " + email.getText(), content));
                document.add(new Paragraph("Phone: " + phoneno.getText(), content));
                document.add(new Paragraph("Address: " + address1.getText() + ", " + address2.getText() + ", " + address3.getText(), content));

                document.add(new Paragraph("COMPANY INFORMATION", title));
                document.add(new Paragraph("Type of business: " + businessType.getText(), content));
                document.add(new Paragraph("Company Address: " + companyEmail.getText(), content));
                document.add(new Paragraph("Email Address: " + companyAddress1.getText() + ", " + companyAddress2.getText() + ", " + companyAddress3.getText(), content));

                document.add(new Paragraph("BANK INFORMATION", title));
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                float[] columnWidths = {1f, 1f, 1f};
                table.setWidths(columnWidths);
                PdfPCell cell1 = new PdfPCell(new Paragraph("Bank: \n" + bankName.getText(), content));
                cell1.setPaddingLeft(10);
                cell1.setFixedHeight(50);
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell cell2 = new PdfPCell(new Paragraph("Account Type: \nSavings Account\n", content));
                cell2.setPaddingLeft(10);
                cell2.setFixedHeight(50);
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell cell3 = new PdfPCell(new Paragraph("Account Number: \n" + ReadFile.DataStorage.savingsAccount.getAccountNum(), content));
                cell3.setPaddingLeft(10);
                cell3.setFixedHeight(50);
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                document.add(table);

                document.add(new Paragraph("LOAN DETAILS", title));
                PdfPTable table2 = new PdfPTable(3); // 3 columns.
                table2.setWidthPercentage(100); //Width 100%
                table2.setSpacingBefore(10f); //Space before table
                table2.setSpacingAfter(10f); //Space after table

                //Set Column widths
                float[] columnWidths4 = {1f, 1f, 1f};
                table2.setWidths(columnWidths4);

                PdfPCell t2c1 = new PdfPCell(new Paragraph("Loan Amount: \n" + loanAmount.getText() + "\n", content));
                t2c1.setPaddingLeft(10);
                t2c1.setFixedHeight(50);
                t2c1.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell t2c2 = new PdfPCell(new Paragraph("Applicant Name: \n" + name.getText() + "\n", content));
                t2c2.setPaddingLeft(10);
                t2c2.setFixedHeight(50);
                t2c2.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell t2c3 = new PdfPCell(new Paragraph("Applicant IC: \n" + ICno.getText() + "\n", content));
                t2c3.setPaddingLeft(10);
                t2c3.setFixedHeight(50);
                t2c3.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell t2c4 = new PdfPCell(new Paragraph("Purpose of Loan: \n" + loanPurpose.getText() + "\n", content));
                t2c4.setPaddingLeft(10);
                t2c4.setFixedHeight(50);
                t2c4.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell t2c5 = new PdfPCell(new Paragraph("Collateral Items: \n" + items.getText() + "\n", content));
                t2c5.setPaddingLeft(10);
                t2c5.setFixedHeight(50);
                t2c5.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell t2c6 = new PdfPCell(new Paragraph("Collateral Price(in RM): \n" + price.getText() + "\n", content));
                t2c6.setPaddingLeft(10);
                t2c6.setFixedHeight(50);
                t2c6.setHorizontalAlignment(Element.ALIGN_LEFT);

                table2.addCell(t2c1);
                table2.addCell(t2c2);
                table2.addCell(t2c3);
                table2.addCell(t2c4);
                table2.addCell(t2c5);
                table2.addCell(t2c6);
                document.add(table2);

                document.add(new Paragraph("DECLARATION", title));
                Paragraph dec = new Paragraph("I hereby certify that the information contained herein is complete and accurate." +
                        " This information has been furnished with the understanding that it is to be used to determine the amount and " +
                        "conditions of the loan to be extended. Furthermore, I hereby authorize the financial institutes listed in this loan" +
                        " application to release necessary information to the company for which loan is being applied for in order to verify " +
                        "the information contained herein.\n\n", content);
                dec.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(dec);
                Paragraph recap = new Paragraph("*Note that the personal financial statement and bank statement needs to be sent with this" +
                        " loan application form to check credit stability.", content);
                recap.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(recap);
                document.add(new Paragraph("\n\n\n"));

                PdfPTable table3 = new PdfPTable(2);
                table2.setWidthPercentage(100);
                table2.setSpacingBefore(10f);
                table2.setSpacingAfter(10f);
                float[] columnWidths3 = {1f, 1f};
                table3.setWidths(columnWidths3);

                PdfPTable signatureTable = new PdfPTable(2);
                signatureTable.setWidthPercentage(100);
                signatureTable.setSpacingBefore(10f);
                signatureTable.setSpacingAfter(10f);
                float[] columnWidths5 = {1f, 1f};
                signatureTable.setWidths(columnWidths5);

                PdfPCell signature = new PdfPCell(new Paragraph("\n\n\n\n\n\n" + "____________________________________\n\n" + "Name: " + name.getText() + "\n\nDate:" + dtf.format(now) + "\n\n" , signBoxContent));
                signature.setPaddingLeft(10);
                signature.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell date = new PdfPCell(new Paragraph("*OFFICE USE ONLY\n\n\n\n\n\n" + "____________________________________\n\n" + "Officer Name:\n\nDate:" + "\n\n", signBoxContent));
                date.setPaddingLeft(10);
                date.setHorizontalAlignment(Element.ALIGN_LEFT);

                signatureTable.addCell(signature);
                signatureTable.addCell(date);
                document.add(signatureTable);

                document.add(new Paragraph("Note: Send the form to centralevergreeninc@gmail.com for further loan processing",signBoxContent));

                document.close();
                writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

}
