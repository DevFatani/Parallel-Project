package sa.edu.uqu.cs;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Muhammad Ashari Fatnai
 */
public class RMIServer {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        DataCollectorImpl hello = new DataCollectorImpl();
        Registry registry = LocateRegistry.createRegistry(Constants.RMI_PORT);
        registry.bind(Constants.RMI_ID, hello);
        System.out.println("service is started...");
    }
}
