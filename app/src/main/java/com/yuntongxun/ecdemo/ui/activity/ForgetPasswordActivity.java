package com.yuntongxun.ecdemo.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.bean.ForgetBean;
import com.yuntongxun.ecdemo.bean.VerifyCodeBean;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.net.BaseObserver;
import com.yuntongxun.ecdemo.net.Net;

import io.reactivex.disposables.Disposable;

/**
 * 登陆
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername;
    EditText etUserpwd;
    ImageView delet_image;
    TextView thorcode;
    ImageView mDeleteThorcode;
    EditText passwordEt;
    ImageView passwordDelIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initViews();
    }


    private void initViews() {
        etUsername = (EditText) findViewById(R.id.activity_login_et_username);
        etUserpwd = (EditText) findViewById(R.id.activity_login_et_password);
        delet_image = (ImageView) findViewById(R.id.delete_phoen_num);
        thorcode = (TextView) findViewById(R.id.getauthorcode);
        mDeleteThorcode = (ImageView) findViewById(R.id.delete_thcode);
        passwordDelIv = (ImageView) findViewById(R.id.delete_password);
        passwordEt = (EditText) findViewById(R.id.passwordEt);

        findViewById(R.id.delete_phoen_num).setOnClickListener(this);
        findViewById(R.id.delete_thcode).setOnClickListener(this);
        findViewById(R.id.delete_password).setOnClickListener(this);
        findViewById(R.id.activity_forget).setOnClickListener(this);
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.getauthorcode).setOnClickListener(this);

        mHandler2 = new Handler();
        initListener();
    }

    @Override
    protected void onDestroy() {
        mHandler2.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initListener() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                delet_image.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().replace(" ", "");
                if (TextUtils.isEmpty(phone)) {
                    delet_image.setVisibility(View.INVISIBLE);
                    thorcode.setClickable(false);
                } else if (11 == phone.length()) {
                    thorcode.setClickable(true);
                } else {
                    thorcode.setClickable(false);
                }
            }
        });
        etUserpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDeleteThorcode.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mDeleteThorcode.setVisibility(View.INVISIBLE);
                }

            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordDelIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    passwordDelIv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    int time = 60;
    Handler mHandler2;
    Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            thorcode.setText(time-- + "秒后获取");
            if (time >= 0) {
                mHandler2.postDelayed(mRefreshRunnable, 1000);
            } else {
                stopCount();
            }
        }
    };

    public void startCount() {
        time = 60;
        thorcode.setTextColor(Color.parseColor("#999999"));
        thorcode.setEnabled(false);
        mHandler2.postDelayed(mRefreshRunnable, 0);

    }

    public void stopCount() {
        time = -1;
        thorcode.setTextColor(Color.parseColor("#16BF8D"));
        thorcode.setEnabled(true);
        thorcode.setText("接收验证码");
        mHandler2.removeCallbacks(mRefreshRunnable);
    }

    /**
     * 判断电话号码是否完整
     *
     * @param phone
     * @return
     */
    public boolean judgePhone(String phone) {
        //如果电话为空，或者电话长度不为11，或者不是1开头,则返回
        if (TextUtils.isEmpty(phone) || phone.length() != 11 || !phone.startsWith("1")) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return true;
        }
        etUserpwd.requestFocus();
        return false;
    }

    public void forgetPwd(String phone, String pwd, String code) {
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "手机号码错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Net.forget(phone, pwd, code, new BaseObserver<ForgetBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ForgetBean forgetBean) {
                if (forgetBean.getStatusCode() == 0) {
                    finish();
                } else {
                    ToastUtil.showMessage(forgetBean.getStatusMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showMessage(e.getMessage());
            }
        });
    }

    private void requestCode(String phone) {
        //如果电话为空，或者电话长度不为11，或者不是1开头,则返回
        if (judgePhone(phone)) {
            return;
        }

        Net.getVerifyMsg(phone, "F", new BaseObserver<VerifyCodeBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(VerifyCodeBean bean) {
                if (bean.getStatusCode() == 0) {
                    startCount();
                    ToastUtil.showMessage("验证码已发送，请注意查收手机短信");
                } else {
                    ToastUtil.showMessage(bean.getStatusMsg());
                    stopCount();
                }
            }

            @Override
            public void onError(Throwable e) {
                stopCount();
                ToastUtil.showMessage(e.getMessage());
            }
        });
    }

    /**
     * 获取格式化之后的电话号码，未格式化之前有空格
     *
     * @return
     */
    private String getPhoneNumberFormat() {
        return etUsername.getText().toString().replace(" ", "").trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_phoen_num://删除电话
                etUsername.setText("");
                etUsername.requestFocus();
                break;
            case R.id.delete_thcode://删除验证码
                etUserpwd.setText("");
                etUserpwd.requestFocus();
                break;
            case R.id.delete_password://删除密码
                passwordEt.setText("");
                passwordEt.requestFocus();
                break;
            case R.id.activity_forget://注册
                String name = getPhoneNumberFormat();
                String code = etUserpwd.getText().toString();
                String pwd = passwordEt.getText().toString();
                forgetPwd(name, pwd, code);
                break;
            case R.id.getauthorcode://请求验证码
                thorcode.setEnabled(false);
                requestCode(getPhoneNumberFormat());
                break;
            case R.id.backIv://返回
                finish();
                break;
        }
    }
}
