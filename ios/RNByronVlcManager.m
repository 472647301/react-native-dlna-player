#import "RNByronVlcManager.h"
#import "RNByronVlc.h"
#import <React/RCTBridge.h>

@implementation RNByronVlcManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view {
    return [[RNByronVlc alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(source, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(time, int);
RCT_EXPORT_VIEW_PROPERTY(rate, float);
RCT_EXPORT_VIEW_PROPERTY(paused, BOOL);
RCT_EXPORT_VIEW_PROPERTY(aspectRatio, NSString);
RCT_EXPORT_VIEW_PROPERTY(volume, int);

RCT_EXPORT_VIEW_PROPERTY(onEventVlc, RCTBubblingEventBlock);

@end
