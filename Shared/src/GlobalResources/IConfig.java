package GlobalResources;

public interface IConfig {
    String name = "";
    int pid = 0;
    String address = "";
    int port = 0;


    public int getPid();

    public void setPid(int pid);

    public String getAddress();

    public void setAddress(String address);

    public int getPort();

    public void setPort(int port_client);

    public String getName();

    public void setName(String name);
}
