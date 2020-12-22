package sample;

import com.itextpdf.layout.property.TabAlignment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ResourceBundle;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import javax.management.Notification;

public class PersonalLoanController {
    @FXML
    private StackPane root;

    @FXML
    private TextField nameP;
    @FXML
    private TextField ICnoP;
    @FXML
    private TextField emailP;
    @FXML
    private TextField addressP1;
    @FXML
    private TextField addressP2;
    @FXML
    private TextField addressP3;
    @FXML
    private TextField phonenoP;

    @FXML
    private TextField noOfDependantsP;
    @FXML
    private TextField educationLevelP;
    @FXML
    private TextField maritalP;
    @FXML
    private TextField homeTelNoP;

    @FXML
    private TextField bankNameP;
    @FXML
    private TextField accNum;
    @FXML
    private TextField accType;
    @FXML
    private TextField loanAmount;

    @FXML
    private TextField companynameP;
    @FXML
    private TextField posP;
    @FXML
    private TextField incomeP;
    @FXML
    private TextField loanPurposeP;
    @FXML
    private TextField itemsP;
    @FXML
    private TextField priceP;

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
    void generatePFormPushed() {
        Font formTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
        Font title = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD);
        Font content = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
        Font signBoxContent = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.ITALIC);
        Document document = new Document();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        try {
            String filename = nameP.getText();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename + ".pdf"));
            document.open();

            Image image1 = Image.getInstance("src/sample/Resources/OrganicLogo.png");
            image1.setAbsolutePosition(420f, 650f);
            image1.scaleAbsolute(200, 200);
            document.add(image1);
            document.add(new Paragraph("PERSONAL LOAN APPLICATION E-FORM", formTitle));
            document.add(new Paragraph("BASIC DETAILS", title));
            document.add(new Paragraph("Name: " + nameP.getText(), content));
            document.add(new Paragraph("I/C No.: " + ICnoP.getText(), content));
            document.add(new Paragraph("Email Address: " + emailP.getText(), content));
            document.add(new Paragraph("Phone number: " + phonenoP.getText(), content));
            document.add(new Paragraph("Address: " + addressP1.getText() + ", " + addressP2.getText() + ", " + addressP3.getText(), content));

            document.add(new Paragraph("FAMILY INFORMATION", title));
            PdfPTable t1 = new PdfPTable(2);
            t1.setWidthPercentage(100);
            t1.setSpacingBefore(10f);
            t1.setSpacingAfter(10f);

            float[] columnWidths1 = {1f, 1f};
            t1.setWidths(columnWidths1);
            PdfPCell t1c1 = new PdfPCell(new Paragraph("No of Dependants: \n" + noOfDependantsP.getText() + "\n", content));
            t1c1.setPaddingLeft(10);
            t1c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell t1c2 = new PdfPCell(new Paragraph("Highest Dependent's Education Level: \n" + educationLevelP.getText() + "\n", content));
            t1c2.setPaddingLeft(10);
            t1c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell t1c3 = new PdfPCell(new Paragraph("Marital Status: " + maritalP.getText() + "\n", content));
            t1c3.setPaddingLeft(10);
            t1c3.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell t1c4 = new PdfPCell(new Paragraph("Home Tel No.: " + homeTelNoP.getText() + "\n", content));
            t1c4.setPaddingLeft(10);
            t1c4.setHorizontalAlignment(Element.ALIGN_LEFT);

            t1.addCell(t1c1);
            t1.addCell(t1c2);
            t1.addCell(t1c3);
            t1.addCell(t1c4);
            document.add(t1);

            document.add(new Paragraph("BANK INFORMATION", title));
            PdfPTable t2 = new PdfPTable(3);
            t2.setWidthPercentage(100);
            t2.setSpacingBefore(10f);
            t2.setSpacingAfter(10f);

            float[] columnWidths2 = {1f, 1f, 1f};
            t2.setWidths(columnWidths2);
            PdfPCell t2c1 = new PdfPCell(new Paragraph("Bank: \n" + bankNameP.getText() + "\n", content));
            t2c1.setPaddingLeft(10);
            t2c1.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t2c2 = new PdfPCell(new Paragraph("Account Number: \n" + accNum.getText() + "\n", content));
            t2c2.setPaddingLeft(10);
            t2c2.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t2c3 = new PdfPCell(new Paragraph("Account Type: \n" + accType.getText() + "\n", content));
            t2c3.setPaddingLeft(10);
            t2c3.setHorizontalAlignment(Element.ALIGN_LEFT);

            t2.addCell(t2c1);
            t2.addCell(t2c2);
            t2.addCell(t2c3);
            document.add(t2);

            document.add(new Paragraph("WORKING BACKGROUND", title));
            PdfPTable t3 = new PdfPTable(3);
            t3.setWidthPercentage(100);
            t3.setSpacingBefore(10f);
            t3.setSpacingAfter(10f);

            float[] columnWidths3 = {1f, 1f, 1f};
            t3.setWidths(columnWidths3);
            PdfPCell t3c1 = new PdfPCell(new Paragraph("Company Name: \n" + companynameP.getText() + "\n", content));
            t3c1.setPaddingLeft(10);
            t3c1.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t3c2 = new PdfPCell(new Paragraph("Position: \n" + posP.getText() + "\n", content));
            t3c2.setPaddingLeft(10);
            t3c2.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t3c3 = new PdfPCell(new Paragraph("Income per month: \n" + incomeP.getText() + "\n", content));
            t3c3.setPaddingLeft(10);
            t3c3.setHorizontalAlignment(Element.ALIGN_LEFT);

            t3.addCell(t3c1);
            t3.addCell(t3c2);
            t3.addCell(t3c3);
            document.add(t3);

            document.add(new Paragraph("LOAN DETAILS", title));
            PdfPTable t4 = new PdfPTable(3);
            t4.setWidthPercentage(100);
            t4.setSpacingBefore(10f);
            t4.setSpacingAfter(10f);

            float[] columnWidths4 = {1f, 1f, 1f};
            t4.setWidths(columnWidths4);

            PdfPCell t4c1 = new PdfPCell(new Paragraph("Loan Amount: \n" + loanAmount.getText() + "\n", content));
            t4c1.setPaddingLeft(10);
            t4c1.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t4c2 = new PdfPCell(new Paragraph("Applicant Name: \n" + nameP.getText() + "\n", content));
            t4c2.setPaddingLeft(10);
            t4c2.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t4c3 = new PdfPCell(new Paragraph("Applicant IC: \n" + ICnoP.getText() + "\n", content));
            t4c3.setPaddingLeft(10);
            t4c3.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t4c4 = new PdfPCell(new Paragraph("Purpose of Loan: \n" + loanPurposeP.getText() + "\n", content));
            t4c4.setPaddingLeft(10);
            t4c4.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t4c5 = new PdfPCell(new Paragraph("Collateral Items: \n" + itemsP.getText() + "\n", content));
            t4c5.setPaddingLeft(10);
            t4c5.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell t4c6 = new PdfPCell(new Paragraph("Collateral Price(in RM): \n" + priceP.getText() + "\n", content));
            t4c6.setPaddingLeft(10);
            t4c6.setHorizontalAlignment(Element.ALIGN_LEFT);

            t4.addCell(t4c1);
            t4.addCell(t4c2);
            t4.addCell(t4c3);
            t4.addCell(t4c4);
            t4.addCell(t4c5);
            t4.addCell(t4c6);
            document.add(t4);

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

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(10f);
            signatureTable.setSpacingAfter(10f);
            float[] columnWidths5 = {1f, 1f};
            signatureTable.setWidths(columnWidths5);

            PdfPCell signature = new PdfPCell(new Paragraph("\n\n\n\n\n\n" + "____________________________________\n\n" + "Name: " + nameP.getText() + "\n\nDate:" + dtf.format(now) + "\n\n" , signBoxContent));
            signature.setPaddingLeft(10);
            signature.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell date = new PdfPCell(new Paragraph("*OFFICE USE ONLY\n\n\n\n\n\n" + "____________________________________\n\n" + "Officer Name:\n\nDate:" + "\n\n", signBoxContent));
            date.setPaddingLeft(10);
            date.setHorizontalAlignment(Element.ALIGN_LEFT);

            signatureTable.addCell(signature);
            signatureTable.addCell(date);
            document.add(signatureTable);


            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
