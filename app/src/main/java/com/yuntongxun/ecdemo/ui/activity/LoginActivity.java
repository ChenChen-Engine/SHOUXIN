package com.yuntongxun.ecdemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.bean.LoginBean;
import com.yuntongxun.ecdemo.common.CCPAppManager;
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

/**
 * 登陆
 */
public class LoginActivity extends ECSuperActivity implements View.OnClickListener {
    EditText etUsername;
    EditText etUserpwd;
    ImageView delet_image;
    ImageView mDeleteThorcode;
    ImageView passwordDelIv;
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
    LoginBean.DataBean databean ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
        ECApplication.photoUrl = "";
        registerReceiver(new String[]{SDKCoreHelper.ACTION_SDK_CONNECT});
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    public int getTitleLayout() {
        return -1;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private void initViews() {
        etUsername = (EditText) findViewById(R.id.activity_login_et_username);
        etUserpwd = (EditText) findViewById(R.id.activity_login_et_password);
        delet_image = (ImageView) findViewById(R.id.delete_phoen_num);
        mDeleteThorcode = (ImageView) findViewById(R.id.delete_thcode);
        passwordDelIv = (ImageView) findViewById(R.id.delete_password);

        findViewById(R.id.delete_phoen_num).setOnClickListener(this);
        findViewById(R.id.delete_thcode).setOnClickListener(this);
        findViewById(R.id.loginBtn).setOnClickListener(this);
        findViewById(R.id.goToRegisterTv).setOnClickListener(this);
        findViewById(R.id.forgetPwdTv).setOnClickListener(this);

        initListener();
    }

    @Override
    protected void onDestroy() {
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


    /**
     * 获取格式化之后的电话号码，未格式化之前有空格
     *
     * @return
     */
    private String getPhoneNumberFormat() {
        return etUsername.getText().toString();
    }

    private long clickTime = 0;

    @Override
    public void onClick(View v) {
        long cureentTime = System.currentTimeMillis();
        if (cureentTime >= clickTime + 500) {
            switch (v.getId()) {
                case R.id.delete_phoen_num://删除电话
                    etUsername.setText("");
                    etUsername.requestFocus();
                    break;
                case R.id.delete_thcode://删除验证码
                    etUserpwd.setText("");
                    etUserpwd.requestFocus();
                    break;
                case R.id.loginBtn://注册
                    String name = getPhoneNumberFormat();
                    String pwd = etUserpwd.getText().toString();
                    login(name, pwd);
                    break;
                case R.id.getauthorcode://请求验证码

                    break;
                case R.id.goToRegisterTv://跳转注册
                    startActivity(new Intent(this, RegisterActivity.class));
                    break;
                case R.id.forgetPwdTv:  //跳转忘记密码
                    startActivity(new Intent(this, ForgetPasswordActivity.class));
                    break;
            }
            clickTime = cureentTime;
        }
    }

    private long backTime = 0;

    @Override
    public void onBackPressed() {
        long cureentTime = System.currentTimeMillis();
        if (cureentTime <= backTime + 1000) {
            super.onBackPressed();
            AppManager.getAppManager().finishAllActivity();
        } else {
            backTime = cureentTime;
            ToastUtil.showMessage("再点一次退出应用");
        }
    }

    public void login(final String phone, final String pwd) {
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

        Net.login(phone, pwd, new BaseObserver<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean bean) {
                if (bean != null && bean.getData() != null) {
                    databean = bean.getData();
                    loginScueess(bean.getData().getShouxin(), pwd);
                } else {
                    ToastUtil.showMessage(bean.getStatusMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showMessage(e.getMessage());
            }
        });
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
    }


    private void saveAccount() throws InvalidClassException {
        if(databean == null){
            return;
        }
        String appkey = FileAccessor.getAppKey();
        String token = FileAccessor.getAppToken();
        String mobile = databean.getShouxin();
        String voippass = etUserpwd.getText().toString();

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

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
