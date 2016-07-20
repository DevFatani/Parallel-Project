package sa.edu.uqu.cs;

import java.sql.*;
import java.util.HashMap;

public class MySqlConnection {

    private Connection con;
    private Statement st;
    private ResultSet rs;
    private final static String INSERT_NETWORK_DATA
            = "INSERT INTO monitor.network (net_proto,net_localadd,net_foreignadd,net_state,net_pid,ip)VALUES (?,?,?,?,?,?);";
    private final static String INSERT_RESOURCES_DATA
            = "INSERT INTO monitor.resources(cpu,memory,storage,ip,processId,processArch,processNum,host_name) VALUES (?,?,?,?,?,?,?,?);";
    private final static String INSERT_NEW_ADMIN
            = "INSERT INTO monitor.authentication(username,password) VALUES (MD5(?),MD5(?));";


    public MySqlConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/monitor", "root", "");
            this.st = this.con.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
        }
    }

    public DataWrapper getData(String ip) throws SQLException {
        final DataWrapper dataWrapper = new DataWrapper();
        final String query1 = "SELECT * FROM `network` WHERE ip=" + "\"" + ip + "\" ORDER BY `id` DESC LIMIT 5";
        final String query2 = "SELECT * FROM `resources` WHERE ip=" + "\"" + ip + "\" ORDER BY `id` DESC LIMIT 1";

        this.st = this.con.createStatement();
        this.rs = this.st.executeQuery(query1);
        while (this.rs.next()) {
            HashMap<String, String> hash = new HashMap<>();
            hash.put("net_proto", this.rs.getString("net_proto"));
            hash.put("net_localadd", this.rs.getString("net_localadd"));
            hash.put("net_foreignadd", this.rs.getString("net_foreignadd"));
            hash.put("net_state", this.rs.getString("net_state"));
            hash.put("net_pid", this.rs.getString("net_pid"));
            dataWrapper.getNetwork().add(hash);
        }

        this.st = this.con.createStatement();
        this.rs = this.st.executeQuery(query2);
        if (this.rs.next()) {
            dataWrapper.setCpuUsage(this.rs.getString("cpu"));
            dataWrapper.setUsageMemory(this.rs.getString("memory"));
            dataWrapper.setUsageStorage(this.rs.getString("storage"));
            dataWrapper.setProcessorArch(this.rs.getString("processArch"));
            dataWrapper.setProcessorId(this.rs.getString("processId"));
            dataWrapper.setProcessorNum(this.rs.getString("processNum"));
            dataWrapper.setHostName(this.rs.getString("host_name"));
        }
        this.rs.close();
        this.st.close();
        return dataWrapper;
    }

    public boolean isAuth(String username, String password) throws SQLException {
        final String query1 = "SELECT * FROM `authentication` WHERE username=" + "MD5(\"" 
                + username + "\") " + "AND" + " MD5(\"" + password + "\")";
        this.st = this.con.createStatement();
        this.rs = this.st.executeQuery(query1);
        boolean result = rs.next();
        this.con.close();
        this.st.close();
        this.rs.close();
        return result;
    }

    public void setDataResources(String cpu, String mem, String storage, String ip,
            String processId, String processArch, String processNum, String hostName) {
        try (PreparedStatement ps = this.con.prepareStatement(INSERT_RESOURCES_DATA)) {
            ps.setString(1, cpu);
            ps.setString(2, mem);
            ps.setString(3, storage);
            ps.setString(4, ip);
            ps.setString(5, processId);
            ps.setString(6, processArch);
            ps.setString(7, processNum);
            ps.setString(8, hostName);
            ps.execute();
            this.con.close();
            System.out.println("successful save resourses");
        } catch (Exception e) {
        }
    }

    public boolean signUp(String username, String password) {
        try (PreparedStatement ps = this.con.prepareStatement(INSERT_NEW_ADMIN)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.execute();
            this.con.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setDataNetwork(DataWrapper dataWrapper) {
        try {
            for (int i = 0; i < dataWrapper.getNetwork().size(); i++) {
                HashMap<String, String> hash = dataWrapper.getNetwork().get(i);
                PreparedStatement ps = this.con.prepareStatement(INSERT_NETWORK_DATA);
                ps.setString(1, hash.get("net_proto"));
                ps.setString(2, hash.get("net_localadd"));
                ps.setString(3, hash.get("net_foreignadd"));
                ps.setString(4, hash.get("net_state"));
                ps.setString(5, hash.get("net_pid"));
                ps.setString(6, dataWrapper.getIp());
                ps.execute();
                ps.close();
            }
            this.con.close();
            System.out.println("successful save network");
        } catch (Exception e) {
        }
    }
}