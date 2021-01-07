package sample.Account;

import javafx.concurrent.Task;
import sample.ReadFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class DepositAccount extends Account {

    public double[][] interestRate = { { 1.85, 1.9, 2.1, 2.2 },
                                        { 1.9, 1.95, 2.15, 2.25 },
                                        { 2.0, 2.1, 2.2, 2.3 }
    };

    public int depositType;

    public double getInterestRate(int depositMonth, int depositType) { return interestRate[depositMonth][depositType]; }

    public int getDepositType() { return depositType; }

    public void setDepositType(int depositType) { this.depositType = depositType; }

    public void updateBalance() {
        if (getBalance() >= 50000)
            setDepositType(2);
        else if (getBalance() >= 15000 && getBalance() < 50000)
            setDepositType(1);
        else
            setDepositType(0);

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);

        if (month % 3 == 0 && !isBalanceUpdateStatus() && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            switch (month - ReadFile.DataStorage.savingsAccount.getAccountDateOpen().getMonthValue()) {
                case 3: balance *= Math.pow((1 + (getInterestRate(0, getDepositType()) / 400)), (4 * 3 / 12));
                    break;

                case 6: balance *= Math.pow((1 + (getInterestRate(1, getDepositType()) / 400)), (4 * 6 / 12));
                    break;

                case 9: balance *= Math.pow((1 + (getInterestRate(2, getDepositType()) / 400)), (4 * 9 / 12));
                    break;

                case 11: balance *= Math.pow((1 + (getInterestRate(3, getDepositType()) / 400)), (4 * 12 / 12));
                    break;

                default:
                    break;
            }

            balance = (double) Math.round(balance * 100) / 100;
            setBalanceUpdateStatus(true);

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE DEPOSITS SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.depositAccount.getBalance() +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.depositAccount.getAccountNum() + "'");
                new Thread(updateDepositStatus).start();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        if (calendar.get(Calendar.DAY_OF_MONTH) > 27) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE DEPOSITS SET ACCOUNT_UPDATE_STATUS = 'N'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.depositAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }

    public boolean renewal() {
        LocalDate renewalDate = LocalDate.now();

        if (getAccountDateOpen().getYear() + 1 < renewalDate.getYear()) {
            new Thread(sendRenewal).start();
            return false;
        }

        return true;
    }

    Task<Void> sendRenewal = new Task<Void>() {
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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ReadFile.DataStorage.savingsAccount.getEmail(),false));
                message.setSubject("Automated Message Transfer Authentication");
                message.setText("Dear " + ReadFile.DataStorage.savingsAccount.getName() + ",\n\nThis is an automated generated email to remind you of your deposits." +
                        " Your deposits account has been terminated and you can collect the amount in it." +
                        " You can choose for it to remain in the account but you will not receive any additional interest compounded into your account." +
                        "\n\nPlease reach out to us at (+604) 653 4758 for more information.");

                message.setSentDate(new Date());
                Transport.send(message);
            } catch (MessagingException e){ e.printStackTrace(); }

            return null;
        }
    };

    Task<Void> updateDepositStatus = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE DEPOSITS SET ACCOUNT_UPDATE_STATUS = 'Y'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.depositAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };
}
