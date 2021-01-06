package sample;

import javafx.scene.control.Alert;

public class Validation {

    public void intValidation( String text, String phonenoPText, String noOfDependantsPText, String homeTelNoPText, String accNumText, String loanAmountText ){
        try {
            Integer.parseInt(text);
            Integer.parseInt(phonenoPText);
            Integer.parseInt(noOfDependantsPText);
            Integer.parseInt(homeTelNoPText);
            Integer.parseInt(accNumText);
            Integer.parseInt(loanAmountText);
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Integer input wrongly");
            alert.showAndWait();
            return;
        }
    }
}
