#import "RNByronVlcManager.h"
#import "RNByronVlc.h"
#import <React/RCTBridge.h>

@implementation RNByronVlcManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view
{
    return [[RNByronVlc alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(src, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(paused, BOOL);
RCT_EXPORT_VIEW_PROPERTY(muted, BOOL);
RCT_EXPORT_VIEW_PROPERTY(seek, float);
RCT_EXPORT_VIEW_PROPERTY(snapshotPath, NSString);
RCT_EXPORT_VIEW_PROPERTY(volume, int);

RCT_EXPORT_VIEW_PROPERTY(onVideoLoadStart, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoLoad, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoBuffer, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoError, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoProgress, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoPause, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoStop, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onVideoEnd, RCTBubblingEventBlock);

@end
