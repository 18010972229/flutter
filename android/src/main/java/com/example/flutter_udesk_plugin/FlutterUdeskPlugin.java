package com.example.flutter_udesk_plugin;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Printer;

import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import udesk.core.UdeskConst;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/** FlutterUdeskPlugin */
public class FlutterUdeskPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;

  private  static  String cellphone;
  private  static  String nickName;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_udesk_plugin");
    context = flutterPluginBinding.getApplicationContext();
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_udesk_plugin");
    channel.setMethodCallHandler(new FlutterUdeskPlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("registerUDesk")) {

      String domain = call.argument("domainUrl");
      String appKey = call.argument("appKey");
      String appId = call.argument("appId");
      initApiKey(domain, appKey, appId);

    }else if (call.method.equals("jumpUDeskView")) {

      Map<String, String> info = new  HashMap<String, String>();

      String uuid = DeviceIdUtil.getDeviceUUID();
      info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, uuid);

      if (!cellphone.isEmpty()){
        info.put(UdeskConst.UdeskUserInfo.CELLPHONE, cellphone);
      }
      if (!nickName.isEmpty()) {
        info.put(UdeskConst.UdeskUserInfo.NICK_NAME, nickName);
      }

      UdeskConfig.Builder builder = new UdeskConfig.Builder();
      builder.setDefaultUserInfo(info);
      UdeskSDKManager.getInstance().entryChat(context, builder.build(), uuid);

    }else if (call.method.equals("setUdeskUserInfo")) {

      String phone = call.argument("cellphone");
      String nickname = call.argument("nickName");
      cellphone = phone;
      nickName = nickname;

    } else {
      result.notImplemented();
    }
  }
  // 初始化UDesk
  public void initApiKey(String domain, String appKey, String appId) {

    UdeskSDKManager manager = UdeskSDKManager.getInstance();
    manager.initApiKey(context, domain, appKey, appId);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
