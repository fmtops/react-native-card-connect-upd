#import "RNCardConnectReactLibrary.h"
#import <BoltMobileSDK/BoltMobileSDK.h>
#import <BoltMobileSDK/BMSCardInfo.h>
#import <BoltMobileSDK/BMSAccount.h>
#import <BoltMobileSDK/BMSSwiperController.h>
#import <React/RCTLog.h>
#import <React/RCTConvert.h>

@implementation RNCardConnectReactLibrary

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE(BoltSDK)

RCT_EXPORT_METHOD(discoverDevice) {

    RCTLogInfo(@"discovering devices?!?!");

    self.swiper = [[BMSSwiperController alloc] initWithDelegate:self swiper:BMSSwiperTypeVP3300 loggingEnabled:YES];

    [self.swiper findDevices];
}

RCT_EXPORT_METHOD(setupConsumerApiEndpoint:(NSString *)endpoint) {
    [BMSAPI instance].endpoint = endpoint;
    [BMSAPI instance].enableLogging = true;
}

RCT_EXPORT_METHOD(getCardToken:(NSString *)cardNumber expirationDate:(NSString *)expirationDate CVV:(NSString *)CVV                   resolve: (RCTPromiseResolveBlock)resolve
rejecter:(RCTPromiseRejectBlock)reject)
{
    BMSCardInfo *card = [BMSCardInfo new];

    card.cardNumber = cardNumber;
    card.expirationDate = expirationDate;
    card.CVV = CVV;

    [[BMSAPI instance] generateAccountForCard:card completion:^(BMSAccount *account, NSError *error){
        if (account) {
            resolve(account.token);
        } else {
            reject(@"error", error.localizedDescription, error);
        }
    }];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"BoltDeviceFound",
        @"BoltOnTokenGenerated",
        @"BoltOnTokenGeneratedError",
        @"BoltOnSwiperConnected",
        @"BoltOnSwiperDisconnected",
        @"BoltOnSwiperReady",
        @"BoltOnSwipeError",
        @"BoltOnTokenGenerationStart",
        @"BoltOnRemoveCardRequested",
        @"BoltOnBatteryState",
        @"BoltOnLogUpdate",
        @"BoltOnDeviceConfigurationUpdate",
        @"BoltOnDeviceConfigurationProgressUpdate",
        @"BoltOnDeviceConfigurationUpdateComplete",
        @"BoltOnTimeout",
        @"BoltOnCardRemoved",
        @"BoltOnDeviceBusy"
    ];
}

- (void)swiper:(BMSSwiper *)swiper connectionStateHasChanged:(BMSSwiperConnectionState)state
{
    RCTLogInfo(@"swiper connectionStateHasChanged");

    // [self sendEventWithName:@"BoltOnSwiperReady"];

    // switch (state) {
    //     case BMSSwiperConnectionStateConnected:
    //         NSLog(@"Did Connect Swiper");
    //         self.swiperStatus.text = @"Connected";
    //         self.connectButton.enabled = NO;
    //         if (self.communicationAlert)
    //         {
    //             if (self.presentedViewController == self.communicationAlert)
    //             {
    //                 [self.communicationAlert dismissViewControllerAnimated:YES completion:^{
    //                     self.communicationAlert = nil;
    //                 }];
    //             }
    //             else
    //             {
    //                 self.communicationAlert = nil;
    //             }
    //         }
    //         break;
    //     case BMSSwiperConnectionStateDisconnected:
    //         NSLog(@"Did Disconnect Swiper");
    //         self.swiperStatus.text = @"Disconnected";
    //         self.connectButton.enabled = YES;
    //         if (self.communicationAlert)
    //         {
    //             if (self.presentedViewController == self.communicationAlert)
    //             {
    //                 [self.communicationAlert dismissViewControllerAnimated:YES completion:^{
    //                     self.communicationAlert = nil;
    //                 }];
    //             }
    //             else
    //             {
    //                 self.communicationAlert = nil;
    //             }
    //         }
    //         break;
    //     case BMSSwiperConnectionStateConfiguring:
    //         NSLog(@"Configuring Device");
    //         self.swiperStatus.text = @"Configuring";
    //         self.connectButton.enabled = NO;
    //         [self i_showCommunicationAlertWithMessage:@"Configuring" cancelable:NO];
    //         break;
    //     case BMSSwiperConnectionStateConnecting:
    //         NSLog(@"Will Connect Swiper");
    //         self.swiperStatus.text = @"Connecting";
    //         self.connectButton.enabled = NO;
    //         [self i_showCommunicationAlertWithMessage:@"Connecting" cancelable:NO];
    //             break;
    //     case BMSSwiperConnectionStateSearching:
    //         NSLog(@"Will search for Swiper");
    //         self.swiperStatus.text = @"Searching";
    //         self.connectButton.enabled = NO;
    //         [self i_showCommunicationAlertWithMessage:@"Searching" cancelable:YES];
    //         break;
    //     default:
    //         break;
    // }
}

- (void)swiperDidStartCardRead:(BMSSwiper *)swiper
{
    RCTLogInfo(@"swiper swiperDidStartCardRead");

    [self sendEventWithName:@"BoltOnSwiperReady" body:@{@"test": @"I guess I need a body"}];
    // NSLog(@"Card Read Started");
    // [self.view endEditing:YES];
    
    // if (self.alert == nil)
    // {
    //     self.alert = [UIAlertController alertControllerWithTitle:@"" message:@"" preferredStyle:UIAlertControllerStyleAlert];
    //     [self.alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    //         self.alert = nil;
    //         [self.swiper cancelTransaction];
    //     }]];
    // }
    
    // if (self.swipeOnlySwitch.isOn ||
    //     ((AppDelegate*)[UIApplication sharedApplication].delegate).swiperType == BMSSwiperTypeBBPOS)
    // {
    //     self.alert.message = @"Swipe Card";
    // }
    
    // if (!self.presentedViewController)
    // {
    //     [self presentViewController:self.alert animated:YES completion:nil];
    // }
    // else
    // {
    //     // Delay until alert is dismissed
    //     dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
    //         if (!self.presentedViewController)
    //         {
    //             [self presentViewController:self.alert animated:YES completion:nil];
    //         }
    //     });
    // }
}

- (void)swiper:(BMSSwiper *)swiper didGenerateTokenWithAccount:(BMSAccount *)account completion:(void (^)(void))completion
{
    RCTLogInfo(@"swiper didGenerateTokenWithAccount");

    [self sendEventWithName:@"BoltOnTokenGenerated" body:@{@"token": account.token}];

    // [self i_stopActivityIndicator];
    
    // if (self.alert)
    // {
    //     [self.alert dismissViewControllerAnimated:YES completion:^{
    //         self.alert = nil;
    //     }];
    // }
    // else if (self.communicationAlert)
    // {
    //     [self.communicationAlert dismissViewControllerAnimated:YES completion:^{
    //         self.communicationAlert = nil;
    //     }];
    // }
    
    // if (account)
    // {
    //     UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Token Generated" message:account.token preferredStyle:UIAlertControllerStyleAlert];
    //     [alert addAction:[UIAlertAction actionWithTitle:@"Retry" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    //         completion();
    //     }]];
    //     [alert addAction:[UIAlertAction actionWithTitle:@"Done" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    //         self.restartReaderBlock = completion;
    //         self.restartReaderButton.enabled = YES;
    //     }]];
    //     [self presentViewController:alert animated:YES completion:nil];
    // }
    // else
    // {
    //     UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error" message:@"An unknown error" preferredStyle:UIAlertControllerStyleAlert];
    //     [alert addAction:[UIAlertAction actionWithTitle:@"Retry" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    //         completion();
    //     }]];
    //     [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    //         self.restartReaderBlock = completion;
    //         self.restartReaderButton.enabled = YES;
    //     }]];
    //     [self presentViewController:alert animated:YES completion:nil];
    // }
}

- (void)swiper:(BMSSwiper *)swiper didFailWithError:(NSError *)error completion:(void (^)(void))completion
{
    RCTLogInfo(@"swiper didFailWithError");
    RCTLogInfo(error.localizedDescription);

    [self sendEventWithName:@"BoltOnSwipeError" body:@{@"error": error.localizedDescription}];

    // [self i_stopActivityIndicator];
    
    // if (self.alert)
    // {
    //     [self.alert dismissViewControllerAnimated:YES completion:^{
    //         self.alert = nil;
    //     }];
    // }
    // else if (self.communicationAlert)
    // {
    //      [self.communicationAlert dismissViewControllerAnimated:YES completion:^{
    //          self.communicationAlert = nil;
    //      }];
    // }

    // NSMutableString *errorMessage = [[NSMutableString alloc] initWithFormat:@"An error occured:\n%@",error.localizedDescription];
    
    // if ([error.userInfo valueForKey:@"firmwareVersion"])
    // {
    //     [errorMessage appendFormat:@"\n\n%@", [error.userInfo valueForKey:@"firmwareVersion"]];
    // }
    
    // UIAlertController *controller = [UIAlertController alertControllerWithTitle:@""
    //                                                                     message:errorMessage
    //                                                              preferredStyle:UIAlertControllerStyleAlert];
    // [controller addAction:[UIAlertAction actionWithTitle:@"Retry" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
    //     completion();
    // }]];
    // [controller addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    //     self.restartReaderBlock = completion;
    //     self.restartReaderButton.enabled = YES;
    // }]];
    // [self presentViewController:controller animated:YES completion:nil];
}

- (void)swiper:(BMSSwiperController*)swiper foundDevices:(NSArray*)devices
{
    RCTLogInfo(@"swiper foundDevices");

    BMSDevice *device = [devices objectAtIndex:0];
    // int value = returnedObject.aVariable;
    [self sendEventWithName:@"BoltDeviceFound" body:@{@"macAddress": device.uuid, @"name": device.name}];
}

- (void)swiper:(BMSSwiperController*)swiper displayMessage:(NSString*)message canCancel:(BOOL)cancelable
{
    RCTLogInfo(@"swiper displayMessage");
    // if (self.alert == nil)
    // {
    //     self.alert = [UIAlertController alertControllerWithTitle:@"" message:@"" preferredStyle:UIAlertControllerStyleAlert];
    // }
    
    // self.alert.message = message;
    
    // if (cancelable &&
    //     self.alert.actions.count == 0)
    // {
    //     [self.alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    //         self.alert = nil;
    //         [self.swiper cancelTransaction];
    //     }]];
    // }
    // else if (!cancelable &&
    //          self.alert.actions.count > 0)
    // {
    //     for (UIAlertAction *action in self.alert.actions)
    //     {
    //         action.enabled = NO;
    //     }
    // }
    
    // if (!self.presentedViewController)
    // {
    //     [self presentViewController:self.alert animated:YES completion:nil];
    // }
}

- (void)swiper:(BMSSwiperController *)swiper configurationProgress:(float)progress
{
    RCTLogInfo(@"swiper configurationProgress");
    // [self i_showCommunicationAlertWithMessage:[NSString stringWithFormat:@"Configuring: %.0f%%",progress*100]
    //                                cancelable:NO];
}

@end
