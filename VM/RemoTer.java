import com.jcraft.jsch.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class RemoTer {
    // Extra Streams for Debugging
    private static String input_string;
    private static byte[] input_data = new byte[1024];
    private static BufferedInputStream in = null;
    private static BufferedReader stdin = null;
    private static BufferedOutputStream out = null;
    private static InputStream config_io = null;
    private static String cmd_input;
    private static String VM_username = "";
    private static int VM_reverse_ssh_port = 0;
    private static String VM_password = "";

    private static void readData() throws IOException, JSchException{
        String str = "", byte_str = "", regex = "[\0]+";
        int bytesRead = in.read(input_data, 0, input_data.length);
        while(bytesRead != -1) {
            byte_str = new String(input_data);
            // trim the string of any unwanted characters;
            byte_str = byte_str.replaceAll(regex, "");
            str = str + byte_str;
            // check for ETX End of Text
            if((int)str.charAt(str.length() - 1)==3) { break; }
            bytesRead = in.read(input_data, 0, input_data.length);
        }
        input_string = str;
        System.out.println("Read String: " + input_string);
    }

    private static void sendData() throws IOException, JSchException{
        cmd_input = cmd_input + Character.toString((char)3);
        out.write(cmd_input.getBytes(), 0, cmd_input.length());
        out.flush();
        System.out.println("Output Sent! " + cmd_input.length());
    }

    public static void main(String[] args) throws IOException{
        
        String command = "";

        for(int i=0;i<args.length;i++){
            command = command + args[i] + " ";
        }
        // System.out.println("Command: " + command+"\n");
        
        try{
            stdin = new BufferedReader(new InputStreamReader(System.in));
        } catch(Exception e) {
            System.out.println("IO Failed");
            System.out.println(e);
            System.exit(-1);
        }

        try {
            config_io = new FileInputStream("./config.properties");
            Properties props = new Properties();
            props.load(config_io);
            VM_username = props.getProperty("local_system_username");
            VM_password = props.getProperty("local_system_password");
            VM_reverse_ssh_port = Integer.parseInt(props.getProperty("VM_reverse_ssh_port"));
        } catch(Exception e) {
            System.out.println("\nERROR: Cannot Read Config File.\n");
            System.out.println(e);
            System.exit(-1);
        }

       
        try{
            JSch jsch=new JSch();
            Session session=jsch.getSession(VM_username, "localhost", VM_reverse_ssh_port);
            session.setPassword(VM_password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // making a connection with timeout of 30seconds
            session.connect(30*1000);
            // set the default command   
            if(command.trim().length()<1){
                command = "pwd";
            }
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            ((ChannelExec) channel).setPty(true);

            // X Forwarding
            // channel.setXForwarding(true);

            channel.setInputStream(System.in);
            //channel.setInputStream(null);

            channel.setOutputStream(System.out);

            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream ins=channel.getInputStream();

            channel.connect();

            byte[] tmp=new byte[128];
            while(true){
                while(ins.available()>0){
                    int i=ins.read(tmp, 0, 128);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    if(ins.available()>0) continue;
                    System.out.println("\nEXIT-STATUS : " + channel.getExitStatus() + "\n");
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
        } catch(Exception e) {
            System.out.println("\nERROR: Your Local System cannot be connected. Try Again Later.\n");
            System.out.println(e);
            System.exit(-1);
        } finally {
            in.close();
            stdin.close();
            out.close();
        }
    }
}
