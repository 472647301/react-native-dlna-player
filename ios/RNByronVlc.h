#import <React/RCTView.h>

@class RCTEventDispatcher;

@interface RNByronVlc : UIView

@property (nonatomic, copy) RCTBubblingEventBlock onVideoStart;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoBuffer;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoError;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoProgress;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoPaused;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoEnd;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoSwitch;

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

@end
