package sample;

public class Validation {

    public boolean intValidation( String text, String phoneNoPText, String noOfDependantsPText, String homeTelNoPText, String accNumText, String loanAmountText ){
        try {
            Integer.parseInt(text);
            Integer.parseInt(phoneNoPText);
            Integer.parseInt(noOfDependantsPText);
            Integer.parseInt(homeTelNoPText);
            Integer.parseInt(accNumText);
            Integer.parseInt(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public boolean intValidation2( String icText, String phoneText, String priceText, String accNumText, String loanAmountText ){
        try {
            Integer.parseInt(icText);
            Integer.parseInt(phoneText);
            Integer.parseInt(priceText);
            Integer.parseInt(accNumText);
            Integer.parseInt(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
