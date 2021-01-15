package sample.Card;

public class Card {

    public String cardID;
    public String name;
    public String expiryDate;
    public String cvv;

    public double fixedMonthlyLimit;

    public String getCardID() { return cardID; }

    public void setCardID(String cardID) { this.cardID = cardID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getExpiryDate() { return expiryDate; }

    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }

    public void setCvv(String cvv) { this.cvv = cvv; }

    public double getFixedMonthlyLimit() {
        return fixedMonthlyLimit;
    }

    public void setFixedMonthlyLimit(double fixedMonthlyLimit) {
        this.fixedMonthlyLimit = fixedMonthlyLimit;
    }

    public boolean isValid(String amount) {
        if (amount == null)
            return false;

        try {
            double limitAmount = Double.parseDouble(amount);

            if (limitAmount < 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
