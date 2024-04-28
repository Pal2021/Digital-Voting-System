import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Admin {
    private JFrame frame;
    private Connection connection;
    private String winningParty;

    public Admin(JFrame frame, Connection connection) {
        this.frame = frame;
        this.connection = connection;
    }

    public void openAdminView() {
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BorderLayout());
        adminPanel.setBackground(new Color(240, 240, 240));

        JLabel adminLabel = new JLabel("Welcome, Admin!");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 24));
        adminLabel.setHorizontalAlignment(SwingConstants.CENTER);
        adminPanel.add(adminLabel, BorderLayout.NORTH);

        JPanel votePanel = new JPanel();
        votePanel.setLayout(new BoxLayout(votePanel, BoxLayout.Y_AXIS));
        votePanel.setBackground(new Color(240, 240, 240));
        Map<String, Integer> voteCounts = getVoteCounts();
        int maxVotes = 0;
        for (int votes : voteCounts.values()) {
            if (votes > maxVotes) {
                maxVotes = votes;
            }
        }
        boolean tie = false;
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            JPanel partyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); 
            partyPanel.setBackground(new Color(240, 240, 240));

            JLabel voteLabel = new JLabel(entry.getKey() + ": " + entry.getValue());
            voteLabel.setFont(new Font("Arial", Font.PLAIN, 16)); 
            if (entry.getValue() == maxVotes) {
                voteLabel.setForeground(Color.RED);
                if (tie) {
                    winningParty += ", " + entry.getKey();
                } else {
                    winningParty = entry.getKey();
                    tie = true;
                }
            }
            partyPanel.add(voteLabel);
            votePanel.add(partyPanel);
        }
        adminPanel.add(new JScrollPane(votePanel), BorderLayout.CENTER);

        JLabel winningLabel;
        if (!tie) {
            winningLabel = new JLabel("Winning Party: " + determineWinningParty(voteCounts));
        } else {
            winningLabel = new JLabel("Parties running for the final seat: " + winningParty);
        }
        winningLabel.setFont(new Font("Arial", Font.BOLD, 16));
        winningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        adminPanel.add(winningLabel, BorderLayout.SOUTH);


        JButton returnButton = new JButton("Return to Home Page");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                frame.repaint();
                new VotingManagementSystem();
            }
        });
        adminPanel.add(returnButton, BorderLayout.WEST);

        frame.getContentPane().removeAll();
        frame.add(adminPanel);
        frame.revalidate();
        frame.repaint();
    }

    private Map<String, Integer> getVoteCounts() {
        Map<String, Integer> voteCounts = new HashMap<>();
        try {
         
            voteCounts.put("BJP", 0);
            voteCounts.put("Aam Aadmi Party", 0);
            voteCounts.put("INC", 0);
            voteCounts.put("CPI", 0);
            voteCounts.put("CPIM", 0);
            voteCounts.put("DMK", 0);
            voteCounts.put("AIADMK", 0);

            String sql = "SELECT voted_party, COUNT(*) AS vote_count FROM votes GROUP BY voted_party ORDER BY vote_count DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String party = resultSet.getString("voted_party");
                int count = resultSet.getInt("vote_count");
                voteCounts.put(party, count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return voteCounts;
    }


    private String determineWinningParty(Map<String, Integer> voteCounts) {
        String winningParty = "";
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winningParty = entry.getKey();
            }
        }
        return winningParty;
    }
}
