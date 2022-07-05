
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#endif

#import <BoltMobileSDK/BMSSwiperController.h>

@interface RNCardConnectReactLibrary : RCTEventEmitter <RCTBridgeModule, BMSSwiperControllerDelegate>

@property (nonatomic, strong) BMSSwiperController *swiper;
@property (nonatomic) Boolean isConnecting;
@property (nonatomic, copy) void(^restartReaderBlock)(void);

@end
  
