package com.yuntongxun.ecdemo.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.bean.CheckRegisterBean;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.common.view.SearchEditText;
import com.yuntongxun.ecdemo.common.view.TitleBar;
import com.yuntongxun.ecdemo.net.BaseObserver;
import com.yuntongxun.ecdemo.net.Net;
import com.yuntongxun.ecdemo.ui.BaseActivity;
import com.yuntongxun.ecdemo.ui.contact.ContactDetailActivity;
import com.yuntongxun.ecdemo.ui.personcenter.FriendInfoUI;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.PersonInfo;
import com.yuntongxun.ecsdk.SdkErrorCode;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by luhuashan on 17/8/4.
 */

public class AddFriendActivity extends BaseActivity {


    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.searchView)
    SearchEditText mSearch;
    @BindView(R.id.rl_add_phone)
    RelativeLayout mRlAddContact;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.iv_phone_tag)
    ImageView mIvPhoneTag;
    @BindView(R.id.tv_phone_tag)
    TextView mTvPhoneTag;

    @Override
    protected void initView(Bundle savedInstanceState) {
        initTooleBar(titleBar, true, "添加朋友");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_friend;
    }

    @Override
    protected void initWidgetAciotns() {


        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //完成自己的事件
                    search(mSearch.getText().toString().trim());
                }
                return false;
            }
        });
    }

    /*首页添加好友搜索，群添加成员搜索都会到这里，用GroupID判断是群还是添加好友*/
    private void search(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            Net.checkRegister(keyword, new BaseObserver<CheckRegisterBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(CheckRegisterBean value) {
                    if (value.getIsRegister() == 0) {
                        ToastUtil.showMessage("该用户尚未注册！");
                        mRlAddContact.setVisibility(View.GONE);
                        mTvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        searchUser(value.getShouxin());
                    }
                }

                @Override
                public void onError(Throwable e) {

                }
            });
        }
    }

    private void searchUser(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            ECDevice.getPersonInfo(keyword, new ECDevice.OnGetPersonInfoListener() {
                @Override
                public void onGetPersonInfoComplete(ECError ecError, PersonInfo personInfo) {
                    if (ecError.errorCode == SdkErrorCode.REQUEST_SUCCESS && personInfo != null) {
                        Intent intent = new Intent(mContext, FriendInfoUI.class);
                        intent.putExtra(FriendInfoUI.MOBILE, personInfo.getUserId());
                        intent.putExtra(ContactDetailActivity.DISPLAY_NAME, personInfo.getNickName());
                        startActivity(intent);
                    } else {
                        mRlAddContact.setVisibility(View.GONE);
                        mTvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mRlAddContact.setVisibility(View.GONE);
            mTvEmpty.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTvEmpty.setVisibility(View.GONE);
    }

    @OnClick(R.id.rl_add_phone)
    public void onViewClicked() {
        startActivity(AddressBookListAct.class);
    }
}
