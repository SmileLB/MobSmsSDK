package com.example.admin.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * Description:
 * Created by LiBing
 * Data:2017/5/24 14:46
 */
public class RegSecondActivity extends AppCompatActivity{

    private EditText mEtCode;
    private Button mBtnResend;
    private Button mBtnOk;

    private String phone;
    private String pwd;
    private String countryCode;

    private CountTimerView countTimerView;

    private  SMSEvenHanlder evenHanlder;
    private TextView mTxtTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_second);
        initView();
        initData();
        initEvent();

        evenHanlder = new SMSEvenHanlder();//
        SMSSDK.registerEventHandler(evenHanlder);

    }

    private void initEvent() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });

        mBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.getVerificationCode("+"+countryCode, phone);
                countTimerView = new CountTimerView(mBtnResend,R.string.smssdk_resend_identify_code);
                countTimerView.start();
            }
        });
    }

    private  void submitCode(){
        String vCode = mEtCode.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            Toast.makeText(this, R.string.smssdk_write_identify_code,Toast.LENGTH_SHORT).show();
            return;
        }
        SMSSDK.submitVerificationCode(countryCode,phone,vCode);
    }

    private void initData() {
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        String formatedPhone = "+" + countryCode + " " + splitPhoneNum(phone);

        //String text = getString(R.string.smssdk_send_mobile_detail)+formatedPhone;
        mTxtTip.setText("验证码已发送至"+formatedPhone);
    }

    /** 分割电话号码 */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    private void initView() {
        mEtCode = (EditText) findViewById(R.id.edittxt_code);
        mBtnResend = (Button) findViewById(R.id.btn_reSend);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mTxtTip = (TextView) findViewById(R.id.txtTip);
        //开始计时
        CountTimerView timerView = new CountTimerView(mBtnResend);
        timerView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(evenHanlder);
    }

    class SMSEvenHanlder extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //校验验证码，返回校验的手机和国家代码
                            Toast.makeText(RegSecondActivity.this, "校验成功",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }
            });
        }
    }
}
