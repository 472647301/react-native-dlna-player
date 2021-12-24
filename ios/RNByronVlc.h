#import <React/RCTView.h>
#import <MobileVLCKit/MobileVLCKit.h>

@class RCTEventDispatcher;

@interface RNByronVlc : UIView <VLCMediaPlayerDelegate>

@property (nonatomic, copy) RCTBubblingEventBlock onVideoLoadStart;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoLoad;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoBuffer;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoError;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoProgress;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoPause;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoStop;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoEnd;

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

@end
