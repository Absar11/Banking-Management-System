package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email){
        if(!account_exist(email)){
            String query = "INSERT into Accounts(account_number, full_name, email, balance, security_pin)  VALUES (?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.print("Enter Full Name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter Initial Amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter your Security Pin: ");
            String security_pin = scanner.nextLine();

            try{
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);

                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0){
                    return account_number;
                }else {
                    throw new RuntimeException("Account creation failed!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }

        }
        throw new RuntimeException("Account already exist");
    }

    public long getAccount_number(String email){
        String query = "Select account_number from Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number does not exist");
    }

    private long generateAccountNumber(){
        String query = "SELECT account_number from Accounts ORDER BY account_number DESC limit 1";
        try{
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number+1;
            }else{
                return 10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean account_exist(String email){
        String query = "SELECT account_number from accounts where email=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
