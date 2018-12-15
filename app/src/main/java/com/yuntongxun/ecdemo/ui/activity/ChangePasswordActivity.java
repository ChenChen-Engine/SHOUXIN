package com.yuntongxun.ecdemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.bean.ChangePasswordBean;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.net.BaseObserver;
import com.yuntongxun.ecdemo.net.Net;
import com.yuntongxun.ecdemo.net.utils.SPUtils;

import io.reactivex.disposables.Disposable;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView oldDel, newDel, againDel;
    private EditText oldEt, newEt, againEt;
    private Button changeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initViews();
    }

    private void initViews() {
        findViewById(R.id.oldDel).setOnClickListener(this);
        findViewById(R.id.oldDel).setOnClickListener(this);
        findViewById(R.id.againDel).setOnClickListener(this);
        findViewById(R.id.changeBtn).setOnClickListener(this);
        oldEt = findViewById(R.id.oldEt);
        newEt = findViewById(R.id.newEt);
        againEt = findViewById(R.id.againEt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oldDel:
                oldEt.setText("");
                break;
            case R.id.newDel:
                newEt.setText("");
                break;
            case R.id.againDel:
                againEt.setText("");
                break;
            case R.id.changeBtn:
                change();
                break;
        }
    }

    private void change() {
        if(jumpPwd(oldEt.getText().toString())){
            return;
        }
        if(jumpPwd(newEt.getText().toString())){
            return;
        }
        if(jumpPwd(againEt.getText().toString())){
            return;
        }

        if(!newEt.getText().toString().equals(againEt.getText().toString())){
            Toast.makeText(this, "两次输入的密码不一致，请重新输入!", Toast.LENGTH_SHORT).show();
            return;
        }

        Net.changePassword(SPUtils.getPhone(), oldEt.getText().toString(), newEt.getText().toString(), new BaseObserver<ChangePasswordBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ChangePasswordBean bean) {
                if(bean!=null){
                    String msg = null;
                    if(!TextUtils.isEmpty(bean.getData())){
                        msg = bean.getData();
                    }else if(!TextUtils.isEmpty(bean.getStatusMsg())){
                        msg = bean.getStatusMsg();
                    }else {
                        msg = "修改异常，请联系开发者！";
                    }
                    Toast.makeText(ChangePasswordActivity.this,msg, Toast.LENGTH_SHORT).show();
                    if(bean.getStatusCode() == 0){
                        finish();
                    }
                }else{
                    Toast.makeText(ChangePasswordActivity.this, "修改异常，请联系开发者！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private boolean jumpPwd(String password){
        if(password.length()<6){
            Toast.makeText(this, "密码输入错误", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return false;
        }
    }
}
