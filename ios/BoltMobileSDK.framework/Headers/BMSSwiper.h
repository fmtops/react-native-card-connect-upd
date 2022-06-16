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

/** Error Domain for swiper errors */
extern NSString * _Nonnull const BMSSwiperErrorDomain;

/** Model name for BBPOS devices. */
extern NSString * _Nonnull const BMSSwiperModelBBPOS;

/** Model name for VP3300 devices. */
extern NSString * _Nonnull const BMSSwiperModelVP3300;

/** Model name for VP3600 devices. */
extern NSString * _Nonnull const BMSSwiperModelVP3600;

/** Error codes for BMSSwiperErrorDomain. */
typedef NS_ENUM(NSInteger, BMSSwiperError)
{
    /** The application hasnt received audio access permission from the user. */
    BMSSwiperErrorAudioPermissionDenied = 100,
    /** A chip error occured and the user should swipe the card. */
    BMSSwiperErrorSwipeCard,
    /** A chip card was swiped and should be inserted. */
    BMSSwiperErrorInsertCard,
    /** The transaction was canceled. */
    BMSSwiperErrorCanceledTransaction,
    /** The swiper timed out. */
    BMSSwiperErrorTimeout,
    /** Connection error. */
    BMSSwiperErrorConnectionError,
    /** Unsupported connection mode. */
    BMSSwiperErrorUnsupportedMode,
    /** Swiped card was unable to be read. */
    BMSSwiperErrorBadCardRead,
    /** The device failed to configure. */
    BMSSwiperErrorConfigurationError,
    /** The device failed to start audio connection due to another application taking higher priority over audio. */
    BMSSwiperErrorOtherAudioPlaying,
    /** The card presented is not supported. */
    BMSSwiperErrorCardNotSupported,
    /** An unknown error occured, check localized description for details. */
    BMSSwiperErrorUnknown = 500
};

@class BMSAccount;

@protocol BMSSwiperDelegate;

/**
 Values describing the connection state of the swiper.
 */
typedef NS_ENUM(NSInteger, BMSSwiperConnectionState)
{
    /** The swiper is not connected. */
    BMSSwiperConnectionStateDisconnected = 0,
    /** The application is looking for devices.
     
     The swiper can be in this state from two methods, findDevices and connectToDevice.
     */
    BMSSwiperConnectionStateSearching,
    /** The swiper is attempting to connect.
     
     User interaction should be disabled until the state changes.
     */
    BMSSwiperConnectionStateConnecting,
    /** The swiper is connected. */
    BMSSwiperConnectionStateConnected,
    /** The swiper is configuring.
     
     User interaction should be disabled until the state changes.
     */
    BMSSwiperConnectionStateConfiguring
};

/**
 Values describing the battery status of the swiper.
 */
typedef NS_ENUM(NSInteger, BMSSwiperBatteryStatus)
{
    /** The swiper battery is low but can still process commands. */
    BMSSwiperBatteryStatusLow,
    /** The swiper battery is critical and wont process any commands. */
    BMSSwiperBatteryStatusCritical
};

/**
 BMSSwiper is a base class that defines a swiper controller. To use the swiper, please use BMSSwiperController. If the
 Library doesn't contain BMSSwiperController, this version does not support swiper integration.
 */
@interface BMSSwiper : NSObject
{
@protected
    /**
        The current connection state of the swiper.
     */
    BMSSwiperConnectionState _connectionState;
}

/**
 The delegate for the swiper callbacks.
 */
@property(nonatomic, weak, nullable) id<BMSSwiperDelegate> delegate;

/**
 The connection state of the swiper.
 */
@property(nonatomic, assign, readonly) BMSSwiperConnectionState connectionState;

/**
 The model of swiper being used.
 */
+ (NSString* _Nonnull)model;

/**
 Flag for setting or checking if debug logging is enabled.
 */
@property(nonatomic, getter = isDebugLoggingEnabled) BOOL debugLoggingEnabled;

/**
 Used to initialize BMSSwiper with a delegate.

 @param delegate A class that adheres to the BMSSwiperDelegate protocol.

 @return An instance of BMSSwiper.
 */
- (instancetype _Nullable)initWithDelegate:(id<BMSSwiperDelegate> _Nullable)delegate;

/**
 Used to initialize BMSSwiper with a delegate and set logging enabled.

 @param delegate A class that adheres to the BMSSwiperDelegate protocol.
 @param enabled A flag to set debugLoggingEnabled.

 @return An instance of BMSSwiper.
 */
- (instancetype _Nullable)initWithDelegate:(id<BMSSwiperDelegate> _Nullable)delegate loggingEnabled:(BOOL)enabled;

/**
 Indicates if an audio warning should be displayed.

 If returns TRUE, this swiper utilizes the audio jack for communication. An audio warning should be shown prior to
 initializing the swiper that indicates the user should remove any headphones and turn the volume to max.

 @return A flag for if an audio warning should be displayed.
 */
- (bool)shouldShowAudioWarning;

@end

/**
 A protocol that handles responses from BMSSwiper.
 */
@protocol BMSSwiperDelegate <NSObject>

@required

/**
 A required method notifying the delegate that BMSSwiper successfully generated a token and returns an account.

 Call completion to start the swiper again. If you presented a status indicator or disabled user interaction in
 [BMSSwiperDelegate swiperDidStartCardRead:], dismiss the status indicator and re-enable user interaction at this point
 before calling completion.

 @param swiper An instance of BMSSwiper.
 @param account The account returned with the generated token for the card swipe.
 @param completion A completion block that can be called to restart the swiper.

 @see BMSAccount
 */
- (void)swiper:(BMSSwiper * _Nonnull)swiper didGenerateTokenWithAccount:(BMSAccount * _Nullable)account completion:(void (^ _Nonnull)(void))completion;

/**
 A required method notifying the delegate that BMSSwiper failed to swipe the card.

 Call completion to start the swiper again. If you presented a status indicator or disabled user interaction in
 [BMSSwiperDelegate swiperDidStartCardRead:], dismiss the status indicator and re-enable user interaction at this point
 before calling completion.

 @param swiper An instance of BMSSwiper.
 @param error The error that occurred if available.
 @param completion Block that should be called to start the swiper again.
 */
- (void)swiper:(BMSSwiper * _Nonnull)swiper didFailWithError:(NSError * _Nonnull)error completion:(void (^ _Nonnull)(void))completion;

@optional

/**
 Optional method notifying the delegate that BMSSwiper connection state has changed.

 @param state The new state of the swiper.

 @param swiper An instance of BMSSwiper.
 */
- (void)swiper:(BMSSwiper * _Nonnull)swiper connectionStateHasChanged:(BMSSwiperConnectionState)state;

/**
 Optional method notifying the delegate that the BMSSwiper battery is low or in critical state.

 @param swiper An instance of BMSSwiper.
 @param status The status of the swiper.
 */
- (void)swiper:(BMSSwiper * _Nonnull)swiper batteryLevelStatusHasChanged:(BMSSwiperBatteryStatus)status;

/**
 Optional method notifying the delegate that BMSSwiper started an MSR transaction.

 Use this callback to display a status indicator and disable user interaction while the services are called.

 @param swiper An instance of BMSSwiper.
 */
- (void)swiperDidStartCardRead:(BMSSwiper * _Nonnull)swiper;

@end
