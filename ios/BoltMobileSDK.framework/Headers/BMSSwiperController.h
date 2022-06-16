/*

 Copyright 2017 iMobile3, LLC. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, is permitted provided that adherence to the following
 conditions is maintained. If you do not agree with these terms,
 please do not use, install, modify or redistribute this software.

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY IMOBILE3, LLC "AS IS" AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 EVENT SHALL IMOBILE3, LLC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "BMSSwiper.h"
#import "BMSDevice.h"
#import "BMSNonPaymentCard.h"

/**
 Swiper types available.
 */
typedef NS_ENUM(NSInteger,BMSSwiperType)
{
    /** The BBPOS swiper */
    BMSSwiperTypeBBPOS,
    /** The IDTech VP3300 swiper */
    BMSSwiperTypeVP3300,
    /** The IDTech VP3600 swiper */
    BMSSwiperTypeVP3600
};

/**
 Card read modes.
 */
typedef NS_ENUM(NSInteger,BMSCardReadMode)
{
    /** Card read mode Swipe only */
    BMSCardReadModeSwipe,
    /** Card read mode swipe and dip */
    BMSCardReadModeSwipeDip,
    /** Card read mode swipe, dip and tap*/
    BMSCardReadModeSwipeDipTap,
    /** Card read mode swipe and tap*/
    BMSCardReadModeSwipeTap
};

/**
 Device beep settings.
 */
typedef NS_ENUM(NSInteger,BMSDeviceBeepSetting)
{
    /** The device will not emit a tone. */
    BMSDeviceBeepSettingNone,
    /** The device will emit a single, short tone. */
    BMSDeviceBeepSettingSingle,
    /** The device will emit a two short tones. */
    BMSDeviceBeepSettingDouble,
    /** The device will emit a three short tones. */
    BMSDeviceBeepSettingTriple,
    /** The device will emit a four short tones. */
    BMSDeviceBeepSettingQuadruple,
    /** The device will emit a single, 200 milisecond long tone. */
    BMSDeviceBeepSetting200ms,
    /** The device will emit a single, 400 milisecond long tone. */
    BMSDeviceBeepSetting400ms,
    /** The device will emit a single, 600 milisecond long tone. */
    BMSDeviceBeepSetting600ms,
    /** The device will emit a single, 800 milisecond long tone. */
    BMSDeviceBeepSetting800ms
};

@protocol BMSSwiperControllerDelegate;

/**
 A subclass of BMSSwiper implementing the communication methods with the swiper.

 @see BMSSwiper
 */
@interface BMSSwiperController : BMSSwiper

/**
 The current swiper type.
 */
@property (nonatomic, readonly) BMSSwiperType swiperType;

/**
 Used to configure the device's beep setting
 
 If the device supports an audible beep to remove the card after a transaction, this setting will allow you to customize it. The default value
 is BMSDeviceBeepSetting800ms unless the device doesn't support beeps, in which case it's BMSDeviceBeepSettingNone.
 */
@property (nonatomic) BMSDeviceBeepSetting beepSetting;

/**
 Used to configure the device's timeout for a card read.
 
 This value is in seconds and must be greater than `1`s. For BMSCardReadModeSwipe the max value is `0xFF` or `255`s. For BMSCardReadModeSwipeDip
 the max value is `0xFFFF` or `65535`s. The default value is `60`s. If an invalid value is attempted to be set the value will not change.
 
 @note If you set this value to greater than the BMSCardReadMode supplied it will be reduced to the max for that mode.
 */
@property (nonatomic) NSInteger cardReadTimeout;

/**
 The current card read mode of the reader.
 
 This value can only be set when connecting to a device.
 */
@property (nonatomic, readonly) BMSCardReadMode currentReadMode;

/**
 Used to instantiate the class with a delegate and if logging is enabled.

 @param delegate The BMSSwiperControllerDelegate responder.
 @param type The BMSSwiperType to use.
 @param enabled A boolean flag to enable logging.
 */
- (instancetype)initWithDelegate:(id<BMSSwiperControllerDelegate>)delegate swiper:(BMSSwiperType)type loggingEnabled:(BOOL)enabled;

/**
 Used to find BLE devices
 
 Can be canceled with `cancelFindDevices`.
 */
- (void)findDevices;

/**
 Used to cancel finding devices
 
 Call this method with `connectionState` is `BMSSwiperConnectionStateSearching`.
 */
- (void)cancelFindDevices;

/**
 Used to connect to device
 
 If connection state is still `BMSSwiperConnectionStateSearching` you can cancel the connection with `cancelFindDevices`.
 
 @param device The UUID of the device you wish to connect to.
 
 @deprecated Deprecated in 4.1. Please use connectToDevice:mode:
 */
- (void)connectToDevice:(NSUUID*)device DEPRECATED_MSG_ATTRIBUTE("Deprecated in 4.1. Please use connectToDevice:mode:");

/**
 Used to connect to device
 
 If connection state is still `BMSSwiperConnectionStateSearching` you can cancel the connection with `cancelFindDevices`.
 
 @param device The UUID of the device you wish to connect to.
 @param mode The card read mode for the connection.
 */
- (void)connectToDevice:(NSUUID*)device mode:(BMSCardReadMode)mode;

/**
 Used to connect to device
 
 If connection state is still `BMSSwiperConnectionStateSearching` you can cancel the connection with `cancelFindDevices`.
 
 @param device The UUID of the device you wish to connect to.
 @param mode The card read mode for the connection.
 @param forceConfigure Forces the device to reconfigure.
 */
- (void)connectToDevice:(NSUUID*)device mode:(BMSCardReadMode)mode forceConfig:(BOOL)forceConfigure;

/**
 Used to cancel a transaction in progress
 
 This should only be used if the swiper becomes unresponsive do to an event like the card being removed.
*/
- (void)cancelTransaction;

/**
 Performs cleanup to stop the swiper and release the BMSSwiperControllerDelegate
 
 This **MUST** be called for BMSSwiperController to be properly released otherwise resulting in unknown issues with swiper.
 Create a new instance of BMSSwiperController after this is called
 */
- (void)releaseDevice;

@end

/**
 A protocol that extends BMSSwiperDelegate implementing specific device capabilities like device searching.
 */
@protocol BMSSwiperControllerDelegate <BMSSwiperDelegate>

/**
 A required method that provides devices found.
 
 Starts being called when [BMSSwiperController findDevices] is called.
 
 @param swiper An instance of BMSSwiperController.
 @param devices An array of unique devices found.
 
 @see BMSDevice
 */
- (void)swiper:(BMSSwiperController*)swiper foundDevices:(NSArray*)devices;

/**
 A required method that provides messages generated by the device.
 
 These messages are required to be displayed to the user.
 
 @param swiper An instance of BMSSwiperController.
 @param message A message returned from the device that must be displayed to the screen.
 @param cancelable Indicates that [BMSSwiperController cancelTransaction] can be used.
 */
- (void)swiper:(BMSSwiperController*)swiper displayMessage:(NSString*)message canCancel:(BOOL)cancelable;

@optional

/**
 An optional method that provides the progress of the configuration progress.
 
 This method will be called when the swiper connection state switches to BMSSwiperConnectionStateConfiguring. It provides
 the progress of the configuration. User interaction should be disabled until progress is complete.
 
 @param swiper An instance of BMSSwiperController.
 @param progress The progress of the configuration. Value is from 0 to 1.
 */
- (void)swiper:(BMSSwiperController*)swiper configurationProgress:(float)progress;

/**
 An optional method that requests an amount to be shown on the device if it has a display.
 
 This amount will be displayed on devices such as the VP3600. If no amount is provided, $1.00 will be shown.
 
 @param swiper An instance of BMSSwiperController.
 @return The transaction amount to be shown on the device.
 */
- (NSDecimalNumber*)transactionAmountForSwiper:(BMSSwiperController*)swiper;

/**
 An optional method notifying the delegate that BMSSwiper detected a non payment card.

 Call completion to start the swiper again. If you presented a status indicator or disabled user interaction in
 [BMSSwiperDelegate swiperDidStartCardRead:], dismiss the status indicator and re-enable user interaction at this point
 before calling completion.

 @param swiper An instance of BMSSwiper.
 @param card The account returned with the generated token for the card swipe.
 @param completion A completion block that can be called to restart the swiper.

 @see BMSAccount
 */
- (void)swiper:(BMSSwiper *)swiper didReadNonPaymentCard:(BMSNonPaymentCard *)card completion:(void (^)(void))completion;

@end
