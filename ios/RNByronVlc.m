#import <React/RCTConvert.h>
#import "RNByronVlc.h"
#import <React/RCTBridgeModule.h>
#import <React/RCTEventDispatcher.h>
#import <React/UIView+React.h>
#import <MobileVLCKit/MobileVLCKit.h>
#import <AVFoundation/AVFoundation.h>

@implementation RNByronVlc
{
    RCTEventDispatcher *_eventDispatcher;
    VLCMediaPlayer *_player;

    BOOL _paused;
    BOOL _started;
}

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher
{
    if ((self = [super init])) {
        _eventDispatcher = eventDispatcher;

        _paused = NO;
        _started = NO;

        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];

        [defaultCenter addObserver:self
                          selector:@selector(applicationWillResignActive:)
                              name:UIApplicationWillResignActiveNotification
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(applicationWillEnterForeground:)
                              name:UIApplicationWillEnterForegroundNotification
                            object:nil];
    }

    return self;
}

- (void)applicationWillResignActive:(NSNotification *)notification
{
    if(_player && !_paused) {
        [_player play];
    }
}

- (void)applicationWillEnterForeground:(NSNotification *)notification
{
    [self applyModifiers];
}

- (void)applyModifiers
{
    if(_player && !_paused) {
        [_player play];
    }
}

- (void)setPaused:(BOOL)paused
{
    if(_player) {
        if (paused) {
            [_player pause];
            if(self.onVideoPause)
                self.onVideoPause(@{@"paused": @(1)});
        } else {
            [_player play];
            if(self.onVideoPause)
                self.onVideoPause(@{@"paused": @(0)});
        }
    }
    _paused = paused;
}

-(void)setSrc:(NSDictionary *)source
{
    if(_player) {
        [_player pause];
        [_player stop];
        _player = nil;
    }
    _started = NO;

    NSString* uri = [source objectForKey:@"uri"];
    NSDictionary* initOptions = [source objectForKey:@"options"];
    NSURL* url = [NSURL URLWithString:uri];
    
    _player = [[VLCMediaPlayer alloc] init];
    [_player setDrawable:self];
    _player.delegate = self;
    _player.scaleFactor = 0;

    VLCMedia *media = [VLCMedia mediaWithURL:url];
    for (NSString* option in initOptions) {
        [media addOption:[option stringByReplacingOccurrencesOfString:@"--" withString:@""]];
    }
    _player.media = media;
    [[AVAudioSession sharedInstance] setActive:NO withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:nil];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if(self.onVideoLoadStart)
            self.onVideoLoadStart(@{@"src": @{
                                            @"uri": uri ? uri : [NSNull null],
                                            },
                                    @"target": self.reactTag
                                    });
    });
    [self applyModifiers];
}

-(void)setSeek:(float)pos
{
    if([_player isSeekable]){
        if(pos>=0 && pos <= 1){
            [_player setPosition:pos];
        }
    }
}

- (void)setSnapshotPath:(NSString *)snapshotPath
{
    if(_player && _player.isPlaying) {
        [_player saveVideoSnapshotAt:snapshotPath withWidth:0 andHeight:0];
    }
}

- (void)onProgressUpdate
{
    if(_player && _player.isPlaying && self.onVideoProgress) {
        int currentTime   = [[_player time] intValue];
        int duration      = [_player.media.length intValue];
        int remainingTime = [[_player remainingTime] intValue];
        NSLog(@"remainingTime %i", remainingTime);
        if (remainingTime < 0 && remainingTime > -500) {
            if(self.onVideoEnd)
                self.onVideoEnd(@{ @"target": self.reactTag });
        }
        if(!_started) {
            _started = YES;
            if(self.onVideoLoad)
                self.onVideoLoad(@{
                                   @"target": self.reactTag,
                                   @"duration": [NSNumber numberWithInt:duration],
                                   @"currentTime": [NSNumber numberWithInt:currentTime],
                                   });
        }
        if( currentTime >= 0 && currentTime < duration) {
            self.onVideoProgress(@{
                                   @"target": self.reactTag,
                                   @"currentTime": [NSNumber numberWithInt:currentTime],
                                   @"duration":[NSNumber numberWithInt:duration]
                                   });
        }
    }
}

- (void)mediaPlayerTimeChanged:(NSNotification *)aNotification
{
    [self onProgressUpdate];
}


- (void)mediaPlayerStateChanged:(NSNotification *)aNotification
{

     NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
     NSLog(@"userInfo %@",[aNotification userInfo]);
     NSLog(@"standardUserDefaults %@",defaults);
    if(_player){
        VLCMediaPlayerState state = _player.state;
        switch (state) {
            case VLCMediaPlayerStateOpening:
                 NSLog(@"VLCMediaPlayerStateOpening %i",1);
                break;
            case VLCMediaPlayerStatePaused:
                NSLog(@"VLCMediaPlayerStatePaused %i",1);
                if(self.onVideoPause)
                    self.onVideoPause(@{ @"target": self.reactTag });
                break;
            case VLCMediaPlayerStateStopped:
                NSLog(@"VLCMediaPlayerStateStopped %i",1);
                if(self.onVideoStop)
                    self.onVideoStop(@{ @"target": self.reactTag });
                break;
            case VLCMediaPlayerStateBuffering:
                NSLog(@"VLCMediaPlayerStateBuffering %i",1);
                if(self.onVideoBuffer)
                    self.onVideoBuffer(@{ @"target": self.reactTag });
                break;
            case VLCMediaPlayerStatePlaying:
                NSLog(@"VLCMediaPlayerStatePlaying %i",1);
                break;
            case VLCMediaPlayerStateEnded:
                NSLog(@"VLCMediaPlayerStateEnded %i",1);
                if(self.onVideoEnd)
                    self.onVideoEnd(@{ @"target": self.reactTag });
                break;
            case VLCMediaPlayerStateError:
                NSLog(@"VLCMediaPlayerStateError %i",1);
                if(self.onVideoError)
                    self.onVideoError(@{ @"target": self.reactTag });
                break;
            default:
                break;
        }
    }
}

-(void)setVolume:(int)volume
{
    if(_player){
        VLCAudio *audio = _player.audio;
        if(volume >= 0){
            audio.volume = volume;
        }
    }
}

#pragma mark - Lifecycle
- (void) removeFromSuperview
{
    if(_player) {
        [_player pause];
        [_player stop];
        _player = nil;
    }
    _eventDispatcher = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super removeFromSuperview];
}

@end
