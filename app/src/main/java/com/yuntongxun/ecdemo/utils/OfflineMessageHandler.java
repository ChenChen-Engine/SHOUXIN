package com.yuntongxun.ecdemo.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.DemoUtils;
import com.yuntongxun.ecdemo.common.utils.ECNotificationManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.common.utils.SharedPreferencesUtils;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.storage.FriendMessageSqlManager;
import com.yuntongxun.ecdemo.storage.GroupNoticeSqlManager;
import com.yuntongxun.ecdemo.storage.IMessageSqlManager;
import com.yuntongxun.ecdemo.ui.SDKCoreHelper;
import com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper;
import com.yuntongxun.ecdemo.ui.contact.ECContacts;
import com.yuntongxun.ecdemo.ui.group.DemoGroupNotice;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECPreviewMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECUserStateMessageBody;
import com.yuntongxun.ecsdk.im.ECVideoMessageBody;
import com.yuntongxun.ecsdk.im.friend.ECFriendMessageBody;
import com.yuntongxun.ecsdk.im.friend.FriendInner;

import java.io.File;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import io.vov.vitamio.utils.Log;

import static com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper.INTENT_ACTION_CHAT_USER_STATE;
import static com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper.USER_STATE;
import static com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper.checkNeedNotification;
import static com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper.mOnMessageReportCallback;

public class OfflineMessageHandler {
    private static final String TAG = "IMChattingHelper";
    /**
     * 处理接收消息
     *
     * @param msg
     * @param showNotice
     */
    public static synchronized void postReceiveMessage(ECMessage msg,
                                                 boolean showNotice) {
        // 接收到的IM消息，根据IM消息类型做不同的处理
        // IM消息类型：ECMessage.Type

        if("~ytxfa".equalsIgnoreCase(msg.getForm())){
            msg.setDirection(ECMessage.Direction.SEND);
        }


        if (msg.getType() == ECMessage.Type.FRIEND) {//好友相关
//            Intent intent = new Intent(SDKCoreHelper.ACTION_SDK_FRIENDNUM);
//            CCPAppManager.getContext().sendBroadcast(intent);

            int num = (int) SharedPreferencesUtils.getParam(CCPAppManager.getContext(), SharedPreferencesUtils.FRIEND_NUM, -1);
            if (num > 0) {
                num++;
                SharedPreferencesUtils.setParam(CCPAppManager.getContext(), SharedPreferencesUtils.FRIEND_NUM, num);
            } else {
                SharedPreferencesUtils.setParam(CCPAppManager.getContext(), SharedPreferencesUtils.FRIEND_NUM, 1);
            }
            ECFriendMessageBody body = (ECFriendMessageBody) msg.getBody();

            if (body != null) {
                FriendInner inner = body.getInner();
                String url = inner.getAvatar();
                String userId = msg.getForm();
                String nick = msg.getNickName();
                String content = inner.getMsg();
                String to = msg.getTo();

                String markSelf = TextUtils.isEmpty(nick) ? to : nick;//用户昵称》账号

                String markSOther = TextUtils.isEmpty(nick) ? userId : nick;//用户昵称》账号


                ECFriendMessageBody.FriendNoticeType type = inner.getSubType();
                if (type == ECFriendMessageBody.FriendNoticeType.IS_FRIENDLY) {

                    if (userId.equalsIgnoreCase(CCPAppManager.getUserId())) {
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您与" + markSelf + "已成为好友",
                                        "好友添加通知", null,
                                        msg.getType().ordinal());
                        FriendMessageSqlManager.insertFriendByUserId(to, markSelf, "1", url);


                        insertFriendToGroupNotice("好友添加通知", "您与" + markSelf + "已成为好友", to);
                    } else {
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您与" + markSOther + "已成为好友",
                                        "好友添加通知", null,
                                        msg.getType().ordinal());
                        FriendMessageSqlManager.insertFriendByUserId(userId, markSOther, "1", url);

                        insertFriendToGroupNotice("好友添加通知", "您与" + markSOther + "已成为好友", userId);
                    }

                } else if (type == ECFriendMessageBody.FriendNoticeType.DELETE_FRIEND) {
                    if (userId.equalsIgnoreCase(CCPAppManager.getUserId())) {

                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您已与" + markSelf + "解除好友关系",
                                        "好友删除通知", null,
                                        msg.getType().ordinal());

                        FriendMessageSqlManager.delFriend(to);

                        insertFriendToGroupNotice("好友删除通知", "您已与" + markSelf + "解除好友关系", to);
                    } else {
                        FriendMessageSqlManager.delFriend(userId);
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您已与" + markSOther + "解除好友关系",
                                        "好友删除通知", null,
                                        msg.getType().ordinal());

                        insertFriendToGroupNotice("好友删除通知", "您已与" + markSOther + "解除好友关系", userId);
                    }
                } else if (type == ECFriendMessageBody.FriendNoticeType.ADD_FRIEND) {

                    if (!CCPAppManager.getUserId().equalsIgnoreCase(userId)) {
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), markSOther + "请求添加您为好友",
                                        "好友添加通知", null,
                                        msg.getType().ordinal());
                        FriendMessageSqlManager.insertFriendByUserId(userId, markSOther, "0", url);

                        insertFriendToGroupNotice("好友添加通知", markSOther + "请求添加您为好友", userId);
                    }
                } else if (type == ECFriendMessageBody.FriendNoticeType.AGREE_ADD_FRIEND) {

                    if (!CCPAppManager.getUserId().equalsIgnoreCase(userId)) {
                        FriendMessageSqlManager.insertFriendByUserId(userId, nick, "1", url);
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您与" + markSOther + "成为好友",
                                        "好友同意通知", null,
                                        msg.getType().ordinal());

                        insertFriendToGroupNotice("好友同意通知", "您与" + markSOther + "成为好友", userId);

                    } else {
                        ECNotificationManager.getInstance()
                                .showFriendNotification(
                                        CCPAppManager.getContext(), "您与" + markSelf + "成为好友",
                                        "好友同意通知", null,
                                        msg.getType().ordinal());

                        insertFriendToGroupNotice("好友同意通知", "您与" + markSelf + "成为好友", to);
                    }
                }
            }

            return;
        }

        if (msg.getType() == ECMessage.Type.STATE) { //状态消息
            String msgTo = msg.getTo();
            if (isGroup(msg)) {
                return;
            }
//            ECUserStateMessageBody stateBody = (ECUserStateMessageBody) msg.getBody();
//            String state = stateBody.getMessage();
//            Intent intent = new Intent();
//            intent.putExtra(USER_STATE, state);
//            intent.setAction(INTENT_ACTION_CHAT_USER_STATE);
//            CCPAppManager.getContext().sendBroadcast(intent);

            return;
        }
        if (msg.isMultimediaBody()) {
            {
                if (msg.getType() != ECMessage.Type.CALL) {
                    ECFileMessageBody body = (ECFileMessageBody) msg.getBody();
                    FileAccessor.initFileAccess();
                    if (!TextUtils.isEmpty(body.getRemoteUrl())) {
                        boolean thumbnail = false;
                        String fileExt = DemoUtils
                                .getExtensionName(body.getRemoteUrl());
                        if (msg.getType() == ECMessage.Type.VOICE) {
                            body.setLocalUrl(new File(FileAccessor.getVoicePathName(),
                                    DemoUtils.md5(String.valueOf(System
                                            .currentTimeMillis())) + ".amr")
                                    .getAbsolutePath());
                        } else if (msg.getType() == ECMessage.Type.IMAGE) {
                            ECImageMessageBody imageBody = (ECImageMessageBody) body;

                            Log.e("HD----", imageBody.gethDImageURL());
                            Log.e("HDs----", imageBody.isHPicture() + "");
                            thumbnail = !TextUtils.isEmpty(imageBody
                                    .getThumbnailFileUrl());
                            imageBody.setLocalUrl(new File(FileAccessor
                                    .getImagePathName(), DemoUtils
                                    .md5(thumbnail ? imageBody.getThumbnailFileUrl()
                                            : imageBody.getRemoteUrl())
                                    + "." + fileExt).getAbsolutePath());
                        } else if (msg.getType() == ECMessage.Type.RICH_TEXT) {

                            ECPreviewMessageBody previewMessageBody = (ECPreviewMessageBody) body;
                            thumbnail = !TextUtils.isEmpty(previewMessageBody
                                    .getThumbnailFileUrl());

                            previewMessageBody.setLocalUrl(FileAccessor.IMESSAGE_RICH_TEXT + "/" + previewMessageBody.getFileName());


                        } else {
                            if (msg.getBody() instanceof ECVideoMessageBody) {
                                ECVideoMessageBody videoBody = (ECVideoMessageBody) body;

                                thumbnail = !TextUtils.isEmpty(videoBody
                                        .getThumbnailUrl());
                                StringBuilder builder = new StringBuilder(
                                        videoBody.getFileName());
                                builder.append("_thum.png");
                                body.setLocalUrl(new File(FileAccessor
                                        .getFilePathName(), builder.toString())
                                        .getAbsolutePath());

                            } else {
                                body.setLocalUrl(new File(FileAccessor
                                        .getFilePathName(), DemoUtils.md5(String
                                        .valueOf(System.currentTimeMillis()))
                                        + "."
                                        + fileExt).getAbsolutePath());
                            }
                        }
                        if (IMChattingHelper.syncMessage != null) {
                            IMChattingHelper.syncMessage.put(msg.getMsgId(), new IMChattingHelper.SyncMsgEntry(showNotice, thumbnail, msg));
                        }
                        if (SDKCoreHelper.getECChatManager() != null) {
                            if (thumbnail) {
                                SDKCoreHelper.getECChatManager().downloadThumbnailMessage(msg, IMChattingHelper.getInstance());
                            } else {
                                SDKCoreHelper.getECChatManager().downloadMediaMessage(msg, IMChattingHelper.getInstance());
                            }
                        }
                        if (TextUtils.isEmpty(body.getFileName())
                                && !TextUtils.isEmpty(body.getRemoteUrl())) {
                            body.setFileName(FileAccessor.getFileName(body
                                    .getRemoteUrl()));
                        }
                        if (msg.getType() == ECMessage.Type.IMAGE
                                && msg.getDirection() == ECMessage.Direction.RECEIVE) {
                            msg.setUserData(msg.getUserData());
                        } else {
                            msg.setUserData("fileName=" + body.getFileName());
                        }
                    }
                    if (IMessageSqlManager.insertIMessage(msg, msg.getDirection()
                            .ordinal()) > 0) {
                        return;

                    }
                } else {
                    LogUtil.e(TAG, "ECMessage fileUrl: null");
                }
            }
        } else {
            if (msg.getType() == ECMessage.Type.TXT && msg.getSessionId().toUpperCase().startsWith("G")) {//群组
                ECTextMessageBody body = (ECTextMessageBody) msg.getBody();
                if (body != null && body.isAt()) {//@消息
                    try {
                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_AT, msg.getSessionId()+CCPAppManager.getUserId(), true);
                    } catch (InvalidClassException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (IMessageSqlManager
                .insertIMessage(msg, msg.getDirection().ordinal()) <= 0) {
            return;
        }

        if (mOnMessageReportCallback != null) {
            ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
            msgs.add(msg);
            mOnMessageReportCallback.onPushMessage(msg.getSessionId(), msgs);
        }

        // 是否状态栏提示
        if (showNotice) {
            showNotification(msg);
        }
    }

    private static void showNotification(ECMessage msg) {
        if (checkNeedNotification(msg.getSessionId())) {
            ECNotificationManager.getInstance().forceCancelNotification();
            String lastMsg = "";
            if (msg.getType() == ECMessage.Type.TXT) {
                lastMsg = ((ECTextMessageBody) msg.getBody()).getMessage();
            }
            ECContacts contact = ContactSqlManager.getContact(msg.getForm());
            if (contact == null) {
                return;
            }
            ECNotificationManager.getInstance()
                    .showCustomNewMessageNotification(
                            CCPAppManager.getContext(), lastMsg,
                            contact.getNickname(), msg.getSessionId(),
                            msg.getType().ordinal());
        }
    }

    private static void insertFriendToGroupNotice(String nameTitle, String content, String member) {
        DemoGroupNotice demoGroupNotice = new DemoGroupNotice();
        demoGroupNotice.setGroupName(nameTitle);
        demoGroupNotice.setContent(content);
        demoGroupNotice.setMember(member);
        GroupNoticeSqlManager.insertNoticeMsg(demoGroupNotice, member);
    }


    private static boolean isGroup(ECMessage msg) {
        if (msg == null || TextUtils.isEmpty(msg.getTo())) {
            return false;
        }

        return msg.getTo().startsWith("g") || msg.getTo().startsWith("G");
    }

    public static void sendBroad(Map<String,ECMessage> map){
        //好友更新
        Intent intent = new Intent(SDKCoreHelper.ACTION_SDK_FRIENDNUM);
        CCPAppManager.getContext().sendBroadcast(intent);
        //群更新
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                ECMessage msg = map.get(key);
                ECUserStateMessageBody stateBody = (ECUserStateMessageBody) msg.getBody();
                String state = stateBody.getMessage();
                Intent groupIntent = new Intent();
                intent.putExtra(USER_STATE, state);
                intent.setAction(INTENT_ACTION_CHAT_USER_STATE);
                CCPAppManager.getContext().sendBroadcast(groupIntent);
            }
        }
    }

}
