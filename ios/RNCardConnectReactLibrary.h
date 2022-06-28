
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#endif

typedef void (^ActivateBlock)();

#import <BoltMobileSDK/BMSSwiperController.h>
// #import <BoltMobileSDK/BMSSwiperControllerDelegate.h>

@interface RNCardConnectReactLibrary : RCTEventEmitter <RCTBridgeModule, BMSSwiperControllerDelegate>

@property (nonatomic, strong) BMSSwiperController *swiper;

@property (nonatomic) Boolean isConnecting;

@property (readwrite, copy) ActivateBlock activate;

@end
  
