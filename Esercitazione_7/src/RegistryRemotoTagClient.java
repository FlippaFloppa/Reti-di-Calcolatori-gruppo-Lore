import java.rmi.Remote;

public interface RegistryRemotoTagClient extends RegistryRemotoClient {

    public Remote[] cercaTag(String tag);
    
}
