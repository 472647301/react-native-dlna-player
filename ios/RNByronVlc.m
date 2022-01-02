#import "RNByronVlc.h"
#import <IJKMediaFrameworkWithSSL/IJKMediaFrameworkWithSSL.h>
#import <AVFoundation/AVFoundation.h>

@implementation RNByronVlc {
    RCTEventDispatcher *_eventDispatcher;
    IJKFFMoviePlayerController *_player;
    id _timeObserver;
    NSInteger _width;
    NSInteger _height;
    float _volume;
    BOOL _paused;
}

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher {
    if ((self = [super init])) {
        _eventDispatcher = eventDispatcher;
        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
        [defaultCenter addObserver:self
                          selector:@selector(applicationWillResignActive:)
                              name:UIApplicationWillResignActiveNotification
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(applicationWillEnterForeground:)
                              name:UIApplicationWillEnterForegroundNotification
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(loadStateDidChange:)
                              name:IJKMPMoviePlayerLoadStateDidChangeNotification
                            object:_player];
        [defaultCenter addObserver:self
                          selector:@selector(moviePlayBackDidFinish:)
                              name:IJKMPMoviePlayerPlaybackDidFinishNotification
                            object:_player];
        [defaultCenter addObserver:self
                          selector:@selector(mediaIsPreparedToPlayDidChange:)
                              name:IJKMPMediaPlaybackIsPreparedToPlayDidChangeNotification
                            object:_player];
        [defaultCenter addObserver:self
                          selector:@selector(moviePlayBackStateDidChange:)
                              name:IJKMPMoviePlayerPlaybackStateDidChangeNotification
                            object:_player];
        [defaultCenter addObserver:self
                          selector:@selector(movieSeekDidComplete:)
                              name:IJKMPMoviePlayerDidSeekCompleteNotification
                            object:_player];
    }
    return self;
}

-(void)setSrc:(NSDictionary *)source {
    if (_player) {
        [_player shutdown];
        _player = nil;
        self.onVideoSwitch(nil);
    }
    NSString* uri = [source objectForKey:@"uri"];
    NSDictionary* headers = [source objectForKey:@"headers"];
    NSString* userAgent = [source objectForKey:@"userAgent"];
    NSArray* headerKeys = [headers allKeys];
    IJKFFOptions *options = [IJKFFOptions optionsByDefault];
    [options setOptionIntValue:1 forKey:@"infbuf" ofCategory:kIJKFFOptionCategoryPlayer];
    [options setOptionIntValue:0 forKey:@"packet-buffering" ofCategory:kIJKFFOptionCategoryPlayer];
    for (NSString * key in headerKeys) {
        [options setFormatOptionValue:headers[key] forKey:key];
    }
    if(userAgent) {
        [options setFormatOptionValue:userAgent forKey:@"user-agent"];
    }
    _player = [[IJKFFMoviePlayerController alloc] initWithContentURLString:uri withOptions:options];
    _player.view.frame = self.bounds;
    _player.scalingMode = IJKMPMovieScalingModeAspectFit;
    _player.shouldAutoplay = YES;
    self.autoresizesSubviews = YES;
    [self addSubview:_player.view];
    if(!_timeObserver) {
        _timeObserver = [NSTimer scheduledTimerWithTimeInterval: 1 target: self
                                                       selector: @selector(onProgressUpdate)
                                                       userInfo: nil repeats: YES];
    }
    // https://github.com/bilibili/ijkplayer/issues/4916
    [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord
    withOptions:AVAudioSessionCategoryOptionDefaultToSpeaker | AVAudioSessionCategoryOptionMixWithOthers
                                           error:nil];
    [_player prepareToPlay];
}

- (void)setWidth:(NSInteger)width {
    _width = width;
    if (self.frame.size.width != width) {
        CGRect frame = self.frame;
        frame.size.width = width;
        self.frame = frame;
        _player.view.frame = frame;
    }
}

- (void)setHeight:(NSInteger)height {
    _height = height;
    if (self.frame.size.height != height) {
        CGRect frame = self.frame;
        frame.size.height = height;
        self.frame = frame;
        _player.view.frame = frame;
    }
}

- (void)setPaused:(BOOL)paused {
    _paused = paused;
    if(_player && _player.isPreparedToPlay) {
        if (paused) {
            [_player pause];
        } else {
            [_player play];
        }
    }
}

-(void)setSeek:(float)seek {
    if(_player && _player.isPreparedToPlay) {
        [_player setCurrentPlaybackTime:[_player duration] * seek];
    }
}

-(void)setVolume:(float)volume {
    _volume = volume;
    if(_player && _player.isPreparedToPlay) {
        [_player setPlaybackVolume:volume];
    }
}

- (void)setMuted:(BOOL)muted {
    if(_player && _player.isPreparedToPlay) {
        if (muted) {
            [_player setPlaybackVolume:0.0];
        } else {
            [_player setPlaybackVolume:_volume];
        }
    }
}

- (void)onProgressUpdate {
    if(_player && _player.isPlaying) {
        int currentTime   = [_player currentPlaybackTime] * 1000;
        int duration      = [_player duration] * 1000;
        self.onVideoProgress(@{
            @"currentTime": @(currentTime),
            @"duration": @(duration)
        });
    }
}

#pragma mark - Notification
- (void)applicationWillResignActive:(NSNotification *)notification {
    if (!_paused) {
        [_player play];
    }
}

- (void)applicationWillEnterForeground:(NSNotification *)notification {
    if (!_paused) {
        [_player play];
    }
}

- (void)loadStateDidChange:(NSNotification *)notification {
    IJKMPMovieLoadState loadState = _player.loadState;
    if ((loadState & IJKMPMovieLoadStatePlaythroughOK) != 0) {
        int currentTime   = [_player currentPlaybackTime] * 1000;
        int duration      = [_player duration] * 1000;
        self.onVideoStart(@{
            @"currentTime": @(currentTime),
            @"duration": @(duration)
        });
    } else if ((loadState & IJKMPMovieLoadStateStalled) != 0) {
        self.onVideoBuffer(nil);
    } else {
        self.onVideoError(nil);
    }
}

- (void)moviePlayBackDidFinish:(NSNotification *)notification {
    int reason = [[[notification userInfo] valueForKey:IJKMPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue];
    switch (reason) {
        case IJKMPMovieFinishReasonPlaybackEnded:
            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackEnded: %d\n", reason);
            int duration1      = [_player duration] * 1000;
            self.onVideoProgress(@{
                @"currentTime": @(duration1),
                @"duration": @(duration1)
            });
            self.onVideoEnd(nil);
            break;
        case IJKMPMovieFinishReasonUserExited:
            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonUserExited: %d\n", reason);
            int duration2      = [_player duration] * 1000;
            self.onVideoProgress(@{
                @"currentTime": @(duration2),
                @"duration": @(duration2)
            });
            self.onVideoEnd(nil);
            break;
        case IJKMPMovieFinishReasonPlaybackError:
            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackError: %d\n", reason);
            self.onVideoError(nil);
            break;
        default:
            NSLog(@"playbackPlayBackDidFinish: ???: %d\n", reason);
            break;
    }
}

- (void)mediaIsPreparedToPlayDidChange:(NSNotification*)notification {
    NSLog(@"mediaIsPreparedToPlayDidChange");
}

- (void)moviePlayBackStateDidChange:(NSNotification*)notification {
    switch (_player.playbackState) {
        case IJKMPMoviePlaybackStateStopped: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: stopped", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStatePlaying: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: playing", (int)_player.playbackState);
            self.onVideoPaused(@{@"paused": @(NO)});
            break;
        }
        case IJKMPMoviePlaybackStatePaused: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: paused", (int)_player.playbackState);
            self.onVideoPaused(@{@"paused": @(YES)});
            break;
        }
        case IJKMPMoviePlaybackStateInterrupted: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: interrupted", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStateSeekingForward:
        case IJKMPMoviePlaybackStateSeekingBackward: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: seeking", (int)_player.playbackState);
            break;
        }
        default: {
            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: unknown", (int)_player.playbackState);
            break;
        }
    }
}

- (void)movieSeekDidComplete:(NSNotification*)notification {
    if(!_paused) {
        [_player play];
    }
}

#pragma mark - Lifecycle
- (void) removeFromSuperview {
    if(_player) {
        [_player shutdown];
    }
    if(_timeObserver) {
        [_timeObserver invalidate];
        _timeObserver = nil;
    }
    _eventDispatcher = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super removeFromSuperview];
}

@end
