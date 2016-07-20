package sa.edu.uqu.cs;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Muhammad Ashari Fatani
 */
public class DataCollectorImpl extends UnicastRemoteObject implements IDataCollector {

    private DataWrapper dataWrapper;

    public DataCollectorImpl() throws RemoteException {
        super();
        this.dataWrapper = new DataWrapper();
    }

    @Override
    public String fetchData(String ip) throws RemoteException {
        //socket connection
        try (Socket client = new Socket(ip, Constants.SOCKET_PORT)) {
            InputStream chanel = client.getInputStream();
            ObjectInputStream result = new ObjectInputStream(chanel);
            this.dataWrapper = (DataWrapper) result.readObject();
            result.close();
            chanel.close();
        } catch (Exception e) {
        }
        this.dataWrapper.setIp(ip);

        new Thread() {

            @Override
            public void run() {
                storeResourcesInfo();
            }

        }.start();

        new Thread() {

            @Override
            public void run() {
                storeNetworkInfo();
            }

        }.start();

        return this.dataWrapper.getHostName();
    }

    private void storeResourcesInfo() {
        long usedMemory = new Long(this.dataWrapper.getTotalMemory()) - new Long(this.dataWrapper.getFreeMemory());
        BigDecimal usedMemoryDecimal = new BigDecimal(usedMemory);
        BigDecimal totalMemoryDecimal = new BigDecimal(this.dataWrapper.getTotalMemory());
        BigDecimal usedMemoryPrec = (usedMemoryDecimal.divide(totalMemoryDecimal, 2,
                RoundingMode.HALF_UP)).multiply(new BigDecimal(100));

        long usedStorage = new Long(this.dataWrapper.getTotalStorage())
                - new Long(this.dataWrapper.getFreeStorage());
        BigDecimal usedStorageDecimal = new BigDecimal(usedStorage);
        BigDecimal totalStorageDecimal = new BigDecimal(this.dataWrapper.getTotalStorage());
        BigDecimal usedStoragePrec
                = (usedStorageDecimal.divide(totalStorageDecimal, 2,
                        RoundingMode.HALF_UP)).multiply(new BigDecimal(100));
        new MySqlConnection().setDataResources(this.dataWrapper.getCpuUsage(),
                usedMemoryPrec + "", usedStoragePrec + "", this.dataWrapper.getIp(),
                this.dataWrapper.getProcessorId(), this.dataWrapper.getProcessorArch(),
                this.dataWrapper.getProcessorNum(), this.dataWrapper.getHostName());
    }

    private void storeNetworkInfo() {
        new MySqlConnection().setDataNetwork(this.dataWrapper);
    }

    @Override
    public DataWrapper loadData(String ip) throws RemoteException {
        try {
            return new MySqlConnection().getData(ip);
        } catch (SQLException ex) {
            Logger.getLogger(DataCollectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public boolean isAuth(String username, String password) throws RemoteException {
        try {
            return new MySqlConnection().isAuth(username, password);
        } catch (SQLException ex) {
            Logger.getLogger(DataCollectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean newAdmin(String username, String password) throws RemoteException {
        return new MySqlConnection().signUp(username, password);
    }
}
