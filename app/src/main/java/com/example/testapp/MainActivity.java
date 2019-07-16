package com.example.testapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button Send_button;
    TextView tv;
    EditText requestUrlEditor = null;
    String url = "";
    String barcode = "";
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 연결안된 위젯들 확인
        initControls();
        //AsyncTask를 통해 HttpURLConnection 수행.
        Send_button.setOnClickListener(new View.OnClickListener (){
            public void onClick(View v){
                //URL설정.
                url = requestUrlEditor.getText().toString();
                if(!TextUtils.isEmpty(url)){
                 //   if(URLUtil.isHttpsUrl(url) || URLUtil.isHttpsUrl(url)){
                    // url과 파라메터값을 전달(현재는 파라미터 값을 null로 처리하고 url에 직접 입력하게 해둠. 입력값들을 받아 차례로 넣어 RequestHttpURLConnection에서 자동으로 조립할 수 있도록 가능.
                        NetworkTask networkTask = new NetworkTask(url,null);
                        networkTask.execute();  // 비동기 task 작동.
                  //  }else{
                  //      Toast.makeText(getApplicationContext(),"유효한 주소값이 아닙니다.",Toast.LENGTH_SHORT).show();
                 //   }
                }else{
                    Toast.makeText(getApplicationContext(),"URL창이 비어있습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    // 위젯에 대한 참조.
    private void initControls(){
        if(requestUrlEditor == null){
            requestUrlEditor = (EditText) findViewById(R.id.http_url_editor);
        }
        if(tv == null){
            tv = (TextView) findViewById(R.id.tv);
        }
        if(Send_button == null){
            Send_button = (Button)  findViewById(R.id.Send_button);
        }
        if (img == null){
            img = (ImageView) findViewById(R.id.imageView1);
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String>{
        String url;
        ContentValues values;
        NetworkTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        //background작업 시작전 ui작업을 진행.
        protected void onPreExecute(){
            super.onPreExecute();
        }
        // background 작업 진행
        protected  String doInBackground(Void... params){
            String result; // 요청 결과를 저장할 변수
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,values); // 해당 주소로 부터 결과물 얻음
            return result;
        }
        // 끝난후 ui진행
        protected  void onPostExecute(String s){
            super.onPostExecute(s);
            //doInBackground 로 부터 리턴된 값이 매개변수로 넘어오므로 s를 추력.
            tv.setText(s);
            barcode = s;
         //   Bitmap barcodes = createBarcode(barcode);
      //      img.setImageBitmap(barcodes);
      //      img.invalidate();
        }
        /*
        public Bitmap createBarcode(String code){
            Bitmap bitmap = null;
            MultiFormatWriter gen = new MultiFormatWriter();
            com.google.zxing.
        }
        */
    }
}
