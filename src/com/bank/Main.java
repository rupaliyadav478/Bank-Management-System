package com.bank;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.List;
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

                    System.out.println("\nDo you want to see your password?");
                    System.out.println("1. Show Password");
                    System.out.println("2. Hide Password");
                    System.out.print("Enter choice: ");

                    int passwordChoice = sc.nextInt();
                    sc.nextLine();

                    if (passwordChoice == 1) {
                        System.out.println("Your Password: " + regPassword);
                    } else {
                        System.out.println("Your Password: ********");
                    }

                    String email;

                    while (true) {
                        System.out.print("Enter Email: ");
                        String inputEmail = sc.nextLine().trim().toLowerCase();

                        // Remove existing Gmail domain
                        if (inputEmail.endsWith("@gmail.com")) {
                            inputEmail = inputEmail.substring(
                                    0, inputEmail.length() - 10);
                        } else if (inputEmail.endsWith("gmail.com")) {
                            inputEmail = inputEmail.substring(
                                    0, inputEmail.length() - 9);
                        }

                        // Validate email username and compulsory number
                        if (inputEmail.matches("[a-z0-9._%+-]+(@[a-z0-9._%+-]+)?")
                                && inputEmail.matches(".*\\d.*")) {

                            if (inputEmail.contains("@")) {
                                email = inputEmail + "gmail.com";
                            } else {
                                email = inputEmail + "@gmail.com";
                            }

                            break;

                        } else {
                            System.out.println(
                                    "Invalid Email! Number is compulsory. Try again.");
                        }
                    }

                    System.out.println("Your Email: " + email);

                    String phone;

                    while (true) {
                        System.out.print("Enter Phone: ");
                        phone = sc.nextLine();

                        if (phone.matches("\\d{10}")) {
                            break;
                        } else {
                            System.out.println("Invalid Phone Number! Enter exactly 10 digits.");
                        }
                    }

                    System.out.println("\nSelect Account Type:");
                    System.out.println("1. Savings Account");
                    System.out.println("2. Current Account");
                    System.out.print("Enter your choice: ");

                    int accountChoice = sc.nextInt();
                    sc.nextLine();

                    String accountType;

                    if (accountChoice == 1) {
                        accountType = "SAVINGS";
                    } else if (accountChoice == 2) {
                        accountType = "CURRENT";
                    } else {
                        System.out.println("Invalid account type!");
                        return;
                    }

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
                                userId, accountNumber, accountType, "NOT_SET", "NOT_SET"
                        );

                        if (accountCreated) {
                            System.out.println("Bank Account Created Successfully!");
                            System.out.println("Account Number: " + accountNumber);
                            System.out.println("Account Type: " + accountType);
                        } else {
                            System.out.println("Bank Account Creation Failed!");
                        }

                    } else {
                        System.out.println("\nRegistration Failed!");
                    }
                    break;
                // USER LOGIN
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

                        AccountDAO accountDAO = new AccountDAO();
                        TransactionDAO transactionDAO = new TransactionDAO();

                        // USER DASHBOARD
                        while (true) {

                            System.out.println("\n===== USER DASHBOARD =====");
                            System.out.println("1. Deposit");
                            System.out.println("2. Withdrawal");
                            System.out.println("3. Account Balance");
                            System.out.println("4. Transaction History");
                            System.out.println("5. Logout");
                            System.out.println("6. Money Transfer");
                            System.out.println("7. Account Details");
                            System.out.println("8. Change Password");
                            System.out.println("9. Mini Statement");
                            System.out.print("Enter your choice: ");

                            int dashboardChoice = sc.nextInt();
                            sc.nextLine();

                            switch (dashboardChoice) {

                                case 1:
                                    System.out.print("Enter deposit amount: ");

                                    try {
                                        BigDecimal amount = new BigDecimal(sc.nextLine());

                                        boolean deposited =
                                                accountDAO.deposit(user.getUserId(), amount);

                                        if (deposited) {
                                            System.out.println("Amount deposited successfully!");
                                        } else {
                                            System.out.println("Deposit failed!");
                                        }

                                    } catch (NumberFormatException e) {
                                        System.out.println("Please enter a valid amount!");
                                    }
                                    break;

                                case 2:
                                    System.out.print("Enter withdrawal amount: ");
                                    BigDecimal withdrawalAmount = new BigDecimal(sc.next());

                                    accountDAO.withdraw(user.getUserId(), withdrawalAmount);
                                    break;
                                case 3:
                                        BigDecimal balance =
                                                accountDAO.getAccountBalance(user.getUserId());

                                        if (balance != null) {
                                            System.out.println("\n===== ACCOUNT BALANCE =====");
                                            System.out.println("Available Balance: Rs. " + balance);
                                        } else {
                                            System.out.println("Account not found!");
                                        }

                                        break;


                                case 4:

                                    int accountId = accountDAO.findAccountIdByUserId(user.getUserId());

                                    if (accountId == -1) {
                                        System.out.println("Account not found!");
                                        break;
                                    }

                                    List<Transaction> transactions =
                                            transactionDAO.getTransactionsByAccountId(accountId);

                                    System.out.println("\n===== TRANSACTION HISTORY =====");

                                    if (transactions.isEmpty()) {
                                        System.out.println("No transactions found!");
                                    } else {

                                        for (Transaction transaction : transactions) {

                                            System.out.println("-----------------------------");
                                            System.out.println("Transaction ID: " + transaction.getId());
                                            System.out.println("Type: " + transaction.getTransactionType());
                                            System.out.println("Amount: Rs. " + transaction.getAmount());
                                            System.out.println("Balance After: Rs. " +
                                                    transaction.getBalanceAfter());
                                            System.out.println("Description: " +
                                                    transaction.getDescription());
                                            System.out.println("Date: " + transaction.getCreatedAt());
                                        }

                                        System.out.println("-----------------------------");
                                    }

                                    break;

                                case 5:
                                    System.out.println("Logout Successful!");
                                    break;


                                case 6:

                                    sc.nextLine(); // Consume leftover newline

                                    System.out.print("Enter Receiver Account Number: ");
                                    String receiverAccountNumber = sc.nextLine().trim();

                                    System.out.print("Enter Transfer Amount: ");
                                    BigDecimal transferAmount = new BigDecimal(sc.nextLine());

                                    accountDAO.transferMoney(
                                            user.getUserId(),
                                            receiverAccountNumber,
                                            transferAmount
                                    );

                                    break;
                                case 7:
                                    accountDAO.showAccountDetails(user.getUserId());
                                    break;


                                case 8: {

                                    System.out.print("Enter Current Password: ");
                                    String currentPassword = sc.nextLine();

                                    System.out.print("Enter New Password: ");
                                    String newPassword = sc.nextLine();

                                    System.out.print("Confirm New Password: ");
                                    String confirmPassword = sc.nextLine();

                                    if (newPassword.length() < 6) {
                                        System.out.println("Password must contain at least 6 characters!");
                                    } else if (!newPassword.equals(confirmPassword)) {
                                        System.out.println("New password and confirmation do not match!");
                                    } else if (newPassword.equals(currentPassword)) {
                                        System.out.println("New password must be different!");
                                    } else {

                                        boolean updated = userDAO.changePassword(
                                                user.getUserId(),
                                                currentPassword,
                                                newPassword
                                        );

                                        if (updated) {
                                            System.out.println("Password changed successfully!");
                                        } else {
                                            System.out.println("Incorrect current password!");
                                        }
                                    }

                                    break;
                                }


                                case 9:

                                    int miniStatementAccountId =
                                            accountDAO.findAccountIdByUserId(user.getUserId());

                                    if (miniStatementAccountId == -1) {
                                        System.out.println("Account not found!");
                                        break;
                                    }

                                    List<Transaction> minitransactions =
                                            transactionDAO.getMiniStatementByAccountId(miniStatementAccountId);
                                    System.out.println("\n===== MINI STATEMENT =====");

                                    if (minitransactions.isEmpty()) {
                                        System.out.println("No transactions found!");
                                    } else {

                                        for (Transaction transaction : minitransactions) {

                                            System.out.println("-----------------------------");
                                            System.out.println("Transaction ID: " + transaction.getId());
                                            System.out.println("Type: " + transaction.getTransactionType());
                                            System.out.println("Amount: Rs. " + transaction.getAmount());
                                            System.out.println("Balance After: Rs. " +
                                                    transaction.getBalanceAfter());
                                            System.out.println("Description: " +
                                                    transaction.getDescription());
                                            System.out.println("Date: " + transaction.getCreatedAt());
                                        }

                                        System.out.println("-----------------------------");
                                    }

                                    break;

                                default:
                                    System.out.println("Invalid choice! Try again.");
                            }


                            if (dashboardChoice == 5) {
                                break;
                            }

                        }

                    } else {
                        System.out.println("\nInvalid Username or Password!");
                    }

                    break;

                case 3:
                    System.out.println("\nThank you for using Bank Management System!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }


}

    }}
