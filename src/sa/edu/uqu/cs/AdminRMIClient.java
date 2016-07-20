package sa.edu.uqu.cs;

import java.awt.Button;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Muhammad Ashari Fatani
 */
public class AdminRMIClient extends JFrame {

    private final static Font myFont = new Font("Tahoma", Font.PLAIN, 15);
    private IDataCollector collector;
    private DataWrapper dataWrapper;
    private final JPanel contentPanel;
    private JTextField jtfUserName;
    private JPasswordField jpfPassword;

    AdminRMIClient() throws RemoteException, NotBoundException {
        super("AMAN SYSTEM");
        this.setupRMIConnection();
        this.contentPanel = new JPanel();
        this.setSize(300, 150);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.iniLayout();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupRMIConnection() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", Constants.RMI_PORT);
        this.collector = (IDataCollector) registry.lookup(Constants.RMI_ID);

    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        new AdminRMIClient();
    }

    private void iniLayout() {
        this.getContentPane().add(this.contentPanel);
        this.contentPanel.add(this.createCustomerPanel());
    }

    protected JPanel createCustomerPanel() {
        GridLayout layout = new GridLayout(3, 1);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(layout);

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(p1);
        mainPanel.add(p2);
        mainPanel.add(p3);

        JLabel lblUserName = new JLabel("USER NAME");
        lblUserName.setFont(myFont);
        this.jtfUserName = new JTextField(15);
        p1.add(lblUserName);
        p1.add(this.jtfUserName);

        JLabel lblPassword = new JLabel("PASSWORD");
        lblPassword.setFont(myFont);
        this.jpfPassword = new JPasswordField(15);
        p2.add(lblPassword);
        p2.add(this.jpfPassword);

        JButton btnLogin = new JButton("LOGIN");
        JButton btnNewAdmin = new JButton("NEW ADMIN");
        btnLogin.addActionListener((ActionEvent e) -> {
            try {
                isAuth();
            } catch (RemoteException ex) {
                Logger.getLogger(AdminRMIClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnLogin.setFont(myFont);
        btnNewAdmin.addActionListener((ActionEvent e) -> {
            try {
                signUp();
            } catch (RemoteException ex) {
                Logger.getLogger(AdminRMIClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnNewAdmin.setFont(myFont);
        p3.add(btnLogin);
        p3.add(btnNewAdmin);

        return mainPanel;
    }

    private void isAuth() throws RemoteException {

        if (this.jtfUserName.getText().equals("") || this.jpfPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "invaled Usen Or Pssword", "Auth Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            boolean result = this.collector.isAuth(this.jtfUserName.getText(),
                    String.valueOf(this.jpfPassword.getPassword()));

            if (result) {
                this.setVisible(false);
                new ControlFrame();
            }
        }
    }

    private void signUp() throws RemoteException {
        String username = JOptionPane.showInputDialog("Please input username: ");
        while (username.equals("")) {
            showAlet("please fill the filed");
            username = JOptionPane.showInputDialog("Please input username: ");
        }
        String password = JOptionPane.showInputDialog("Please input password: ");
        while (password.equals("")) {
            showAlet("please fill the filed");
            password = JOptionPane.showInputDialog("Please input password: ");
        }
        boolean result = this.collector.newAdmin(username, password);
        System.out.println(result);
    }

    private void showAlet(String msg) {
        JOptionPane.showMessageDialog(this, msg, "AMAN SYSTEM",
                JOptionPane.INFORMATION_MESSAGE);
    }

    class ControlFrame extends JFrame {

        private JTextField jtfPcIp;

        private final JPanel contentPanel;

        ControlFrame() {
            super("CONTROL PANEL");
            this.setSize(300, 200);
            this.setLocationRelativeTo(null);
            this.setResizable(false);
            this.contentPanel = new JPanel();
            this.iniLayout();
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        }

        private void iniLayout() {
            this.getContentPane().add(this.contentPanel);
            this.contentPanel.add(this.createCustomerPanel());
        }

        protected JPanel createCustomerPanel() {
            GridLayout layout = new GridLayout(4, 1);
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(layout);

            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            mainPanel.add(p1);
            mainPanel.add(p2);
            mainPanel.add(p3);
            mainPanel.add(p4);

            JLabel lblTxt = new JLabel("PLEASE ENTER PC IP TO INVOKE");
            lblTxt.setFont(myFont);
            this.jtfPcIp = new JTextField(15);
            p1.add(lblTxt);
            p2.add(this.jtfPcIp);

            JButton btnCollectData = new JButton("INVOKE PC TO COLLECT DATA");
            btnCollectData.setFont(myFont);
            JButton btnShowData = new JButton("INVOKE PC TO SHOW DATA");
            btnShowData.setFont(myFont);
            btnCollectData.addActionListener((ActionEvent e) -> {
                try {
                    invokePcCollectData();
                } catch (RemoteException ex) {
                    Logger.getLogger(AdminRMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            btnShowData.addActionListener((ActionEvent e) -> {
                if (!jtfPcIp.getText().equals("") && jtfPcIp.getText().contains(".")) {
                    try {
                        new ShowData(jtfPcIp.getText());
                    } catch (RemoteException ex) {
                        Logger.getLogger(AdminRMIClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    showAlet("please fill the filed");
                }
            });
            p3.add(btnCollectData);
            p4.add(btnShowData);

            return mainPanel;
        }

        private void invokePcCollectData() throws RemoteException {
            String ip = this.jtfPcIp.getText();
            if (!ip.equals("") && ip.contains(".")) {
                String result = collector.fetchData(ip);
                System.out.println(result);
            } else {
                showAlet("please fill the filed");
            }
        }
    }

    class ShowData extends JFrame {

        private final JPanel contentPanel;
        private final DataWrapper dataWrapper;

        ShowData(String ip) throws RemoteException {
            super("INFORMATION PANEL");
            this.dataWrapper = collector.loadData(ip);
            this.setSize(500, 500);
            this.setLocationRelativeTo(null);
            this.setResizable(false);
            this.contentPanel = new JPanel();
            this.iniLayout();
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        }

        private void iniLayout() {
            this.getContentPane().add(this.contentPanel);
            this.contentPanel.add(this.createCustomerPanel());
        }

        protected JPanel createCustomerPanel() {
            GridLayout layout = new GridLayout(9, 1);
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(layout);

            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel p9 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            mainPanel.add(p1);
            mainPanel.add(p2);
            mainPanel.add(p3);
            mainPanel.add(p4);
            mainPanel.add(p5);
            mainPanel.add(p6);
            mainPanel.add(p7);
            mainPanel.add(p8);
            mainPanel.add(p9);

            JLabel lblRes = new JLabel("Resources");
            lblRes.setFont(myFont);
            p1.add(lblRes);

            JLabel lblHostName = new JLabel("HOST NAME [" + this.dataWrapper.getHostName() + "]");
            lblHostName.setFont(myFont);
            p2.add(lblHostName);

            String cpu = this.dataWrapper.getCpuUsage() == null ? "0" : this.dataWrapper.getCpuUsage();
            p3.add(new JLabel("CPU [" + cpu + " %]"));

            String mem = this.dataWrapper.getUsageMemory() == null ? "0" : this.dataWrapper.getUsageMemory();
            p4.add(new JLabel("Memory [" + mem + " %]"));

            String storage = this.dataWrapper.getUsageStorage() == null ? "0" : this.dataWrapper.getUsageStorage();
            p5.add(new JLabel("Storage [" + storage + " %]"));

            String processorId = this.dataWrapper.getProcessorId() == null ? "0" : this.dataWrapper.getProcessorId();
            p6.add(new JLabel("Processor id [" + processorId + "]"));

            String processorArch = this.dataWrapper.getProcessorArch() == null ? "0" : this.dataWrapper.getProcessorArch();
            p7.add(new JLabel("Processor architecture [" + processorArch + "]"));

            String processorNum = this.dataWrapper.getProcessorNum() == null ? "0" : this.dataWrapper.getProcessorNum();
            p8.add(new JLabel("Processor Number [" + processorNum + "]"));

            String netInfo = "";
            for (int i = 0; i < this.dataWrapper.getNetwork().size(); i++) {
                netInfo += "\n[et proto - " + this.dataWrapper.getNetwork().get(i).get("net_proto")
                        + "] | [net localadd - " + this.dataWrapper.getNetwork().get(i).get("net_localadd")
                        + "] | [net foreignadd - " + this.dataWrapper.getNetwork().get(i).get("net_foreignadd")
                        + "] | [net state - " + this.dataWrapper.getNetwork().get(i).get("net_state")
                        + "] | [net pid - " + this.dataWrapper.getNetwork().get(i).get("net_pid") + "]";

            }
            final String showNetInfo = netInfo;
            Button btnNet = new Button("NETWORK") {

                @Override
                public boolean action(Event evt, Object what) {
                    showAlet(showNetInfo);
                    return true;
                }
            };
            btnNet.setFont(myFont);
            p9.add(btnNet);
            return mainPanel;
        }
    }
}
