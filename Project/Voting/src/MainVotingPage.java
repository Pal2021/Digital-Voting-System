import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Enumeration;

public class MainVotingPage {
    private JPanel panel;
    private String fullName;
    private boolean hasVoted = false;
    private String email;
    private JFrame frame;
    private ButtonGroup group; 
    private JButton voteButton; 

    public MainVotingPage(String fullName, String email, JFrame frame) {
        this.fullName = fullName; 
        this.email = email; 
        this.frame = frame;

        panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);

        JLabel welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, gbc);

        if (hasVoted(email)) {
            JLabel votedLabel = new JLabel("You have already voted.");
            votedLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy++;
            panel.add(votedLabel, gbc);
        } else {
            JLabel titleLabel = new JLabel("Choose Your Party to Vote");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy++;
            panel.add(titleLabel, gbc);

            group = new ButtonGroup();
            JRadioButton bjpRadioButton = createRadioButton("BJP");
            JRadioButton aamRadioButton = createRadioButton("Aam Aadmi Party");
            JRadioButton incRadioButton = createRadioButton("INC");
            JRadioButton cpiRadioButton = createRadioButton("CPI");
            JRadioButton cpimRadioButton = createRadioButton("CPIM");
            JRadioButton dmkRadioButton = createRadioButton("DMK");
            JRadioButton aidmkRadioButton = createRadioButton("AIADMK");

            group.add(bjpRadioButton);
            group.add(aamRadioButton);
            group.add(incRadioButton);
            group.add(cpiRadioButton);
            group.add(cpimRadioButton);
            group.add(dmkRadioButton);
            group.add(aidmkRadioButton);

            gbc.gridy++;
            panel.add(bjpRadioButton, gbc);
            gbc.gridy++;
            panel.add(aamRadioButton, gbc);
            gbc.gridy++;
            panel.add(incRadioButton, gbc);
            gbc.gridy++;
            panel.add(cpiRadioButton, gbc);
            gbc.gridy++;
            panel.add(cpimRadioButton, gbc);
            gbc.gridy++;
            panel.add(dmkRadioButton, gbc);
            gbc.gridy++;
            panel.add(aidmkRadioButton, gbc);

            voteButton = new JButton("Vote");
            voteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!hasVoted) {
                        if (group.getSelection() != null) {
                            String selectedParty = group.getSelection().getActionCommand();
                            vote(selectedParty);
                            JOptionPane.showMessageDialog(frame, "You have successfully voted for " + selectedParty + "!");
                            disableVoting();
                            hasVoted = true;
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please select a party to vote.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "You have already voted.");
                    }
                }
            });
            gbc.gridy++;
            gbc.insets = new Insets(20, 10, 10, 10); 
            panel.add(voteButton, gbc);
        }

        JButton returnButton = new JButton("Return to Home Page");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll(); 
                frame.repaint(); 
                new VotingManagementSystem(); 
            }
        });
        gbc.gridy++;
        panel.add(returnButton, gbc);
    }

    private boolean hasVoted(String email) {
     
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean voted = false;
        try {
            String jdbcURL = "jdbc:mysql://localhost:3306/voting_management";
            String username = "root";
            String dbPassword = "159Atg45@";
            connection = DriverManager.getConnection(jdbcURL, username, dbPassword);

            String sql = "SELECT * FROM votes WHERE user_email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                voted = true; 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return voted;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setActionCommand(text);
        radioButton.setFont(new Font("Arial", Font.PLAIN, 16));
        radioButton.setOpaque(false); 
        return radioButton;
    }

    public JPanel getPanel() {
        return panel;
    }

    private void vote(String party) {
        Connection connection = null;
        try {
            String jdbcURL = "jdbc:mysql://localhost:3306/voting_management";
            String username = "root";
            String dbPassword = "159Atg45@";
            connection = DriverManager.getConnection(jdbcURL, username, dbPassword);

            String sql = "INSERT INTO votes (user_email, voted_party) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, party);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void disableVoting() {
       
        Enumeration<AbstractButton> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            button.setEnabled(false); 
        }
        voteButton.setEnabled(false); 
    }
}
