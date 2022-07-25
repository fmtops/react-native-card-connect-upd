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

#import "BMSTypes.h"

/**
 Contains functions that are helpful for validating various card parameters.

 - BMS_LuhnCheck - Performs a Luhn validation on a card number.

 - BMS_CardIssuerFromCardNumber - Returns a card issuer enumeration for a card number.

 - BMS_MaxCardNumberLengthForCardNumber - Returns the max length for card number based off its issuer.

 - BMS_ValidateCardLength - Validates the length of the card number based off its issuer.

 - BMS_ValidateCardNumber - Validates the card number.

 - BMS_MaskCardNumberWithCharacterAndFormat - Masks a card number with the given parameters.

 - BMS_ValidateCVV - Validates a CVV.

 - BMS_ValidateCVVForCardNumber - Validates a CVV for a card number.

 - BMS_MaskCVVWithCharacter - Masks a CVV with the given parameters.

 - BMS_ValidateExpirationDate - Validates an expiration date of a card.

 - BMS_Base64GZippedSignatureForImage - Encodes a UIImage of a signature for sending to CardConnect.

 - BMS_ImageFromBase64GZippedString - Decodes signature data returned by CardConnect.

 - BMS_UTCDateFormatter - Returns the UTC NSDateFormatter used in the SDK.
 */
@interface BMSCardFunctions : NSObject

/**
 Validates a credit card number using the Luhn algorithm.

 @throws NSInvalidArgumentException if number contains non-numeric characters.

 @param number A credit card number.

 @return `True` if the number appears to be a valid credit card number.
 */
BOOL BMS_LuhnCheck(NSString *number);

/**
 Parses a card issuer based on the first six digits of the card number.

 @param cardNumber A card number.

 @return A card issuer.
 */
BMSCardIssuer BMS_CardIssuerFromCardNumber(NSString *cardNumber);

/**
 Returns the max length for the determined card issuer of the number.

 Uses BMSCardIssuerFromCardNumber() to determine the card issuer. If the issuer cannot be determined, `-1` is
 returned.

 @param cardNumber A card number.

 @return An integer representing the max length for the card's determined issuer.
 */
NSInteger BMS_MaxCardNumberLengthForCardNumber(NSString *cardNumber);

/**
 Validates card number length is in acceptable bounds for card issuer. If issuer cannot be determined, `FALSE` is
 returned.

 Uses BMSCardIssuerFromCardNumber() to determine the card issuer.

 @param cardNumber A card number.

 @return `True` if the card is an acceptable length for the determined card issuer, `FALSE` otherwise.
 */
BOOL BMS_ValidateCardLength(NSString *cardNumber);

/**
 Runs some simple validation on the card number.

 This is the order of functions it uses to validate the card number:
 - Get card issuer BMSCardIssuerFromCardNumber
 - Validate card number length BMSValidateCardLength
 - Luhn validation BMSLuhnCheck

 @param cardNumber A card number.

 @return If the card number is valid
 */
BOOL BMS_ValidateCardNumber(NSString *cardNumber);

/**
 Returns a masked version of the card number based on the provided settings.

 @param cardNumber A card number.
 @param maskCharacter The character used in the mask.
 @param format The formate used for masking.

 @return A masked version of the cardNumber.
 */
NSString* BMS_MaskCardNumberWithCharacterAndFormat(NSString *cardNumber, unichar maskCharacter, BMSCardMaskFormat format, BMSCardMaskSpacing spacing);

/**
 Runs some simple length validation on the CVV

 @param CVV The CVV to validate.

 @return The result of the validation.
 */
BOOL BMS_ValidateCVV(NSString *CVV);

/**
 Runs a sample length validation on the CVV based on a card number.

 @param CVV The CVV to validate.
 @param cardNumber The card number to use to validate.

 @return The result of the validation.
 */
BOOL BMS_ValidateCVVForCardNumber(NSString *CVV, NSString *cardNumber);

/**
 Returns a masked version of the CVV with the provided maskCharacer.

 @param CVV The CVV to mask.
 @prarm maskCharacter The character used to mask the CVV.

 @return A masked version of CVV.
 */
NSString* BMS_MaskCVVWithCharacter(NSString *CVV, unichar maskCharacter);

/**
 Validates a date against the current date.

 Used to determine if an expiration date on a card is valid for the current date. i.e. A date that is of the current
 month and year or more is valid. Only month and year are used for comparison. The date is compared against UTC
 components and should be retrieved in UTC.

 @param date The date to validate.

 @return A boolean indicating the date is a valid expiration date.

 @see BMS_UTCDateFormatter
 */
BOOL BMS_ValidateExpirationDate(NSDate *date);

/**
 Used to generate an encoded string for a signature image.

 Image should be black with white or clear background. The image will be resized, rendered in fewer pixels and
 compressed to fit the storage size as defined by CardConnect.

 @param image The image to compress.

 @return A base 64 encoded string representation of a resized, rerendered, gzipped image.
 */
NSString* BMS_Base64GZippedSignatureForImage(UIImage *image);

/**
 Used to get a UIImage from compressed signature data returned by CardConnect.

 This will decode the base 64 string and uncompress it. The image returned will be the sized and rendered image as
 performed by BMSBase64GZippedSignatureForImage.

 @param string A base 64 encoded gzipped image.

 @return The UIImage of the encoded string.
 */
UIImage* BMS_ImageFromBase64GZippedString(NSString *string);

/**
 Returns the date formatter used in the SDK for all NSDates.

 This NSDateFormatter is configured for UTC time using the local "en_US_POSIX" and timeZoneForSecondsFromGMT: set to 0.
 It does not have a format set, please set your own format using setDateFormat: or other available methods.

 @return The UTC NSDateFormatter used in the SDK.
 */
NSDateFormatter* BMS_UTCDateFormatter(void);

@end
