//
//  AlphaVideoPlayerLayer.m
//  AlphaVideo
//
//  Created by koren on 2021/4/14.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "AlphaVideoPlayerLayer.h"
#import <CommonCrypto/CommonDigest.h>

@interface AlphaVideoPlayerLayer ()
{
    NSInteger _playCount;
}

/// 视频播放器
@property (nonatomic, strong) AVPlayer *videoPlayer;

/// playItem
@property (nonatomic, strong) AVPlayerItem *playItem;

/// playerLayer
@property (nonatomic, strong) AVPlayerLayer *playerLayer;

@end

@implementation AlphaVideoPlayerLayer

static NSOperationQueue *cacheQueue;

+ (void)load {
    cacheQueue = [NSOperationQueue new];
    cacheQueue.maxConcurrentOperationCount = 3;
}

/// 初始化播放器
- (AVPlayer *)videoPlayer{
    if (!_videoPlayer) {
        _videoPlayer = [[AVPlayer alloc] init];
    }
    return _videoPlayer;
}

- (AVPlayerLayer *)playerLayer{
    if (!_playerLayer) {
        _playerLayer = [AVPlayerLayer layer];
    }
    return _playerLayer;
}

/// 初始化
- (id)initWithBridge:(RCTBridge *)bridge{
    if (self = [super init]) {
        // 默认不静音
        _muted = NO;
        // 默认播放一次，不循环
        _loop = NO;
        // 默认视频白幕在左 实际效果在右
        _maskDirection = alphaVideoMaskDirectionLeftToRight;
        // 默认静音或者锁屏模式下静音且不引起混音App中断
        _isAmbient = YES;
        
        // 设置像素格式
        self.playerLayer.pixelBufferAttributes = @{@"PixelFormatType":@(kCMPixelFormat_32BGRA)};
        // 视频填充样式
        self.playerLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
    }
    return self;
}


/** 设置视图大小 */
- (void)layoutSubviews{
    [super layoutSubviews];
    
    self.playerLayer.needsDisplayOnBoundsChange = YES;
    self.playerLayer.frame = self.bounds;
    [self.layer insertSublayer:self.playerLayer atIndex:0];
}

/// 加载视频URL
/// @param source 视频数据源
- (void)setSource:(NSString *)source{
    if ([source isKindOfClass:[NSString class]] && ![source isEqualToString:@""] && source != nil) {
        if ([source hasPrefix:@"http://"] || [source hasPrefix:@"https://"]) {
            NSURLRequest *URLRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:source] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:20.0];
            if (URLRequest.URL == nil) {
                return;
            }
            if ([[NSFileManager defaultManager] fileExistsAtPath:[self cacheDirectory:[self cacheKey:URLRequest.URL]]]) {
                [self initLoadWithSource:[NSURL fileURLWithPath:[self cacheDirectory:[self cacheKey:URLRequest.URL]]]];
            } else {
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    NSData *videoData = [NSData dataWithContentsOfURL:[NSURL URLWithString:source]];
                    if (videoData) {
                        if ([self ensureDirExistsWithPath:[[self cacheDirectoryPath] stringByAppendingPathComponent:@"alphaVideo"]]) {
                            // 写入沙盒数据
                            [videoData writeToFile:[self cacheDirectory:[self cacheKey:URLRequest.URL]] atomically:YES];
                            [self initLoadWithSource:[NSURL fileURLWithPath:[self cacheDirectory:[self cacheKey:URLRequest.URL]]]];
                        } else {
                            NSLog(@"文件目录不存在");
                        }
                    } else {
                        NSLog(@"视频数据不存在");
                    }
                });
            }
            
        } else {
            [self initLoadWithSource:[NSURL URLWithString:source]];
        }
    }
}

- (void)initLoadWithSource:(NSURL *)videoUrl{
    self.playerLayer.player = self.videoPlayer;
    AVURLAsset *videoAsset = [AVURLAsset assetWithURL:videoUrl];
    [videoAsset loadValuesAsynchronouslyForKeys:@[@"duration",@"tracks"] completionHandler:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            AVPlayerItem *playItem = [[AVPlayerItem alloc] initWithAsset:videoAsset];
            [self intilizaAudioTacks:self->_muted];
            [self intilizaPlayItem:playItem];
            [self play];
        });
    }];
}

/// 设置是否静音
/// @param muted muted
- (void)setMuted:(BOOL)muted{
    _muted = muted;
    if (_playItem) {
        [self intilizaAudioTacks:muted];
    }
}

/// 设置静音或者锁屏模式下静音且不引起混音App中断
- (void)setIsAmbient:(BOOL)isAmbient{
    _isAmbient = isAmbient;
    if (_playItem) {
        [self intilizaIsAmbient:isAmbient];
    }
}

/// 设置静音或者锁屏模式下静音且不引起混音App中断
- (void)intilizaIsAmbient:(BOOL)isAmbient{
    if (isAmbient) {
//        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryAmbient error:NULL];
    }
}

/// 设置音轨
/// @param muted 是否静音
- (void)intilizaAudioTacks:(BOOL)muted{
   
    NSArray *audioTracks = [_playItem.asset tracksWithMediaType:AVMediaTypeAudio];

    NSMutableArray *allAudioParams = [NSMutableArray array];
    for (AVAssetTrack *track in audioTracks) {
      AVMutableAudioMixInputParameters *audioInputParams =
        [AVMutableAudioMixInputParameters audioMixInputParameters];

        [audioInputParams setVolume:muted ? 0:[AVAudioSession sharedInstance].outputVolume atTime:kCMTimeZero];
      [audioInputParams setTrackID:[track trackID]];
      [allAudioParams addObject:audioInputParams];
    }

    AVMutableAudioMix *audioMix = [AVMutableAudioMix audioMix];
    [audioMix setInputParameters:allAudioParams];
    [_playItem setAudioMix:audioMix];
}

/// 设置  playerItem
/// @param playItem playItem
- (void)intilizaPlayItem:(AVPlayerItem *)playItem{
    
    [_videoPlayer seekToTime:kCMTimeZero];
    [self intilizaPlayItemComposition:playItem];
    [self intilizaItemObserver:playItem];
    [_videoPlayer replaceCurrentItemWithPlayerItem:playItem];
}

/// 设置 AVMutableVideoComposition
/// @param playItem playItem
- (void)intilizaPlayItemComposition:(AVPlayerItem *)playItem{
    //获取轨道
    NSArray <AVAssetTrack *> *assetTracks = playItem.asset.tracks;
#if DEBUG
    NSAssert(assetTracks, @"NO tracks please check video source");
#else
    if(!assetTracks.count)return;
#endif
    CGSize videoSize = CGSizeZero;
    switch (_maskDirection) {
        case alphaVideoMaskDirectionLeftToRight:
        case alphaVideoMaskDirectionRightToLeft:{
            videoSize = CGSizeMake(assetTracks.firstObject.naturalSize.width/2.f, assetTracks.firstObject.naturalSize.height);
        }
            break;
        case alphaVideoMaskDirectionTopToBottom:
        case alphaVideoMaskDirectionBottomToTop:
        {
            videoSize =   CGSizeMake(assetTracks.firstObject.naturalSize.width, assetTracks.firstObject.naturalSize.height/2.f);
        }
        default:
            break;
    }
#if DEBUG
    NSAssert(videoSize.width && videoSize.height, @"videoSize can't be zero");
#else
    if (!videoSize.width ||!videoSize.height) return;
#endif
    AVMutableVideoComposition *videoComposition = [AVMutableVideoComposition videoCompositionWithAsset:playItem.asset applyingCIFiltersWithHandler:^(AVAsynchronousCIImageFilteringRequest * _Nonnull request) {
        //source rect this is you will show
        CGRect sourceRect = (CGRect){0,0,videoSize.width,videoSize.height};
        CGRect alphaRect = CGRectZero;
        
        CGFloat dx;
        CGFloat dy;
        switch (self->_maskDirection) {
            case alphaVideoMaskDirectionLeftToRight:
            case alphaVideoMaskDirectionRightToLeft:{
                alphaRect = CGRectOffset(sourceRect, videoSize.width, 0);
                dx = -sourceRect.size.width;
                dy = 0;
            }
                break;
            case alphaVideoMaskDirectionTopToBottom:
            case alphaVideoMaskDirectionBottomToTop:
            {
                alphaRect = CGRectOffset(sourceRect, 0, videoSize.height);
                dx = 0;
                dy = -sourceRect.size.height;
            }
            default:
                break;
        }
     
        
        if (@available(iOS 11.0, *)) {
            if (!videoKernel) {
                NSURL *kernelURL = [[NSBundle mainBundle] URLForResource:@"default" withExtension:@"metallib"];
                NSError *error;
                NSData *kernelData = [NSData dataWithContentsOfURL:kernelURL];
                videoKernel = [CIColorKernel kernelWithFunctionName:@"maskVideoMetal" fromMetalLibraryData:kernelData error:&error];
                #if DEBUG
                NSAssert(!error, @"%@",error);
                #else
                #endif
            }
        } else {
            if (!videoKernel) {
                videoKernel = [CIColorKernel kernelWithString:@"kernel vec4 alphaFrame(__sample s, __sample m) {return vec4(s.rgb, m.r);}"];
            }
        }
        
        CIImage *inputImage;
        CIImage *maskImage;
        switch (self->_maskDirection) {
            case alphaVideoMaskDirectionLeftToRight:{
                inputImage = [[request.sourceImage imageByCroppingToRect:alphaRect] imageByApplyingTransform:CGAffineTransformMakeTranslation(dx, dy)];
                
                maskImage = [request.sourceImage imageByCroppingToRect:sourceRect];
            }
                break;
            case alphaVideoMaskDirectionRightToLeft:{
                inputImage = [request.sourceImage imageByCroppingToRect:sourceRect];
                maskImage = [[request.sourceImage imageByCroppingToRect:alphaRect] imageByApplyingTransform:CGAffineTransformMakeTranslation(dx, dy)];
            }
                break;
            case alphaVideoMaskDirectionTopToBottom:{
                inputImage = [request.sourceImage imageByCroppingToRect:sourceRect];
                maskImage = [[request.sourceImage imageByCroppingToRect:alphaRect] imageByApplyingTransform:CGAffineTransformMakeTranslation(dx, dy)];
            }
                break;
            case alphaVideoMaskDirectionBottomToTop:{
                inputImage = [[request.sourceImage imageByCroppingToRect:alphaRect] imageByApplyingTransform:CGAffineTransformMakeTranslation(dx, dy)];
                maskImage = [request.sourceImage imageByCroppingToRect:sourceRect];
            }
            default:
                break;
        }
        
        CIImage *outPutImage = [videoKernel applyWithExtent:inputImage.extent arguments:@[(id)inputImage,(id)maskImage]];
        [request finishWithImage:outPutImage context:nil];
    }];
    videoComposition.renderSize = videoSize;
    playItem.videoComposition = videoComposition;
    playItem.seekingWaitsForVideoCompositionRendering = YES;
}

/// 视频播放完成回调
-(void)videoDidPlayFihisn{
    [_videoPlayer seekToTime:kCMTimeZero completionHandler:^(BOOL finished) {
        if (finished) {
            if (self->_loop) {
                [self play];
            } else {
                [self didFinishPlay];
            }
        } else {
            [self didFinishPlay];
        }
        
    }];
}

/// 视频播放完成
-(void)didFinishPlay{
    [self clear];
    if (self.onDidPlayFinish) {
        self.onDidPlayFinish(@{});
    }
}

/// 设置observer-监听视频是否播放完成
/// @param playItem playItem
-(void)intilizaItemObserver:(AVPlayerItem *)playItem{
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(videoDidPlayFihisn) name:AVPlayerItemDidPlayToEndTimeNotification object:playItem];
}

/// 释放
- (void)clear{
    [self pause];
    [_videoPlayer.currentItem cancelPendingSeeks];
    [_videoPlayer.currentItem.asset cancelLoading];
    [_videoPlayer replaceCurrentItemWithPlayerItem:nil];
    _videoPlayer = nil;

    if (_playItem) {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:AVPlayerItemDidPlayToEndTimeNotification object:_playItem];
        _playItem = nil;
    }
    cacheQueue = nil;
    [self.playerLayer removeFromSuperlayer];
    [self removeFromSuperview];
}

/// 播放
- (void)play{
    [_videoPlayer play];
    if (_isAmbient) {
        [self intilizaIsAmbient:_isAmbient];
    }
}

/// 暂停
- (void)pause{
    [_videoPlayer pause];
}

#pragma mark - 设置缓存

// 预缓存
- (void)advanceDownload:(NSArray *)urls{
    if (urls.count <= 0) {
        return;
    }
    if (cacheQueue == nil) {
        cacheQueue = [NSOperationQueue new];
        cacheQueue.maxConcurrentOperationCount = 3;
    }
    [cacheQueue addOperationWithBlock:^{
        for (NSString *url in urls) {
            if ([url hasPrefix:@"https://"] || [url hasPrefix:@"http://"]) {
                NSData *videoData = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
                NSURLRequest *URLRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:20.0];
                if (videoData) {
                    if ([self ensureDirExistsWithPath:[[self cacheDirectoryPath] stringByAppendingPathComponent:@"alphaVideo"]]) {
                        // 写入沙盒数据
                        [videoData writeToFile:[self cacheDirectory:[self cacheKey:URLRequest.URL]] atomically:YES];
                    } else {
                        NSLog(@"文件目录不存在");
                    }
                } else {
                    NSLog(@"视频数据不存在");
                }
            }
        }
    }];
}

/// 获取沙盒路径
- (NSString *)cacheDirectoryPath {
    return [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
}

/// 判断是否存在这个路径,没有就创建
- (BOOL)ensureDirExistsWithPath:(NSString *)path {
  BOOL isDir = NO;
  NSError *error;
  BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDir];
  if (!(exists && isDir)) {
    [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
    if (error) {
      return NO;
    }
  }
  return YES;
}

/// 对url进行加密生成key
- (nonnull NSString *)cacheKey:(NSURL *)URL {
    return [self SHA256:URL.absoluteString];
}

/// 查找沙盒路径
- (nullable NSString *)cacheDirectory:(NSString *)cacheKey {
    NSString *cacheDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    return [cacheDir stringByAppendingFormat:@"/alphaVideo/%@.mp4", cacheKey];
}

/// SHA256加密算法
- (NSString *)SHA256:(NSString *)str {
    const char* string = [str UTF8String];
    unsigned char result[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(string, (CC_LONG)strlen(string), result);
    NSMutableString *ret = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH*2];
    for(int i = 0; i<CC_SHA256_DIGEST_LENGTH; i++)
    {
        [ret appendFormat:@"%02x",result[i]];
    }
    ret = (NSMutableString *)[ret uppercaseString];
    return ret;
}

@end
