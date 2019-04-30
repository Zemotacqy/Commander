import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Write_Config {

    public static void main(String[] args) {

        try (OutputStream output = new FileOutputStream("./config.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("VM_username", "zemotacqy");
            prop.setProperty("VM_reverse_ssh_port", "9632");
            prop.setProperty("VM_password", "lifesawesome");

            // save properties to project root folder
            prop.store(output, null);

            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}