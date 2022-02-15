package com.ice.good.lib.ability.push;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ice.good.lib.ability.BuildConfig;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;


/**
 * PushSDK集成帮助类
 */
public class UMengPushHelper {

    private static final String TAG = "PushHelper";
    private static IPushMessageHandler iPushMessageHandler;

    /**
     * 预初始化
     */
    public static void preInit(Context context) {
        //解决厂商通知点击时乱码等问题
        PushAgent.setup(context, BuildConfig.UMENG_API_KEY, BuildConfig.UMENG_MESSAGE_SECRET);
        UMConfigure.preInit(context, BuildConfig.UMENG_API_KEY, BuildConfig.CHANNEL);
    }

    /**
     * 初始化
     */
    public static void init(final Application context, IPushMessageHandler iPushMessageHandler) {
        // 基础组件包提供的初始化函数，应用配置信息：http://message.umeng.com/list/apps
        // 参数一：上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：发布渠道名称；
        // 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
        // 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息
        UMConfigure.init(context, BuildConfig.UMENG_API_KEY, BuildConfig.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE, BuildConfig.UMENG_MESSAGE_SECRET);

        UMengPushHelper.iPushMessageHandler = iPushMessageHandler;

        //获取推送实例
        PushAgent pushAgent = PushAgent.getInstance(context);

        //TODO:需修改为您app/src/main/AndroidManifest.xml中package值
        pushAgent.setResourcePackageName("com.ice.good.lib.lib.restful.demo");

        //推送设置
        pushSetting(context, iPushMessageHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "deviceToken --> " + deviceToken);
                iPushMessageHandler.onRegisterSuccess(deviceToken);
//                //获取deviceToken可通过接口：
//                PushAgent.getInstance(context).getRegistrationId();
//                //可设置别名，推送时使用别名推送
//                String alias = "123456";
//                String type = "aa";
//                PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
//                    @Override
//                    public void onMessage(boolean success, String message) {
//                        Log.i(TAG, "setAlias " + success + " msg:" + message);
//                    }
//                });
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                Log.e(TAG, "register failure：--> " + "code:" + errCode + ",desc:" + errDesc);
                iPushMessageHandler.onRegisterFailed(errCode, errDesc);
            }
        });
        registerDeviceChannel(context);
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     */
    private static void registerDeviceChannel(Context context) {
        //小米通道，填写您在小米后台APP对应的xiaomi id和key
        MiPushRegistar.register(context, "PushConstants.MI_ID", "PushConstants.MI_KEY");
//        //华为，注意华为通道的初始化参数在minifest中配置
//        HuaWeiRegister.register((Application) context.getApplicationContext());
//        //魅族，填写您在魅族后台APP对应的app id和key
//        MeizuRegister.register(context, PushConstants.MEI_ZU_ID, PushConstants.MEI_ZU_KEY);
//        //OPPO，填写您在OPPO后台APP对应的app key和secret
//        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
//        //vivo，注意vivo通道的初始化参数在minifest中配置
//        VivoRegister.register(context);
    }

    //推送设置
    private static void pushSetting(Application context, IPushMessageHandler iPushMessageHandler) {
        PushAgent pushAgent = PushAgent.getInstance(context);

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.setDisplayNotificationNumber(0);

        //推送消息处理
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            //处理通知栏消息
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                if(iPushMessageHandler!=null){
                    //自己处理消息
                    iPushMessageHandler.dealWithNotificationMessage(context, new JSONObject());
                }else{
                    super.dealWithNotificationMessage(context, msg);
                }
                Log.i(TAG, "notification receiver:" + msg.getRaw().toString());
            }

            //自定义通知样式，此方法可以修改通知样式等
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                return super.getNotification(context, msg);
            }

            //处理透传消息
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                if(iPushMessageHandler!=null){
                    //自己处理消息
                    iPushMessageHandler.dealWithCustomMessage(context, new JSONObject());
                }else{
                    super.dealWithCustomMessage(context, msg);
                }
                Log.i(TAG, "custom receiver:" + msg.getRaw().toString());
            }
        };
        pushAgent.setMessageHandler(msgHandler);

        //推送消息点击处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage uMessage) {
                if(iPushMessageHandler!=null){
                    //自己处理消息
                    iPushMessageHandler.dealWithCustomAction(context, new JSONObject());
                }else{
                    super.dealWithCustomAction(context, uMessage);
                }

            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                Log.i(TAG, "click openActivity: " + msg.getRaw().toString());
            }

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Log.i(TAG, "click launchApp: " + msg.getRaw().toString());
            }

            @Override
            public void dismissNotification(Context context, UMessage msg) {
                super.dismissNotification(context, msg);
                Log.i(TAG, "click dismissNotification: " + msg.getRaw().toString());
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        context.registerActivityLifecycleCallbacks(new SimpleLifecycleCallbacks(){
            @Override
            public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                PushAgent.getInstance(activity.getApplication()).onAppStart();
            }
        });
    }

    public static void onOEMPush(String message){
        if(iPushMessageHandler!=null){
            iPushMessageHandler.dealWithCustomMessage(null, new JSONObject());
        }
    }

}
