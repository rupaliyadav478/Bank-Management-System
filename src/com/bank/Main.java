package com.bank;
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
