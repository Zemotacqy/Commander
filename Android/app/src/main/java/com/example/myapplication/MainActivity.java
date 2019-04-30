package com.example.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText e1, e2;
    Button b1, b2;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.command_text);
        e2 = findViewById(R.id.optional);
        b1 = findViewById(R.id.send_button);
        b2 = findViewById(R.id.send_button2);
        t1 = findViewById(R.id.command_op);
        t1.setText("");
        b1.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        System.out.println("Something Clicked!!");
        switch(v.getId()) {
            case R.id.send_button:
                sendCommand("\"" + e1.getText().toString().trim() + "\"");

                break;
        }
    }

    private void sendCommand(final String msg) {
        Thread thread = new Thread(new Runnable() {



            @Override
            public void run() {
                String VM_ip, VM_username, VM_password;
                try{
                    Resources resources = getApplicationContext().getResources();

                    InputStream rawResource = resources.openRawResource(R.raw.config);
                    Properties properties = new Properties();
                    properties.load(rawResource);
                    VM_username = properties.getProperty("VM_username");
                    VM_password = properties.getProperty("VM_password");
                    VM_ip = properties.getProperty("VM_ip");

                    JSch jsch=new JSch();
                    Session session=jsch.getSession(VM_username, VM_ip, 22);
                    session.setPassword(VM_password);
                    Properties config = new Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.connect(30*1000);   // making a connection with timeout.
                    System.out.println("connected...");
                    String command = "cd /home/zemotacqy/remoter && java -cp \".:./jars/jsch.jar\" RemoTer " + msg;
                    final Channel channel=session.openChannel("exec");
                    ((ChannelExec)channel).setCommand(command);
                    // Sets true Terminal Not the virtual Terminal
                    ((ChannelExec) channel).setPty(true);

                    final OutputStream out = channel.getOutputStream();
                    InputStream in = channel.getInputStream();
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String str = e2.getText().toString();
                            str = str + (char)10;
                            byte[] buf = str.getBytes();
                            try {
                                out.write(buf, 0, buf.length);
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    ((ChannelExec)channel).setErrStream(System.err);

                    InputStream ins=channel.getInputStream();

                    channel.connect();

                    final byte[] tmp=new byte[128];
                    int x;
                    while(true){
                        while((x=ins.available())>0){
                            final int i=ins.read(tmp, 0, 128);
                            if(i<0)break;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    t1.append(new String(tmp, 0, i));
                                }
                            });
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
                    System.out.println("Some Error Occurred. Try Again Later.");
                    System.out.println(e);
                    System.exit(-1);
                }
            }
        });
        thread.start();
    }
}
