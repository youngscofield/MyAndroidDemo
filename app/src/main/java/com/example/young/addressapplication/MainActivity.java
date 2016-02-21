package com.example.young.addressapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends Activity {
    // 手机归属地Webservice的参数信息
    private static final String nameSpaceAddress = "http://WebXml.com.cn/";
    private static final String urlAddress = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
    private static final String methodNameAddress = "getMobileCodeInfo";
    private static final String soapActionAddress = "http://WebXml.com.cn/getMobileCodeInfo";

    private Button btnAddress = null;
    private EditText tel = null;
    private TextView telAddress = null;
    private String txtAddress = "";


    private Handler handlerAddress = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            System.out.println("××0××更新号码归属地数据，归属地为：" + txtAddress);
            telAddress.setText(txtAddress);
            Toast.makeText(MainActivity.this, "获取号码归属地成功" + txtAddress,
                    Toast.LENGTH_LONG).show();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddress = (Button) this.findViewById(R.id.btnSearchAddress);
        telAddress = (TextView) this.findViewById(R.id.telAddress);
        tel = (EditText) this.findViewById(R.id.telNo);

        btnAddress.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    public void run() {
                        getTelAddress();
                    }
                }).start();

            }
        });


    }


    public void getTelAddress() {
        System.out.println("××1××进入getTelAddress方法");
        SoapObject soapObject = new SoapObject(nameSpaceAddress,
                methodNameAddress);
        // 这边理论上要做输入验证的，例子图省事没做输入验证验证
        soapObject.addProperty("mobileCode", tel.getText().toString());
        soapObject.addProperty("userId", "");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(urlAddress);
        System.out.println("××2××基本服务设置完毕，下面开始调用服务");
        try {
            httpTransportSE.call(soapActionAddress, envelope);
            System.out.println("××3××调用webservice服务成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("××4××调用webservice服务失败");
        }

        SoapObject object = (SoapObject) envelope.bodyIn;
        System.out.println("××5××获得服务数据成功");
        txtAddress = object.getProperty(0).toString();
        //txtAddress ="测试无网状态";
        System.out.println("××6××解析服务数据成功，数据为：" + txtAddress);
        System.out.println("××7××向主线程发送消息，显示号码归属地");
        handlerAddress.sendEmptyMessage(0);
        System.out.println("××8××向主线程发送消息成功，getTelAddress函数执行完毕");
    }
}

