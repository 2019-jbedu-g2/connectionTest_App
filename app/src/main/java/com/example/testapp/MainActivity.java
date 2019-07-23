package com.example.testapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    Button Send_button, Bscanner_button,WStest_button;
    TextView tv, barcodetv;
    EditText requestUrlEditor = null;
    String url = "";
    String barcode = "";
    String PG = "GET";
    RadioGroup RadioGroup;
    RadioButton get, post;
    String barcodenumber = "";
    public static Context mContext;

    ImageView img;
    //ContentValues request  = new ContentValues();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 연결안된 위젯들 확인
        initControls();
        //AsyncTask를 통해 HttpURLConnection 수행.
        mContext = this;    // 함수호출용
        Send_button.setOnClickListener(new View.OnClickListener (){
            public void onClick(View v){
                //URL설정.
                url = requestUrlEditor.getText().toString();
                if(!TextUtils.isEmpty(url)){
                    //   if(URLUtil.isHttpsUrl(url) || URLUtil.isHttpsUrl(url)){
                    // url과 파라메터값을 전달(현재는 파라미터 값을 null로 처리하고 url에 직접 입력하게 해둠. 입력값들을 받아 차례로 넣어 RequestHttpURLConnection에서 자동으로 조립할 수 있도록 가능.
                    com.example.testapp.MainActivity.NetworkTask networkTask = new com.example.testapp.MainActivity.NetworkTask(url,null);
                    networkTask.execute();  // 비동기 task 작동.
                    //  }else{
                    //      Toast.makeText(getApplicationContext(),"유효한 주소값이 아닙니다.",Toast.LENGTH_SHORT).show();
                    //   }
                }else{
                    Toast.makeText(getApplicationContext(),"URL창이 비어있습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });
        // 바코드 스캐너 호출
        Bscanner_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IntentIntegrator intent = new IntentIntegrator(MainActivity.this); // zxing 내부의 스캐너 호출
                intent.setBeepEnabled(true);        // 바코드 인식시에 비프음의 여부
                intent.initiateScan();              // 스캔화면으로 넘어감.
            }
         });
        //소켓통신 호출
        WStest_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebSocketTest.class);
                startActivity(intent);
            }
        });
        // 라디오버튼 이벤트
        RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.GET_radiobutton){
                    PG = "GET";
                } else if(checkedId == R.id.POST_radioButton){
                    PG = "POST";
                }
            }
        });
    }
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        //  com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
        //  = 0x0000c0de; // Only use bottom 16 bits
        if(requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);  // 결과물을 받을 그릇 생성
            if(result == null) {
                Toast.makeText(this, "Cancelled",Toast.LENGTH_LONG).show();     // 결과물이 없다면 취소 토스트 출력
            } else {
                barcodenumber = result.getContents();                       // 결과물을 받아서 변수에 집어넣음
                tv.setText(barcodenumber);                                  // 결과물을 텍스트 필드에 띄워줌
            }
            }else{
                super.onActivityResult(requestCode,resultCode,data);            // 재시도.
            }
        }

    // 위젯에 대한 참조.
    private void initControls(){
        if(requestUrlEditor == null){
            requestUrlEditor = (EditText) findViewById(R.id.http_url_editor);
        }
        if(tv == null){
            tv = (TextView) findViewById(R.id.tv);
        }
        if(barcodetv == null){
            barcodetv = (TextView) findViewById(R.id.barcodetv);
        }
        if(Send_button == null){
            Send_button = (Button)  findViewById(R.id.Send_button);
        }
        if(Bscanner_button == null){
            Bscanner_button = (Button)  findViewById(R.id.Scanner_button);
        }
        if(WStest_button == null){
            WStest_button = (Button)  findViewById(R.id.NextTab_button);
        }
        if (img == null){
            img = (ImageView) findViewById(R.id.imageView1);
        }
        if (RadioGroup == null){
            RadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        }
        if (get == null){
            get = (RadioButton) findViewById(R.id.GET_radiobutton);
        }
        if (post == null){
            post = (RadioButton) findViewById(R.id.POST_radioButton);
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
//            String T = s.substring(11);
//            tv.setText(T);
//            String bar = s.substring(0,10);
//            barcodetv.setText(bar);   // 바코드 하단 번호 출력
//            barcode = bar;
//            Bitmap barcodes = createBarcode(barcode);   // 바코드 생성 함수를 통하여 생성 후 Bitmap 변수에 저장
//            img.setImageBitmap(barcodes);               // 이미지 뷰에 Bitmpa을 그림.
//            img.invalidate();                       // 이미지뷰를 초기화 시켜 화면을 갱신시킴.
        }
        // 바코드 생성
        public Bitmap createBarcode(String code){
            Bitmap bitmap = null;
            MultiFormatWriter gen = new MultiFormatWriter();            // 포맷
            try{
                final int WIDTH = 840;                              // 바코드 너비
                final int HEIGHT = 320;                             // 바코드 높이
                BitMatrix bytemap = gen.encode(code, BarcodeFormat.CODE_128, WIDTH,HEIGHT); // 바코드 규격 세팅
                bitmap = Bitmap.createBitmap(WIDTH,HEIGHT,Bitmap.Config.ARGB_8888);     // 바코드를 그릴 비트맵 세팅
                for(int i = 0 ; i < WIDTH ; ++i){
                    for(int j = 0 ; j < HEIGHT ; ++j){
                        bitmap.setPixel(i,j,bytemap.get(i,j)? Color.BLACK : Color.WHITE);         // 비트맵에 바코드 정보를 그림(삼항 연산자를 통해 그림)
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }
    public String getPG(){      // 요청방식을 전송
        String re = PG;
        return re;
    }
}
