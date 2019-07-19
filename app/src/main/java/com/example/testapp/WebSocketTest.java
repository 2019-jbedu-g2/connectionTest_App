package com.example.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class WebSocketTest extends AppCompatActivity {
    private Socket mSocket;     // 소켓 선언.
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
                if(!TextUtils.isEmpty(url)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mSocket = IO.socket(url);    //소켓 연결한 주소
                                mSocket.on(Socket.EVENT_CONNECT,onConnect);         // 서버로 부터 받은 연락 체크 . 연결이 되었을때 , onConnect 함수 호출
                                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);  // 연결이 끊겼을때
                                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError); // 커넥트 에러일때
                                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT,onConnectError); //연결 시간초과일때
                                mSocket.connect();              // 소켓 연결 시도.
                            }catch (URISyntaxException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"URL창이 비어있습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Disconnect_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT,onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT,onConnectError);
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
    // 소켓 서버에 커넥트 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectStatusTextView.setText("연결 성공");
                    mSocket.emit("clientMessage","hi"); // 소켓에 해당 메시지를 전송.
                    mSocket.on("serverMessage",onMessageReceived);  // 서버로 부터 받은 메시지. onMesssageReceived 호출
                }
            });
        }
    };
    // 서버로부터 전달받은 이벤트 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject receivedData = (JSONObject) args[0]; // 받은 메시지를 담음
                    String Storename="";
                    String Barcode="";
                    String Number="";
                    try{
                        Storename = receivedData.getString("Storename"); // Storename과 Barcode라는 키값으로 왔다는 가정하에 설정.
                        Barcode = receivedData.getString("Barcode");
                        Number = receivedData.getString("Number");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    RTextView.setText("");
                    RTextView.append(Storename);
                    RTextView.append("\n");
                    RTextView.append(Barcode);
                    RTextView.append("\n");
                    RTextView.append(Number);
                    RTextView.append("\n");
                }
            });
        }
    };
    /*
https://dev.to/medaymentn/creating-a-realtime-chat-app-with-android--nodejs-and-socketio-4o55
https://unikys.tistory.com/276
https://cosmosjs.wordpress.com/tag/%EC%86%8C%EC%BC%93%ED%86%B5%EC%8B%A0/
https://choidev-1.tistory.com/71
https://cosmosjs.wordpress.com/2018/11/12/node-js4-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%99%80-socket-io%EB%A1%9C-%ED%86%B5%EC%8B%A0%ED%95%98%EA%B8%B0/
https://socket.io/blog/native-socket-io-and-android/
https://dev-juyoung.github.io/2017/09/05/android-socket-io/
http://naminsik.com/blog/3651
     */
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            connectStatusTextView.setText("연결 종료");
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            connectStatusTextView.setText("연결 실패");
        }
    };
}
