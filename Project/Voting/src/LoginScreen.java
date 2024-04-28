import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginScreen {
    private JPanel panel;
    private String email; 

    public LoginScreen() {
        panel = new JPanel(); 
        panel.setLayout(new GridBagLayout()); 
        panel.setBackground(new Color(240, 240, 240)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        panel.add(titleLabel, gbc);

        gbc.gridy++;

        JLabel emailLabel = new JLabel("Email:");
        panel.add(emailLabel, gbc);

        gbc.gridy++;

        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridy++; 

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel, gbc);

        gbc.gridy++;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy++; 

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 40)); 
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (isValidLogin(email, password)) {
                    String fullName = getFullName(email);
                    openMainPage(fullName, email); 
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

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_management", "root", "159Atg45@");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; 
        }
    }

    private String getFullName(String email) {
       
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
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel); 
        frame.getContentPane().removeAll();

        MainVotingPage mainVotingPage = new MainVotingPage(fullName, email, frame); 
        frame.add(mainVotingPage.getPanel()); 

        frame.revalidate();
        frame.repaint();
    }
}
