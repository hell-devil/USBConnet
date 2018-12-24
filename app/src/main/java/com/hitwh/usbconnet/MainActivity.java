package com.hitwh.usbconnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ServerThread";
    private static String msg="0";
    private TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.sample_text);
        new ServerThread().start();
    }
    File file;
    class ServerThread extends Thread {
        @Override
        public void run() {
            Log.i(TAG, "running");
            ServerSocket serverSocket = null;
            DataOutputStream dos;
            FileInputStream fis;
            try {
                file = new File("/storage/emulated/0/myCameraPhoto/1234.mp4");
                if (file.length() == 0) {
                    Log.i(TAG, "文件长度为0！");
                    return;
                }
                Log.i(TAG, "完成文件读取");
                serverSocket = new ServerSocket(3580);
                Log.i("TAG", "等待连接");
                while (true) {
                    Socket client = serverSocket.accept();
                    dos = new DataOutputStream(client.getOutputStream());
                    fis = new FileInputStream(file);
                    byte[] sendBytes = new byte[16 * 1024 * 8];
                    int length;
                    while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                        dos.write(sendBytes, 0, length);
                        dos.flush();// 发送给服务器
                        Log.i("TAG", "发送："+length);
                    }
                    dos.close();//在发送消息完之后一定关闭，否则服务端无法继续接收信息后处理，手机卡机
                    fis.close();
                    msg=msg+'0';
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(msg);
                        }
                    });
                    Log.i(TAG, "accept");
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "destory");

                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}