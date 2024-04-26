import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginScreen {
    private JPanel panel; // Define panel as a class-level variable
    private String email; // Store the email

    public LoginScreen() {
        panel = new JPanel(); // Initialize the panel
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for flexible layout
        panel.setBackground(new Color(240, 240, 240)); // Set background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font and size
        panel.add(titleLabel, gbc);

        gbc.gridy++; // Move to the next row

        JLabel emailLabel = new JLabel("Email:");
        panel.add(emailLabel, gbc);

        gbc.gridy++; // Move to the next row

        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridy++; // Move to the next row

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel, gbc);

        gbc.gridy++; // Move to the next row

        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy++; // Move to the next row

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 40)); // Set button size
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check login credentials and open main page
                email = emailField.getText(); // Store the email
                String password = new String(passwordField.getPassword());
                if (isValidLogin(email, password)) {
                    String fullName = getFullName(email);
                    openMainPage(fullName, email); // Pass the email to MainVotingPage
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid email or password. Please try again.");
                }
            }
        });
        panel.add(loginButton, gbc);
    }


    public JPanel getLoginPanel() {
        return panel;
    }

    private boolean isValidLogin(String email, String password) {
        // JDBC connection and query database for login validation
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_management", "root", "159Atg45@");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Return false in case of any SQL exception
        }
    }

    private String getFullName(String email) {
        // Fetch full name of the user from the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_management", "root", "159Atg45@");
             PreparedStatement statement = connection.prepareStatement("SELECT full_name FROM users WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("full_name");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private void openMainPage(String fullName, String email) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel); // Get the JFrame of the LoginScreen
        frame.getContentPane().removeAll(); // Remove all components from the frame

        MainVotingPage mainVotingPage = new MainVotingPage(fullName, email, frame); // Pass the email and frame
        frame.add(mainVotingPage.getPanel()); // Add the MainVotingPage panel to the frame

        frame.revalidate();
        frame.repaint();
    }
}
