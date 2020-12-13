package sample;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile {

    public static void importData() throws FileNotFoundException {
        File file = new File("D:\\OneDrive - Universiti Sains Malaysia\\Year 2 Sem 1\\CAT 201\\Final Project\\New Compressed (zipped) Folder\\Bank File\\Evergreen\\src\\sample\\UserInfo.txt");
        String tempUsername = null, tempPassword = null;

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                tempUsername = scanner.nextLine();
                tempPassword = scanner.nextLine();
            }

            ReadFile.DataStorage.setUsername(tempUsername);
            ReadFile.DataStorage.setPassword(tempPassword);

        } catch (FileNotFoundException e) { e.printStackTrace(); }

        File accountFile = new File("D:\\OneDrive - Universiti Sains Malaysia\\Year 2 Sem 1\\CAT 201\\Final Project\\New Compressed (zipped) Folder\\Bank File\\Evergreen\\src\\sample\\AccountInfo.txt");
        String tempUserID = null, tempName = null, tempUserEmail = null, tempAccountID = null, tempAccountType = null;
        int tempMonthOpen = 0;
        double tempInitialBalance = 0, tempBalance = 0;

        try (Scanner scanner = new Scanner(accountFile)) {
            while (scanner.hasNextLine()) {
                tempUserID = scanner.nextLine();
                tempName = scanner.nextLine();
                tempUserEmail = scanner.nextLine();

                tempAccountID = scanner.nextLine();
                tempAccountType = scanner.nextLine();
                tempMonthOpen = Integer.parseInt(scanner.nextLine());
                tempInitialBalance = Double.parseDouble(scanner.nextLine());
                tempBalance = Double.parseDouble(scanner.nextLine());
            }

            ReadFile.DataStorage.setUserID(tempUserID);
            ReadFile.DataStorage.setName(tempName);
            ReadFile.DataStorage.setUserEmail(tempUserEmail);
            ReadFile.DataStorage.setAccountID(tempAccountID);
            ReadFile.DataStorage.setAccountType(tempAccountType);
            ReadFile.DataStorage.setMonthOpen(tempMonthOpen);
            ReadFile.DataStorage.setInitialBalance(tempInitialBalance);
            ReadFile.DataStorage.setBalance(tempBalance);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        File cardFile = new File("D:\\OneDrive - Universiti Sains Malaysia\\Year 2 Sem 1\\CAT 201\\Final Project\\New Compressed (zipped) Folder\\Bank File\\Evergreen\\src\\sample\\CardInfo.txt");
        String tempCardID, tempCardExpiryDate;
        int tempCardMonthlyLimit;
        double tempCardOutstandingBalance;

        try (Scanner scanner = new Scanner(cardFile)) {
            tempCardID = scanner.nextLine();
            tempCardExpiryDate = scanner.nextLine();
            tempCardMonthlyLimit = Integer.parseInt(scanner.nextLine());
            tempCardOutstandingBalance = Double.parseDouble(scanner.nextLine());

            ReadFile.DataStorage.setCreditCardID(tempCardID);
            ReadFile.DataStorage.setCardExpiryDate(tempCardExpiryDate);
            ReadFile.DataStorage.setCardMonthlyLimit(tempCardMonthlyLimit);
            ReadFile.DataStorage.setCardOutstandingBalance(tempCardOutstandingBalance);
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public static class DataStorage {

        public static String username;
        public static String password;
        public static String userID;
        public static String name;
        public static String userEmail;
        public static String accountID;
        public static String accountType;
        public static String creditCardID;
        public static String cardExpiryDate;

        public static int cardMonthlyLimit;
        public static int monthOpen;

        public static double initialBalance;
        public static double balance;
        public static double cardOutstandingBalance;

        public static String getUsername() {
            return username;
        }

        public static void setUsername(String username) {
            DataStorage.username = username;
        }

        public static String getPassword() {
            return password;
        }

        public static void setPassword(String password) {
            DataStorage.password = password;
        }

        public static String getUserID() {
            return userID;
        }

        public static void setUserID(String userID) {
            DataStorage.userID = userID;
        }

        public static String getName() {
            return name;
        }

        public static void setName(String name) {
            DataStorage.name = name;
        }

        public static String getUserEmail() {
            return userEmail;
        }

        public static void setUserEmail(String userEmail) {
            DataStorage.userEmail = userEmail;
        }

        public static String getAccountID() {
            return accountID;
        }

        public static void setAccountID(String accountID) {
            DataStorage.accountID = accountID;
        }

        public static String getAccountType() {
            return accountType;
        }

        public static void setAccountType(String accountType) {
            DataStorage.accountType = accountType;
        }

        public static int getMonthOpen() {
            return monthOpen;
        }

        public static void setMonthOpen(int monthOpen) {
            DataStorage.monthOpen = monthOpen;
        }

        public static double getInitialBalance() {
            return initialBalance;
        }

        public static void setInitialBalance(double initialBalance) {
            DataStorage.initialBalance = initialBalance;
        }

        public static double getBalance() {
            return balance;
        }

        public static void setBalance(double balance) {
            DataStorage.balance = balance;
        }

        public static String getCreditCardID() {
            return creditCardID;
        }

        public static void setCreditCardID(String creditCardID) {
            DataStorage.creditCardID = creditCardID;
        }

        public static String getCardExpiryDate() {
            return cardExpiryDate;
        }

        public static void setCardExpiryDate(String cardExpiryDate) {
            DataStorage.cardExpiryDate = cardExpiryDate;
        }

        public static int getCardMonthlyLimit() {
            return cardMonthlyLimit;
        }

        public static void setCardMonthlyLimit(int cardMonthlyLimit) {
            DataStorage.cardMonthlyLimit = cardMonthlyLimit;
        }

        public static double getCardOutstandingBalance() {
            return cardOutstandingBalance;
        }

        public static void setCardOutstandingBalance(double cardOutstandingBalance) {
            DataStorage.cardOutstandingBalance = cardOutstandingBalance;
        }
    }
}
