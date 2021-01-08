package sample;

public class Validation {

    public boolean intValidation( String text, String phonenoPText, String noOfDependantsPText, String homeTelNoPText, String accNumText, String loanAmountText ){
        try {
            Integer.parseInt(text);
            Integer.parseInt(phonenoPText);
            Integer.parseInt(noOfDependantsPText);
            Integer.parseInt(homeTelNoPText);
            Integer.parseInt(accNumText);
            Integer.parseInt(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
