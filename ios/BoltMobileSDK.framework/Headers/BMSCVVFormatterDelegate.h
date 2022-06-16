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

#import "BMSTextFieldDelegateProxy.h"

@class BMSCardInfo;
@class BMSCardFormatterDelegate;

/**
 A UITextFieldDelegate class that will auto format its text field with a masked CVV value.

 This class internally contains the CVV set in the text field. When text is changed, this value is updated and a masked
 value is set to the text field. The masking character can be changed using [BMSCVVFormatterDelegate maskCharacter].

 You can check if the CVV is valid using [BMSCVVFormatterDelegate isValidCVV],
 [BMSCVVFormatterDelegate isValidCVVForCardNumber:] or [BMSCVVFormatterDelegate isValidCVVWithCardFormatter:].

 To retrieve the CVV, use [BMSCVVFormatterDelegate setCVVOnCardInfo:] to set it onto your BMSCardInfo object.

 To clear the CVV and text field use [BMSTextFieldDelegateProxy clearTextField].
 */
@interface BMSCVVFormatterDelegate : BMSTextFieldDelegateProxy

/**
 The mask character used for the CVV.

 The default value is '*'.
 */
@property (nonatomic) unichar maskCharacter;

/**
 Can be used to check if the CVV is valid.

 @return The result of the validation.
 */
- (BOOL)isValidCVV;

/**
 Can be used to validate the CVV against a card number;

 @param cardNumber The unmasked card number for validation.

 @return The result of the validation.
 */
- (BOOL)isValidCVVForCardNumber:(NSString*)cardNumber;

/**
 Can be used to validate the CVV using the BMSCardFormatterDelegate.

 @param formatter The formatter being used on the card number text field.

 @return The result of the validation.

 @see BMSCardFormatterDelegate
 */
- (BOOL)isValidCVVWithCardFormatter:(BMSCardFormatterDelegate*)formatter;

/**
 Should be used to set the CVV on a BMSCardInfo object.

 This is the only way to retrieve the unmasked CVV from the text field.

 @param cardInfo The BMSCardInfo object you want to assign the CVV to.

 @see BMSCardInfo
 */
- (void)setCVVOnCardInfo:(BMSCardInfo*)cardInfo;

@end
