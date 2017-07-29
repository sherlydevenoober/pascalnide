/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.frontend.emulator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.pascal.frontend.R;
import com.duy.pascal.frontend.activities.AbstractAppCompatActivity;
import com.duy.pascal.frontend.code.CompileManager;
import  java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.duy.pascal.frontend.R.string.con_crashed;
import static com.duy.pascal.frontend.R.string.con_sendbyadb;
import static com.duy.pascal.frontend.R.string.con_sendbysock;
import static com.duy.pascal.frontend.R.string.debug;
import static com.duy.pascal.frontend.R.string.file;

/**
 * Created by Windows 10 on 7/22/2017.
 */

public class emulatorActivity extends AbstractAppCompatActivity {
    public static final String FILE_PATH = "file_name";
    ServerSocket server=null;
    private String connectionStatus=null;
    private Handler mHandler=null;
    Thread socketThread=null;
    Socket client;
    BufferedWriter os = null;
    BufferedReader is = null;
    boolean end=false;
    String file_path;
    TextView emuStatus;
    ProgressBar pb;
    boolean isDebug=false;
    int port=6699;
    public static final String TAG="Connection";
    public static final int TIMEOUT=9999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_emulator);
        bindView();
        mHandler=new Handler();

        socketThread = new Thread( initializeConnection);
        socketThread.start();

    }
    private Runnable initializeConnection = new Thread() {

        public void run() {
                client=null;
                mHandler.post(waitConnect);
                try{
                    end=false;

                    //Create and config socket
                    server = new ServerSocket();
                    server.setSoTimeout(TIMEOUT*1000);
                    server.setReuseAddress(true);
                    server.bind(new InetSocketAddress(port));

                    //Listen
                    client = server.accept();

                    //bind Stream
                    is = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    os = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                } catch (SocketTimeoutException e) {
                    connectionStatus=getResources().getString(R.string.con_timeout);
                    mHandler.post(showConnectionStatus);
                    emulatorActivity.this.finish();
                } catch (IOException e) {
                    Log.e(TAG, ""+e);
                } finally {
                    try {
                        if (server!=null){
                            server.close();
                            server=null;
                        }

                    }catch (IOException ex){
                        Log.d(TAG, "Can't close socket");
                    }
                }
                if (client!=null) {

                        mHandler.post(connectSuccess);

                        while (!end){
                            try{
                                String res=is.readLine();
                                if (res==null){
                                    emulatorActivity.this.finish();
                                    break;
                                }
                                switch (res){
                                    case "[DEBUG]":
                                            isDebug=true;
                                            sendFolderApp();
                                            mHandler.post(receivedFile);
                                        break;
                                    case "[EXECUTE]":
                                            isDebug=false;
                                            sendFolderApp();
                                            mHandler.post(receivedFile);
                                        break;
                                    case "[DONE]":
                                        if ((new File(file_path)).exists() && (new File(file_path)).isFile()) {
                                            send(os, "", "OK");
                                            mHandler.post(alertSendDatabyAbdtToast);
                                            end = true;
                                        } else {
                                            mHandler.post(alertSendDatabySocketToast);
                                            send(os,"","[SEND_FILE_SOCKET]");
                                            recevieFileThrougthSocket();
                                            end=true;
                                        }
                                        break;
                                    case "[FILE_NAME]":
                                            file_path=is.readLine();
                                        break;

                                }

                            }catch (IOException ex){
                                ex.printStackTrace();
                            }

                        }


                    try{
                        client.close();
                        client=null;
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    connectionStatus="Get: "+file_path;
                    mHandler.post(showConnectionStatus);
                    if (isDebug){
                        mHandler.post(debug);
                    } else {
                        mHandler.post(execute);
                    }

                }

        }

        private void sendFolderApp(){
            send(os,"", Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/PascalEmulator/emulator/tmp");
        }

        private void recevieFileThrougthSocket(){
            try{
                String res;
                String readFileLine="";
                File outputDir = emulatorActivity.this.getApplicationContext().getCacheDir(); // context being the Activity pointer
                File pasFile = File.createTempFile("emu", ".pas", outputDir);
                file_path=pasFile.getPath();
                DataOutputStream fileos = new DataOutputStream(new FileOutputStream(file_path));
                FileWriter mFileWriter = new FileWriter(pasFile);

                while (true ){
                    readFileLine=is.readLine();
                    if (readFileLine!=null){
                        mFileWriter.append(readFileLine);
                        mFileWriter.flush();
                    } else {
                        mFileWriter.close();
                        break;
                    }
                }
                fileos.close();

            } catch (Exception ex){
                ex.printStackTrace();
            }


        }
        private void send(BufferedWriter os,String FLAG,String cmd){
            try{
                os.write(FLAG+cmd);
                os.newLine();
                os.flush();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }

    };

    private Runnable execute = new Runnable() {
        public void run() {
            CompileManager.execute(emulatorActivity.this,file_path,false);
        }
    };
    private Runnable debug = new Runnable() {
        public void run() {
            CompileManager.debug(emulatorActivity.this,file_path);
        }
    };
    private Runnable showConnectionStatus = new Runnable() {
        public void run() {
            Toast.makeText(emulatorActivity.this, connectionStatus, 1).show();
        }
    };

    private Runnable connectSuccess =new Runnable(){
        public void run() {
            emuStatus.setText(getResources().getString(R.string.con_waitcmd));
        }
    };
    private Runnable waitConnect =new Runnable(){
        public void run() {
            emuStatus.setText(getResources().getString(R.string.con_waitcon));
        }
    };
    private Runnable receiveCmd =new Runnable(){
        public void run() {
            emuStatus.setText(getResources().getString(R.string.con_waitfile));
        }
    };
    private Runnable receivedFile =new Runnable(){
        public void run() {
            emuStatus.setText(getResources().getString(R.string.con_compile));
        }
    };
    private Runnable alertSendDatabyAbdtToast =new Runnable () {
        public void run() {
            Toast.makeText(emulatorActivity.this,getResources().getString(con_sendbyadb), 1).show();
        }
    };
    private Runnable alertSendDatabySocketToast =new Runnable () {
        public void run() {
            Toast.makeText(emulatorActivity.this, getResources().getString(con_sendbysock), 1).show();
        }
    };
    private Runnable crashToast =new Runnable () {
        public void run() {
            Toast.makeText(emulatorActivity.this, getResources().getString(con_crashed), 1).show();
        }
    };

    private void bindView(){

        emuStatus= (TextView) findViewById(R.id.emu_status);
        pb=(ProgressBar) findViewById(R.id.emu_progressbar);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mHandler=new Handler();

        socketThread = new Thread( initializeConnection);
        socketThread.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if (server!=null){
                server.close();
                server=null;
            }
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/PascalEmulator/emulator/tmp");
            dir.delete();
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }
}
