import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterUdeskPlugin {

  // 有客服新消息通知的回调
   ValueChanged<String> _callBack;


   set callBack(ValueChanged<String> value) {
    _callBack = value;
  }

  static const MethodChannel _channel =
      const MethodChannel('flutter_udesk_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // 注册UDesk
  Future registerUDesk (String domainUrl, String appKey, String appId) async {

    // 设置处理原生调用Flutter方法
    handlerChanleMethod();

    _channel.invokeMethod('registerUDesk', {
      'domainUrl': domainUrl,
      'appKey': appKey,
      'appId': appId
    });

  }

  // 设置udesk客户信息
  Future setUdeskUserInfo(@required String cellphone, @required String nickName) async {
    await _channel.invokeMethod('setUdeskUserInfo', {'cellphone': cellphone, 'nickName': nickName});
  }

  /// 设置处理原生调用Flutter方法
  Future handlerChanleMethod() async {

    Future<dynamic> platformCallHandler(MethodCall call) async {

      if (call.method == 'showDeskUnReadCount' && call.arguments != null) {
        var unreadCount = call.arguments['unreadCount'];
        if (_callBack != null) {
          _callBack(unreadCount.toString());
        }
      }
    }
   await _channel.setMethodCallHandler(platformCallHandler);
  }

  // 跳转到原生客服页面
  Future jumpUDeskView () async {
    await _channel.invokeMethod('jumpUDeskView');
  }
}
