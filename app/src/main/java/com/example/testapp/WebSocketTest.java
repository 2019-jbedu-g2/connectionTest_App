package com.example.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ResponseCache;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketTest extends AppCompatActivity {
    private WebSocketClient wsc;
    Button Connect_button,Disconnect_button,back_button;
    EditText socket_url_editor;
    TextView connectStatusTextView,RTextView;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_socket_test);
        initControls();
        Connect_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                url = socket_url_editor.getText().toString();
                RTextView.setText("");
                URI uri;
                if(!TextUtils.isEmpty(url)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Draft d = new Draft_6455();
                                  wsc = new WebSocketClient( new URI(url), d){
                                  public void onMessage(final String message){
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              RTextView.append(message);
                                          }
                                      });
                                  }
                                  public void onOpen(ServerHandshake handshake){
                                      RTextView.append("연결성공\n");
                                    //  wsc.send("연결완료");
                                  }
                                  public void onClose(int Code, String Reason, boolean Remote){
                                      RTextView.append("연결종료 : " + Code + " " + Reason +"\n");
                                  }
                                  public void onError(Exception ex){
                                      RTextView.append("문제발생 : "+ ex + "\n");
                                  }
                                  };

                            }catch (URISyntaxException e){
                                e.printStackTrace();
                            }
                            wsc.connect();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"URL창이 비어있습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Disconnect_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wsc.close();;
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initControls(){
        if(Connect_button == null){
            Connect_button = (Button) findViewById(R.id.Connect_button);
        }
        if(Disconnect_button == null){
            Disconnect_button = (Button) findViewById(R.id.Disconnect_button);
        }
        if(back_button == null){
            back_button = (Button) findViewById(R.id.back_button);
        }
        if(socket_url_editor == null){
            socket_url_editor = (EditText) findViewById(R.id.socket_url_editor);
        }
        if(connectStatusTextView == null){
            connectStatusTextView = (TextView) findViewById(R.id.connectStatusTextView);
        }
        if(RTextView == null){
            RTextView = (TextView) findViewById(R.id.RTextView);
        }
    }
}
