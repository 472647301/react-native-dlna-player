// RNByronDLNA.h

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "MediaRendererDelegate.h"

@interface RNByronDLNA : RCTEventEmitter <RCTBridgeModule, MediaRendererDelegate>

@property (nonatomic) enum
{
mediaType_unsupport,
mediaType_video,
mediaType_photo,
mediaType_music,
}
mediaType;

@end