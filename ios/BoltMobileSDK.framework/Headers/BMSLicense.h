//
//  BMSLicense.h
//  CardConnectConsumerSDK
//
//  Created by Nick Rimer on 1/3/20.
//  Copyright © 2020 CardConnect. All rights reserved.
//

#import "BMSNonPaymentCard.h"

/**
 A type of non payment card that represents a Drivers License or ID that complies with AAMVA standards.
 */
@interface BMSLicense : BMSNonPaymentCard <NSCopying>

/**
 The state for the address listed on the license/ID.
 */
@property (nonatomic,copy) NSString *state;

/**
 The city for the address listed on the license/ID.
 */
@property (nonatomic,copy) NSString *city;

/**
 The name on the license/ID.
 */
@property (nonatomic,copy) NSString *name;

/**
 The street address on the license/ID.
 */
@property (nonatomic,copy) NSString *address;

/**
 This is the assigned identification number by AAMVA for the license/ID.
 */
@property (nonatomic,copy) NSString *isoIIN;

/**
 The identification number of the license/ID.
 */
@property (nonatomic,copy) NSString *DLN;

/**
 The expiration date of the license/ID.
 
 This date is in UTC and does not contain time. If expiration date is `null` then the license or ID does not expire.
 */
@property (nonatomic,copy) NSDate *expiration;

/**
 The birthday for the owner of the license/ID.
 */
@property (nonatomic,copy) NSDate *birthday;

/**
 The AAMVA version of the license/ID.
 */
@property (nonatomic,copy) NSString *version;

/**
 The issuing jurisdiction's version of the license/ID.
 */
@property (nonatomic,copy) NSString *jurisdictionVersion;

/**
 The postal/zip for the address of the license/ID.
 */
@property (nonatomic,copy) NSString *postal;

/**
 The type of license/ID.
 */
@property (nonatomic,copy) NSString *licenseClass;

/**
 The restrictions on this license/ID.
 */
@property (nonatomic,copy) NSString *restrictions;

/**
 The endorsements on this license/ID.
 */
@property (nonatomic,copy) NSString *endorsements;

/**
 The sex for the owner of the license/ID.
 
 `1` represents male, `2` represents female and `9` is not specified.
 */
@property (nonatomic,copy) NSString *sex;

/**
 The height of the owner of the license/ID.
 */
@property (nonatomic,copy) NSString *height;

/**
 The weight for the owner of the license/ID.
 */
@property (nonatomic,copy) NSString *weight;

/**
 The hair color for the owner of the license/ID.
 */
@property (nonatomic,copy) NSString *hair;

/**
 The eye color for the owner of the license/ID.
 */
@property (nonatomic,copy) NSString *eye;

@end
