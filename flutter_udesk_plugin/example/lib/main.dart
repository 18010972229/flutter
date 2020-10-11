import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_udesk_plugin/flutter_udesk_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String count = '0';

  FlutterUdeskPlugin _flutterUdeskPlugin = FlutterUdeskPlugin();

  @override
  void initState() {
    super.initState();
    initPlatformState().then((value) {
      _flutterUdeskPlugin.registerUDesk('1560469.s2.udesk.cn', 'f0e82d7ab70a7eb6b69d8485658bea7a', 'f9f5bbce629b43f9');
    });
    _flutterUdeskPlugin.callBack = (value){
      setState(() {
        count = value;
      });
    };

  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterUdeskPlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform


    // setState to update our non-existent appearance.
    if (!mounted) return;


    setState(() {
      _platformVersion = platformVersion;
    });
  }


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(



          title: Text('Plugin example app--${count}'),
        ),
        body: Center(
          child: FlatButton(
            child: Text('跳转到客服----$_platformVersion'),
            onPressed: (){
              _flutterUdeskPlugin.setUdeskUserInfo('19957898592', '月交子测试人员');
              _flutterUdeskPlugin.jumpUDeskView();
            },
          ),
        ),
      ),
    );
  }
}
