#import "FlutterUdeskPlugin.h"

static FlutterMethodChannel *channels;

@implementation FlutterUdeskPlugin

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_udesk_plugin"
            binaryMessenger:[registrar messenger]];
    channels = channel;
  FlutterUdeskPlugin* instance = [[FlutterUdeskPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}
// 处理Flutter调用ios方法
- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"registerUDesk" isEqualToString:call.method]) {

    NSDictionary *params = (NSDictionary *)call.arguments;
    NSString *domain = [params valueForKey:@"domainUrl"];
    NSString *appKey = [params valueForKey:@"appKey"];
    NSString *appId = [params valueForKey:@"appId"];
    [self registerUDeskWithDomain:domain appKey:appKey appId:appId];
      
  }else if ([@"jumpUDeskView" isEqualToString:call.method]) {
    
      [self jumpUDeskView];
      
  }else if ([@"setUdeskUserInfo" isEqualToString:call.method]) {
      
      NSString *cellphone = [call.arguments valueForKey:@"cellphone"];
      NSString *nickName = [call.arguments valueForKey:@"nickName"];
      [self setUdeskUserInfo:cellphone nickName:nickName];
      
  }
  else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)registerUDeskWithDomain:(NSString *)domian appKey:(NSString *)appKey appId:(NSString *)appId {
    //初始化公司（appKey、appID、domain都是必传字段）
    UdeskOrganization *organization = [[UdeskOrganization alloc] initWithDomain:domian appKey:appKey appId:appId];

    UdeskCustomer *customer = [UdeskCustomer new];
    customer.sdkToken = [self deviceId];
    
    [FlutterUdeskPluginCustomer sharedInstance].udeskCustomer = customer;
    
    //初始化sdk
    [UdeskManager initWithOrganization:organization customer:customer];
    
    __weak typeof(self) weakSelf = self;
    //根据后台配置
    [UdeskManager fetchSDKSetting:^(UdeskSetting *setting) {
        [UdeskManager LeaveSDKPage];
        //延迟获取sdk存在db的未读消息
         dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
             
             NSLog(@"未读消息数：%ld",[UdeskManager getLocalUnreadeMessagesCount]);
             NSInteger unreadUDeskCount = [UdeskManager getLocalUnreadeMessagesCount];
             [weakSelf handlerUnReadUDeskCount:unreadUDeskCount];
         });
    } failure:^(NSError *error) {
        NSLog(@"UdeskSDK：%@",error);
    }];
    
    [[NSNotificationCenter defaultCenter] addObserverForName:UD_RECEIVED_NEW_MESSAGES_NOTIFICATION object:nil queue:[NSOperationQueue mainQueue] usingBlock:^(NSNotification * _Nonnull note) {
      //延迟获取sdk存在db的未读消息
       dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
           
           NSLog(@"未读消息数：%ld",[UdeskManager getLocalUnreadeMessagesCount]);
           NSInteger unreadUDeskCount = [UdeskManager getLocalUnreadeMessagesCount];
           [weakSelf handlerUnReadUDeskCount:unreadUDeskCount];
       });
    }];
}
// 设置登录后用户信息
- (void)setUdeskUserInfo:(NSString *)cellphone nickName:(NSString *)nickName {
    
    [FlutterUdeskPluginCustomer sharedInstance].udeskCustomer.cellphone = cellphone;
    [FlutterUdeskPluginCustomer sharedInstance].udeskCustomer.nickName = nickName;
}

- (void)handlerUnReadUDeskCount:(NSInteger)unreadUDeskCount {
    [channels invokeMethod:@"showDeskUnReadCount" arguments:@{@"unreadCount": @(unreadUDeskCount)} result:nil];
}

- (void)jumpUDeskView {

    UdeskSDKConfig *sdkConfig = [UdeskSDKConfig customConfig];
    UdeskSDKManager *sdkManager = [[UdeskSDKManager alloc] initWithSDKStyle:[UdeskSDKStyle customStyle] sdkConfig:sdkConfig];
    
    __weak typeof(self) weakSelf = self;
    UIViewController *viewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    [sdkManager pushUdeskInViewController:viewController completion:^{
        [weakSelf handlerUnReadUDeskCount:0];
    }];
}


NSString * const YJZ_UUID_KEY = @"YJZ_UUID_KEY";
- (NSString *)deviceId {
    NSString * uuid  = [YJZKeychain loadServiceKey:YJZ_UUID_KEY];
    if ([uuid isEqualToString:@"00000000-0000-0000-0000-000000000000"]) {
        uuid = nil;
    }
    if (uuid.length) {
        return uuid;
    }

    uuid = [[NSUUID UUID] UUIDString];
    [YJZKeychain saveServiceKey:YJZ_UUID_KEY data:uuid];

    return uuid;
}

@end





@implementation FlutterUdeskPluginCustomer

static FlutterUdeskPluginCustomer *_sharedInstance = nil;

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        // because has rewrited allocWithZone  use NULL avoid endless loop .
        _sharedInstance = [[super allocWithZone:NULL] init];
    });
    return _sharedInstance;
}

+ (instancetype)allocWithZone:(struct _NSZone *)zone {
    return [FlutterUdeskPluginCustomer sharedInstance];
}

+ (instancetype)alloc {
    return [FlutterUdeskPluginCustomer sharedInstance];
}

- (id)copy{
    return self;
}

- (id)mutableCopy {
    return self;
}

- (id)copyWithZone:(struct _NSZone *)zone
{
    return self;
}


@end
