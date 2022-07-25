//
//  BMSNonPaymentCard.h
//  CardConnectConsumerSDK
//
//  Created by Nick Rimer on 1/3/20.
//  Copyright © 2020 CardConnect. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BMSTypes.h"

/**
 An object containing the details of a card that was read that was not a payment card.
 
 For example, this could be a drivers license or rewards card.
 */
@interface BMSNonPaymentCard : NSObject <NSCopying>

/**
 Raw track 1 data provided by the card.
 */
@property (nonatomic, readonly, copy) NSString *track1;

/**
 Raw track 2 data provided by the card.
 */
@property (nonatomic, readonly, copy) NSString *track2;

/**
 Raw track 3 data provided by the card.
 */
@property (nonatomic, readonly, copy) NSString *track3;

/**
 Returns an instance of a non payment card with track data.
 
 @param track1 The track 1 data from the card.
 @param track2 The track 2 data from the card.
 @param track3 The track 3 data from the card.
 
 @return An instance of BMSNonPaymentCard.
 */
- (instancetype)initWithTrack1:(NSString*)track1 track2:(NSString*)track2 track3:(NSString*)track3;

@end
