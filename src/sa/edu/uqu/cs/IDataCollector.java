package sa.edu.uqu.cs;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Muhammad Ashari Fatani
 */
public interface IDataCollector extends Remote {

    public String fetchData(String ip) throws RemoteException;

    public DataWrapper loadData(String ip) throws RemoteException;

    public boolean isAuth(String username, String password) throws RemoteException;

    public boolean newAdmin(String username, String password) throws RemoteException;
}
