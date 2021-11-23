public interface RegistryRemotoTagServer extends RegistryRemotoServer, RegistryRemotoTagClient{

    public boolean associaTag(String nome_logico_server, String tag);
}