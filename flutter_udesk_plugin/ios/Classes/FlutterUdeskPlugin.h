#import <Flutter/Flutter.h>
#import "Udesk.h"
#import "YJZKeychain.h"

@interface FlutterUdeskPlugin : NSObject<FlutterPlugin>
@end

@interface FlutterUdeskPluginCustomer : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, strong) UdeskCustomer *udeskCustomer;

@end
