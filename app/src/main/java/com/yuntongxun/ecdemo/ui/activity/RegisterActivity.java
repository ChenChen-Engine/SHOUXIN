package com.yuntongxun.ecdemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.bean.RegisterBean;
import com.yuntongxun.ecdemo.bean.VerifyCodeBean;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.dialog.ECProgressDialog;
import com.yuntongxun.ecdemo.common.utils.AppManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.PermissionUtils;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.core.ContactsCache;
import com.yuntongxun.ecdemo.net.BaseObserver;
import com.yuntongxun.ecdemo.net.Net;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecdemo.ui.MainAct;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.contact.ContactLogic;
import com.yuntongxun.ecdemo.ui.contact.ECContacts;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;

import java.io.InvalidClassException;
import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class RegisterActivity extends ECSuperActivity implements View.OnClickListener {

    EditText etUsername;
    EditText etUserpwd;
    ImageView delet_image;
    TextView thorcode;
    ImageView mDeleteThorcode;
    EditText passwordEt;
    ImageView passwordDelIv;
    EditText sxNumEt;
    ImageView delSxNum;

    private ECProgressDialog mPostingdialog;
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
        ECApplication.photoUrl = "";
        registerReceiver(new String[]{SDKCoreHelper.ACTION_SDK_CONNECT});
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_MULTI_PERMISSION:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public int getTitleLayout() {
        return -1;
    }

    private void initViews() {
        etUsername = (EditText) findViewById(R.id.activity_login_et_username);
        etUserpwd = (EditText) findViewById(R.id.activity_login_et_password);
        delet_image = (ImageView) findViewById(R.id.delete_phoen_num);
        thorcode = (TextView) findViewById(R.id.getauthorcode);
        mDeleteThorcode = (ImageView) findViewById(R.id.delete_thcode);
        passwordEt = (EditText) findViewById(R.id.passwordEt);
        passwordDelIv = (ImageView) findViewById(R.id.delete_password);
        sxNumEt = (EditText) findViewById(R.id.sxNumEt);
        delSxNum = (ImageView) findViewById(R.id.delSxNum);
        mHandler2 = new Handler();
        findViewById(R.id.delete_phoen_num).setOnClickListener(this);
        findViewById(R.id.delete_thcode).setOnClickListener(this);
        findViewById(R.id.delete_password).setOnClickListener(this);
        findViewById(R.id.delSxNum).setOnClickListener(this);
        findViewById(R.id.registerTv).setOnClickListener(this);
        findViewById(R.id.getauthorcode).setOnClickListener(this);
        findViewById(R.id.goToLoginTv).setOnClickListener(this);
        findViewById(R.id.forgetPwdTv).setOnClickListener(this);
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
        sxNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                delSxNum.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    delSxNum.setVisibility(View.INVISIBLE);
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
        thorcode.setTextColor(getResources().getColor(R.color.colorPrimary));
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


    public void register(final String sxNum, final String phone, final String pwd, String code) {
        if (TextUtils.isEmpty(sxNum)) {
            Toast.makeText(this, "守信号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sxNum.length() < 6) {
            Toast.makeText(this, "守信号不能小于6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
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
        Net.register(sxNum, phone, pwd, code, new BaseObserver<RegisterBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RegisterBean registerBean) {
                if (registerBean.getStatusCode() == 0) {
                    /*用守信号登录*/
                    loginScueess(sxNum, pwd);
                } else {
                    ToastUtil.showMessage(registerBean.getStatusMsg());
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
        Net.getVerifyMsg(phone, "R", new BaseObserver<VerifyCodeBean>() {
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
        return etUsername.getText().toString();
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
            case R.id.registerTv://注册
                String name = getPhoneNumberFormat();
                String code = etUserpwd.getText().toString();
                String pwd = passwordEt.getText().toString().trim();
                String sxNum = sxNumEt.getText().toString().trim();
                register(sxNum, name, pwd, code);
                break;
            case R.id.delSxNum:
                sxNumEt.setText("");
                sxNumEt.requestFocus();
                break;
            case R.id.getauthorcode://请求验证阿妈
                thorcode.setEnabled(false);
                requestCode(getPhoneNumberFormat());
                break;
            case R.id.goToLoginTv://回到登录页面
                finish();
                break;
            case R.id.forgetPwdTv:  //跳转忘记密码
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x2a) {
            doLauncherAction();
        }
    }

    private void loginScueess(String phone, String pwd) {
        String appkey = FileAccessor.getAppKey();
        String token = FileAccessor.getAppToken();
        ClientUser clientUser = new ClientUser(phone);
        clientUser.setAppKey(appkey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(pwd);
        CCPAppManager.setClientUser(clientUser);

        mPostingdialog = new ECProgressDialog(this, R.string.login_posting);
        mPostingdialog.show();

        SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        // super.handleReceiver(context, intent);
        int error = intent.getIntExtra("error", -1);
        if (SDKCoreHelper.ACTION_SDK_CONNECT.equals(intent.getAction())) {
            // 初始注册结果，成功或者失败
            if (SDKCoreHelper.getConnectState() == ECDevice.ECConnectState.CONNECT_SUCCESS
                    && error == SdkErrorCode.REQUEST_SUCCESS) {
                try {
                    saveAccount();
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
                ContactsCache.getInstance().load();
                doLauncherAction();
                return;
            }
            if (intent.hasExtra("error")) {
                if (SdkErrorCode.CONNECTTING == error) {
                    return;
                }
                if (error == -1) {
                    ToastUtil.showMessage("请检查登陆参数是否正确[" + error + "]");
                }

                if (error == 171139) {
                    ToastUtil.showMessage("登录失败，当前无网络,请检查");
                } else if (error == 520019 || error == 520021) {
                    ToastUtil.showMessage("登录失败，请检查账号及密码");
                } else {
                    ToastUtil.showMessage("登录失败，请稍后重试[" + error + "]");
                }
            }
        }
        dismissPostingDialog();
    }

    /**
     * 关闭对话框
     */
    private void dismissPostingDialog() {
        if (mPostingdialog == null || !mPostingdialog.isShowing()) {
            return;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }

    private void saveAccount() throws InvalidClassException {
        String appkey = FileAccessor.getAppKey();
        String token = FileAccessor.getAppToken();
        String mobile = sxNumEt.getText().toString().trim();
        String voippass = passwordEt.getText().toString();

        ClientUser user = CCPAppManager.getClientUser();
        if (user == null) {
            user = new ClientUser(mobile);
        } else {
            user.setUserId(mobile);
        }
        user.setAppToken(token);
        user.setAppKey(appkey);
        user.setPassword(voippass);
        user.setLoginAuthType(mLoginAuthType);
        CCPAppManager.setClientUser(user);
        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REGIST_AUTO, user.toString(), true);
        // ContactSqlManager.insertContacts(contacts);
        ArrayList<ECContacts> objects = ContactLogic.initContacts();
        objects = ContactLogic.converContacts(objects);
        ContactSqlManager.insertContacts(objects);
    }

    private void doLauncherAction() {
        try {
            Intent intent = new Intent(this, MainAct.class);
            intent.putExtra("launcher_from", 1);
            // 注册成功跳转
            startActivity(intent);
            AppManager.getAppManager().finishActivity(LoginActivity.class);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
