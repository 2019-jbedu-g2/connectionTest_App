package com.example.testapp;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {
    private String RequestType ="";
    public String request(String _url, ContentValues _params) {
        RequestType = getType();    // Request 타입 확인
        //HttpURLConnection 참조 변수
        HttpURLConnection urlConn = null;
        //URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        // 스트링 버퍼에 파라미터 연결
        //보낼 데이터가 없을경우 비운다
        if (_params == null) {
            sbParams.append("");
        }
        //보낼 데이터가 있으면 채운다
        else {
            //파라미터가 2개 이상이면 파라미터 연결에 &을 사용하므로 스위칭할 변수를 생성한다.
            boolean isAnd = false;
            //파라미터 키와 값.
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                //파라미터가 두개 이상일때, 파라미터 사이에 &을 붙인다.
                if (isAnd) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=").append(value); // 키 값과 밸류값을 붙인다.
                // 두개 이상이면 is And 를 true로 바꾸고 다음루프부터 &을 붙이도록 설정
                if (!isAnd) {
                    if (_params.size() >= 2) {
                        isAnd = true;
                    }
                }

            }
        }
        //HttpURLConnection을 통해 web의 데이터를 가져온다.

        try{
            StringBuffer adress = new StringBuffer();
            adress.append(_url);
            adress.append(sbParams.toString());
            URL url = new URL(adress.toString());
            // url에 대한 연결클래스인  urlconnection을 획득. getinputstream을 사용하여 서버로 부터 데이터를 읽어 올수 있게됨
            urlConn = (HttpURLConnection) url.openConnection();

            //urlconn 설정
            urlConn.setReadTimeout(1000);   // 읽어 들일 시 연결 시간. 서버를 보호하기위한 설정 1000 = 1초
            urlConn.setConnectTimeout(1000);  // 서버 접속시 연결 시간. 위의 1000과 합하여 2초내에 연결 되지않으면 서버와의 연결을 포기.
            urlConn.setRequestMethod(RequestType); // URL요청에 대한 메소드 설정 : GET(리소스 취득)/POST(리소스 생성/데이터추가)/PUT(리소스 변경)/DELETE(리소스 삭제)로 설정
            if(RequestType.equals("POST")) {
                 urlConn.setDoOutput(true);   //쓰는 기능 on. default값 false. (주석을 해제 하면 강제로 요청 메소드 방식이 POST로 변경)
            }
            urlConn.setDoInput(true);   // 읽어들임 기능 on
            // Accept-Charset - 클라이언트가 이해 할수 있는 캐릭터셋이 무엇인지 알려줌.
            urlConn.setRequestProperty("Accept", "application/json");  // 서버의 Response 데이터를 json 형식으로 요청.
            // 리소스의 MEDIA TYPE을 나타냄 (MIME-TYPE의 하나)
            urlConn.setRequestProperty("Context_Type", "application/json"); // 타입설정(text/html)형식으로 전송(json으로 전달)
            //urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset - 클라이언트가 이해 할수 있는 캐릭터셋이 무엇인지 알려줌.
            //urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8"); // 리소스의 MEDIA TYPE을 나타냄 (MIME-TYPE의 하나)
            if(RequestType.equals("POST")) {
                //파라미터 전달 및 데이터 읽어오기 (강제로 요청 메소드 방식이 POST로 변경)
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
                pw.write(sbParams.toString()); // 출력 스트림에 출력
                pw.flush(); // 출력스트림을 비우고 버퍼링된 모든 출력 바이트를 강제 실행.
                pw.close(); // 출력스트림을 닫고 모든 시스템 자원을 해제
            }

            //연결 요청 확인
            // 실패 시 null을 리턴하고 종료
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) return null;        // ResponseCode가 200이 아니면 연결 실패로 리턴

            //읽어온 결과물 리턴
            // 요청한 url의 출력물을 버퍼리더로 받음.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            //출력물의 라인과 그 합에 대한 변수
            String line;
            String page = "";

            while (((line = reader.readLine()) != null)) {              // 버퍼 리더내의 글들을 스트링으로 붙임.
                page += line;
            }
            return page;
        } catch(
                MalformedURLException e){ // for URL
            e.printStackTrace();
        }catch(IOException e){  // for openConnection()
            e.printStackTrace();
        }finally{
            if (urlConn != null) urlConn.disconnect();                  // 완료 후 연결 끊음.
        }
        return null;

    }
    public String getType(){        // 요청 메소드를 메인액티비티로부터 받음.
        String type = "";
        type = ((MainActivity)MainActivity.mContext).getPG();
        return type;
    }

}

