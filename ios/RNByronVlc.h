#import <React/RCTView.h>

@class RCTEventDispatcher;

@interface RNByronVlc : UIView

@property (nonatomic, copy) RCTBubblingEventBlock onEventVlc;

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

@end
