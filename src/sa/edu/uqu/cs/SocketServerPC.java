package sa.edu.uqu.cs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServerPC {

    private DataWrapper dataWrapper;

    public DataWrapper getDataWrapper() {
        return dataWrapper;
    }

    public SocketServerPC() {
        this.dataWrapper = new DataWrapper();
    }

    private void getPcName() {
        String hostname = "";
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (Exception ex) {
        }
        this.dataWrapper.setHostName(!hostname.isEmpty() ? hostname : "unknown");
    }

    private void getHardwareInfo() {
        this.dataWrapper.setProcessorId(System.getenv("PROCESSOR_IDENTIFIER"));
        this.dataWrapper.setProcessorArch(System.getenv("PROCESSOR_ARCHITECTURE"));
        this.dataWrapper.setProcessorNum(System.getenv("NUMBER_OF_PROCESSORS"));
    }

    private void getTotalMemory() throws Exception {
        com.sun.management.OperatingSystemMXBean bean
                = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        this.dataWrapper.setTotalMemory(bean.getTotalPhysicalMemorySize() + "");
    }

    private void getFreeMemory() throws Exception {
        com.sun.management.OperatingSystemMXBean bean
                = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        this.dataWrapper.setFreeMemory(bean.getFreePhysicalMemorySize() + "");;
    }

    private void getCpuUsage() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        Process pr = runtime.exec("wmic cpu get loadpercentage");
        pr.waitFor();
        InputStream inputStream = pr.getInputStream();
        char[] cbuf = new char[inputStream.available()];
        if (pr.exitValue() == 0) {
            BufferedReader outReader = new BufferedReader(new InputStreamReader(inputStream));
            outReader.read(cbuf);
            String prec = new String(cbuf).trim();
            this.dataWrapper.setCpuUsage(extractThePercentage(prec));
        } else {
            this.dataWrapper.setCpuUsage("0");
        }
    }

    private static String extractThePercentage(String commandResult) {

        String flage = "LoadPercentage";
        int index = commandResult.indexOf(flage);
        if (index != -1) {
            int trimIndex = index + flage.length();
            commandResult = commandResult.substring(trimIndex);
            commandResult = commandResult.trim();
        } else {
            throw new IllegalArgumentException("Can't find the flage [" + flage + "]");
        }
        return commandResult;
    }

    private void getCpuAvailable() throws Exception {
        this.dataWrapper.setCpuAvailable(Runtime.getRuntime().availableProcessors() + "");
    }

    private void getTotalStorage() throws Exception {
        this.dataWrapper.setTotalStorage(new File("/").getTotalSpace() + "");
    }

    private void getFreeStorage() throws Exception {
        this.dataWrapper.setFreeStorage(new File("/").getFreeSpace() + "");
    }

    private void getNetStatistics() throws InterruptedException, IOException, Exception {
        String[] cmdarray = {"netstat", "-o"};
        Runtime runtime = Runtime.getRuntime();
        Process pr = runtime.exec(cmdarray);
//        pr.waitFor();
        InputStream inputStream = pr.getInputStream();
        Scanner sc = new Scanner(inputStream);
        sc.useDelimiter("\\s*Active Connections\\s*");
        String commandResult = sc.next();
        this.dataWrapper.getNetwork().addAll(minipulateData(commandResult));
        System.out.println(this.dataWrapper.getNetwork().size());
    }

    private static ArrayList<HashMap<String, String>> minipulateData(String commandResult) throws Exception {

        ArrayList<HashMap<String, String>> netInfo = new ArrayList<>();
        String NEW_LINE = System.getProperty("line.separator");
        try {
            try (Scanner scanner = new Scanner(commandResult.trim())) {
                scanner.useDelimiter("Proto  Local Address          Foreign Address        State           PID\\S*");
                commandResult = scanner.next();
            }
            commandResult = commandResult.trim();
            while (commandResult.contains(NEW_LINE)) {
                int index = commandResult.indexOf(NEW_LINE);
                String rawData = commandResult.substring(0, index);
                StringTokenizer tokenizer = new StringTokenizer(rawData, " ");
                String[] buffer = new String[5];
                int partIndex = 0;
                while (tokenizer.hasMoreElements()) {
                    String part = (String) tokenizer.nextElement();
                    buffer[partIndex++] = part;
                }
                HashMap<String, String> hash = new HashMap<>();
                hash.put("net_proto", buffer[0]);
                hash.put("net_localadd", buffer[1]);
                hash.put("net_foreignadd", buffer[2]);
                hash.put("net_state", buffer[3]);
                hash.put("net_pid", buffer[4]);
                netInfo.add(hash);
                commandResult = commandResult.substring(index).trim();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("We have problem with parsing the raw data [" + commandResult + "]");
        }
        return netInfo;
    }

    public static void main(String[] args) throws InterruptedException, IOException, Exception {
        System.out.println("PC server is started");
        Socket server = new ServerSocket(122).accept();
        OutputStream chanel = server.getOutputStream();
        ObjectOutputStream writer = new ObjectOutputStream(chanel);
      final  SocketServerPC pc = new SocketServerPC();

        Thread th1 = new Thread() {
            @Override
            public void run() {
                pc.getPcName();
                try {
                    pc.getCpuUsage();
                } catch (Exception ex) {
                    Logger.getLogger(SocketServerPC.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread th2 = new Thread() {
            @Override
            public void run() {
                pc.getHardwareInfo();
                try {
                    pc.getTotalMemory();
                    pc.getFreeMemory();
                    pc.getCpuAvailable();
                    pc.getTotalStorage();
                    pc.getFreeStorage();

                } catch (Exception ex) {
                    Logger.getLogger(SocketServerPC.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread th3 = new Thread() {

            @Override
            public void run() {
                try {
                    pc.getNetStatistics();
                } catch (IOException ex) {
                    Logger.getLogger(SocketServerPC.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(SocketServerPC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        th1.start();
        th2.start();
        th3.start();
        th1.join();
        th2.join();
        th3.join();

        writer.writeObject(pc.getDataWrapper());
        writer.close();
        chanel.close();

    }
}
