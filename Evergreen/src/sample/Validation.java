package sample;

public class Validation {

    //int validation in personal loan
    public boolean intValidation(String phoneNoPText, String noOfDependantsPText, String homeTelNoPText, String incomeText, String loanAmountText ){
        try {
            Integer.parseInt(phoneNoPText);
            Integer.parseInt(noOfDependantsPText);
            Integer.parseInt(homeTelNoPText);
            Integer.parseInt(incomeText);
            Integer.parseInt(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    //int validation in business loan
    public boolean intValidation2( String phoneText, String priceText,  String loanAmountText ){
        try {
            Integer.parseInt(phoneText);
            Integer.parseInt(priceText);
            Integer.parseInt(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
