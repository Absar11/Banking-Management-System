package BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    Connection connection;
    Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void credit_money(long account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount to credit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            String query = "SELECT * from accounts WHERE account_number = ?  AND security_pin = ?";
            if(account_number != 0){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);

                    int rowsAffected = preparedStatement1.executeUpdate();

                    if(rowsAffected > 0){
                        System.out.println("Rs. " +amount+ " Credited to your account successfully!!");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    }else{
                        System.out.println("Transaction Failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.println("Invalid security pin");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debit_money(long account_number)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Your Security Pin: ");
        String security_pin = scanner.nextLine();

        try{
            connection.setAutoCommit(false);

            if(account_number != 0){
                String query = "SELECT * from Accounts WHERE account_number = ? AND security_pin = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    double current_balance = resultSet.getDouble("balance");

                    if(amount <= current_balance){
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";

                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);

                        int rowsAffected = preparedStatement1.executeUpdate();

                        if(rowsAffected > 0){
                            System.out.println("Amount "+amount+ " Credit successfully from your account");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }

                    }else{
                        System.out.println("Insufficient Balance");
                    }
                }else {
                    System.out.println("Invalid Pin");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void transfer_money(long sender_account_number) throws SQLException{
        scanner.nextLine();
        System.out.print("Enter reciever account number: ");
        long reciever_account_number = scanner.nextLong();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter security pin: ");
        String security_pin = scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(sender_account_number != 0 && reciever_account_number != 0){
                String query = "SELECT * from Accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    double current_amount = resultSet.getDouble("balance");
                    if(amount <= current_amount){
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";

                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, reciever_account_number);

                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();

                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else {
                        System.out.println("Insufficient Balance");
                    }
                }else{
                    System.out.println("Invalid security pin");
                }
            }else {
                System.out.println("Invalid Account Number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void get_balance(long account_number){
        scanner.nextLine();
        System.out.print("Enter your security pin: ");
        String security_pin = scanner.nextLine();

        try{
            String query = "SELECT balance from accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Your current balance is: "+balance);
            }else{
                System.out.println("Invalid Pin");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
