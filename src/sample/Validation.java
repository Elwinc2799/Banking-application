package sample;

public class Validation {

    //int validation in personal loan
    public boolean intValidation(String noOfDependantsPText, String incomeText, String loanAmountText ){
        try {
            Integer.parseInt(noOfDependantsPText);
            Double.parseDouble(incomeText);
            Double.parseDouble(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    //int validation in business loan
    public boolean intValidation2(String priceText,  String loanAmountText ){
        try {
            Double.parseDouble(priceText);
            Double.parseDouble(loanAmountText);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
