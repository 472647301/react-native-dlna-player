#import "RNByronVlc.h"
#import <MobileVLCKit/MobileVLCKit.h>
#import <AVFoundation/AVFoundation.h>

@implementation RNByronVlc {
    RCTEventDispatcher * _eventDispatcher;
    VLCMediaPlayer * _player;
    NSString * _src;
    NSDictionary * _options;
    BOOL _paused;
    BOOL _isLoadDone;
}

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher {
    if ((self = [super init])) {
        _paused = NO;
        _isLoadDone = NO;
        _eventDispatcher = eventDispatcher;
        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
        [defaultCenter addObserver:self selector:@selector(applicationWillResignActive:) name:UIApplicationWillResignActiveNotification object:nil];
        [defaultCenter addObserver:self selector:@selector(applicationWillEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
    return self;
}

- (void)applicationWillResignActive:(NSNotification *)notification {
    
}

- (void)applicationWillEnterForeground:(NSNotification *)notification {
    
}


-(void)releasePlayer {
    if(_player) {
        [_player stop];
        _player = nil;
    }
}

-(void)createPlayer {
    if (_player) {
        [self releasePlayer];
    }
    NSURL* uri    = [NSURL URLWithString:_src];
    _player = [[VLCMediaPlayer alloc] init];
    [_player setDrawable:self];
    _player.delegate = self;
    _player.scaleFactor = 0;

    VLCMedia *media = [VLCMedia mediaWithURL:uri];
    for (NSString* option in _options) {
        [media addOption:[option stringByReplacingOccurrencesOfString:@"--" withString:@""]];
    }
    _player.media = media;
    [[AVAudioSession sharedInstance] setActive:NO withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:nil];
    if (_paused) {
        [_player pause];
    } else {
        [_player play];
    }
}

-(void)setSource:(NSDictionary *)source {
    NSString* uri    = [source objectForKey:@"uri"];
    NSDictionary* options = [source objectForKey:@"options"];
    _src = uri;
    _options = options;
    [self createPlayer];
}

-(void)setTime:(int)time {
    if (_player) {
        VLCTime *newTime = [VLCTime timeWithNumber:@(time)];
        [_player setTime:newTime];
    }
}

-(void)setRate:(float)rate {
    if (_player) {
        [_player setRate:rate];
    }
}

-(void)setPaused:(BOOL)paused {
    _paused = paused;
    if (_player) {
        if (_paused) {
            [_player pause];
        } else {
            [_player play];
        }
    }
}

-(void)setAspectRatio:(NSString*)aspectRatio {
    if (_player) {
        char *char_content = [aspectRatio cStringUsingEncoding:NSASCIIStringEncoding];
        [_player setVideoAspectRatio:char_content];
    }
}

-(void)setVolume:(int)volume {
    if (_player) {
        VLCAudio *audio = _player.audio;
        audio.volume = volume;
    }
}

#pragma mark - Player
/**
 * Sent by the default notification center whenever the player's state has changed.
 * \details Discussion The value of aNotification is always an VLCMediaPlayerStateChanged notification. You can retrieve
 * the VLCMediaPlayer object in question by sending object to aNotification.
 */
- (void)mediaPlayerStateChanged:(NSNotification *)aNotification {
    if (_player) {
        VLCMediaPlayerState state = _player.state;
        switch (state) {
            case VLCMediaPlayerStateStopped:        ///< Player has stopped
                if (!_isLoadDone) {
                    // 加载失败
                    [self vlcNotification:266];
                }
                [self vlcNotification:262];
                break;
            case VLCMediaPlayerStateOpening:
                
                break;
            case VLCMediaPlayerStateBuffering:      ///< Stream is buffering
                [self vlcNotification:259];
                break;
            case VLCMediaPlayerStateEnded:          ///< Stream has ended
                [self vlcNotification:265];
                break;
            case VLCMediaPlayerStateError:          ///< Player has generated an error
                [self vlcNotification:266];
                break;
            case VLCMediaPlayerStatePlaying:        ///< Stream is playing
                if (!_isLoadDone) {
                    _isLoadDone = YES;
                }
                [self vlcNotification:260];
                break;
            case VLCMediaPlayerStatePaused:         ///< Stream is paused
                [self vlcNotification:261];
                break;
            case VLCMediaPlayerStateESAdded:
                
                break;
        }
    }
}

/**
 * Sent by the default notification center whenever the player's time has changed.
 * \details Discussion The value of aNotification is always an VLCMediaPlayerTimeChanged notification. You can retrieve
 * the VLCMediaPlayer object in question by sending object to aNotification.
 */
- (void)mediaPlayerTimeChanged:(NSNotification *)aNotification {
    if (!_isLoadDone) {
        _isLoadDone = YES;
    }
    [self vlcNotification:268];
}

- (void)vlcNotification:(int)type {
    if (_player) {
        int currentTime   = [[_player time] intValue];
        int duration      = [_player.media.length intValue];
        self.onEventVlc(@{
            @"type": @(type),
            @"duration": [NSNumber numberWithInt:duration],
            @"currentTime": [NSNumber numberWithInt:currentTime],
            @"position": [NSNumber numberWithFloat:_player.position]
        });
    }
}

#pragma mark - Lifecycle
- (void) removeFromSuperview {
    if(_player) {
        [self releasePlayer];
    }
    _eventDispatcher = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super removeFromSuperview];
}

@end
