package launcher;

import client.ClientMain;
import server.ServerMain;
import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

public class Launcher extends JFrame {
    
    private static final Color DARK_BG = new Color(28, 28, 32);
    private static final Color CARD_BG = new Color(40, 40, 45);
    private static final Color ACCENT = new Color(0, 150, 255);
    private static final Color TEXT = new Color(240, 240, 240);
    
    private JTextField ipField, nameField;
    
    public Launcher() {
        setupFrame();
        initUI();
    }
    
    private void setupFrame() {
        setTitle("Space Arena Launcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(DARK_BG);
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        //titiolo
        JLabel title = createLabel("SPACE ARENA", 26, Font.BOLD);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);
        
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(DARK_BG);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabs.add("Join", createClientPanel());
        tabs.add("Host", createServerPanel());
        
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        //nome giocatore
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Username:", 14, Font.PLAIN), gbc);
        
        nameField = createTextField("Player" + (int)(Math.random() * 999));
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(nameField, gbc);
        
        // ipServer
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createLabel("Server IP:", 14, Font.PLAIN), gbc);
        
        ipField = createTextField("localhost");
        gbc.gridx = 1;
        panel.add(ipField, gbc);
        
        //connetti
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 8, 8, 8);
        panel.add(createButton("Connetti", e -> launchClient()), gbc);
        
        return panel;
    }
    
    private JPanel createServerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
        
        
        
        // display ip
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            JLabel ipLabel = createLabel("IP: " + ip, 14, Font.BOLD);
            ipLabel.setForeground(ACCENT);
            ipLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            panel.add(ipLabel, BorderLayout.NORTH);
        } catch (Exception e) {
        }
        
        // avvia server
        JButton button = createButton("Avvia Server", e -> launchServer());
        panel.add(button, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createLabel(String text, int size, int style) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", style, size));
        return label;
    }
    
    private JTextField createTextField(String defaultValue) {
        JTextField field = new JTextField(defaultValue, 15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(50, 50, 55));
        field.setForeground(TEXT);
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }
    
    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.addActionListener(action);
        

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 130, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT);
            }
        });
        
        return button;
    }
    
    private void launchClient() {
        String ip = ipField.getText().trim();
        String name = nameField.getText().trim();
        
        if (ip.isEmpty()) ip = "localhost";
        if (name.isEmpty()) name = "Player";
        
        String finalIp = ip;
        String finalName = name;
        
        dispose();
        new Thread(() -> ClientMain.main(new String[]{finalIp, finalName})).start();
    }
    
    private void launchServer() {
        dispose();
        new Thread(() -> ServerMain.main(new String[]{})).start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Launcher().setVisible(true));
    }
}