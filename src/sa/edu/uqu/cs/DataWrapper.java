package sa.edu.uqu.cs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Muhammad Ashari Fatani
 */
public class DataWrapper implements Serializable {
    
    private String ip;
    private String hostName;
    private String cpuUsage;
    private String cpuAvailable;
    private String totalMemory;
    private String usageMemory;
    private String freeMemory;
    private String totalStorage;
    private String freeStorage;
    private String usageStorage;
    private String processorId;
    private String processorArch;
    private String processorNum;
    private ArrayList<HashMap<String, String>> network;
    
    public DataWrapper() {
        this.network = new ArrayList<>();
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public String getProcessorArch() {
        return processorArch;
    }

    public void setProcessorArch(String processorArch) {
        this.processorArch = processorArch;
    }

    public String getProcessorNum() {
        return processorNum;
    }

    public void setProcessorNum(String processorNum) {
        this.processorNum = processorNum;
    }
    
    
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public String getCpuAvailable() {
        return cpuAvailable;
    }

    public void setCpuAvailable(String cpuAvailable) {
        this.cpuAvailable = cpuAvailable;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getUsageMemory() {
        return usageMemory;
    }

    public void setUsageMemory(String usageMemory) {
        this.usageMemory = usageMemory;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(String totalStorage) {
        this.totalStorage = totalStorage;
    }

    public String getFreeStorage() {
        return freeStorage;
    }

    public void setFreeStorage(String freeStorage) {
        this.freeStorage = freeStorage;
    }

    public String getUsageStorage() {
        return usageStorage;
    }

    public void setUsageStorage(String usageStorage) {
        this.usageStorage = usageStorage;
    }

    public ArrayList<HashMap<String, String>> getNetwork() {
        return network;
    }

    public void setNetwork(ArrayList<HashMap<String, String>> network) {
        this.network = network;
    }
}
