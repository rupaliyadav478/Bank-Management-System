package com.bank;
import java.math.BigDecimal;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        while (true) {

            System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // USER REGISTRATION
                case 1:
                    System.out.println("\n===== USER REGISTRATION =====");

                    System.out.print("Enter Username: ");
                    String regUsername = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String regPassword = sc.nextLine();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter Phone: ");
                    String phone = sc.nextLine();

                    User newUser = new User(
                            0,
                            regUsername,
                            regPassword,
                            null,
                            email,
                            phone
                    );

                    boolean registered = userDAO.registerUser(newUser);

                    if (registered) {

                        System.out.println("\nRegistration Successful!");

                        int userId = userDAO.getUserIdByUsername(regUsername);

                        AccountDAO accountDAO = new AccountDAO();

                        String accountNumber = "BANK" + System.currentTimeMillis();

                        boolean accountCreated = accountDAO.createAccount(
                                userId,
                                accountNumber,
                                "SAVINGS",
                                "NOT_SET",
                                "NOT_SET"
                        );

                        if (accountCreated) {
                            System.out.println("Bank Account Created Successfully!");
                            System.out.println("Account Number: " + accountNumber);
                            System.out.println("Account Type: SAVINGS");
                        } else {
                            System.out.println("Bank Account Creation Failed!");
                        }

                    } else {
                        System.out.println("\nRegistration Failed!");
                    }
                   break;
                // USER LOGIN
                case 2:
                    System.out.println("\n===== USER LOGIN =====");

                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    User user = userDAO.loginUser(username, password);

                    if (user != null) {
                        System.out.println("\nLogin Successful!");
                        System.out.println("Welcome, " + user.getUsername());
                        System.out.print("Enter deposit amount: ");
                        BigDecimal amount = new BigDecimal(sc.nextLine());

                        AccountDAO accountDAO = new AccountDAO();

                        boolean deposited = accountDAO.deposit(user.getUserId(), amount);

                        if (deposited) {
                            System.out.println("Amount deposited successfully!");
                        } else {
                            System.out.println("Deposit failed!");
                        }
                    } else {
                        System.out.println("\nInvalid Username or Password!");
                    }
                    break;

                // EXIT
                case 3:
                    System.out.println("\nThank you for using our Bank System!");
                    sc.close();
                    return;

                default:
                    System.out.println("\nInvalid choice! Try again.");
            }
        }
}}
