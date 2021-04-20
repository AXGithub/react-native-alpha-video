//
//  AlphaVideoManager.m
//  AlphaVideo
//
//  Created by koren on 2021/4/14.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import "AlphaVideoManager.h"
#import "AlphaVideoPlayerLayer.h"
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <AVFoundation/AVFoundation.h>

@implementation AlphaVideoManager

RCT_EXPORT_MODULE(RNAlphaVideoManager)

/** 视图初始化加载 */
- (UIView *)view{
    return [[AlphaVideoPlayerLayer alloc] initWithBridge:self.bridge];
}

/** 线程队列创建 */
- (dispatch_queue_t)methodQueue{
    return self.bridge.uiManager.methodQueue;
}

/** 指定在主线程初始化 */
+ (BOOL)requiresMainQueueSetup{
    return YES;
}

/** 是否静音 */
RCT_EXPORT_VIEW_PROPERTY(muted, NSInteger);
/** 是否循环 */
RCT_EXPORT_VIEW_PROPERTY(loop, BOOL);
/** 视频数据源 */
RCT_EXPORT_VIEW_PROPERTY(source, NSString);
/** 视频播放回调 */
RCT_EXPORT_VIEW_PROPERTY(onDidPlayFinish, RCTDirectEventBlock);
/** 视频播放 */
RCT_EXPORT_METHOD(play:(nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,AlphaVideoPlayerLayer *> *viewRegistry) {
        if (reactTag == nil) {
            RCTLogError(@"Invalid view returned from registry, expecting AlphaVideoPlayerLayer");
        } else {
            AlphaVideoPlayerLayer *layerView = viewRegistry[reactTag];
            if (![layerView isKindOfClass:[AlphaVideoPlayerLayer class]]) {
                RCTLogError(@"Invalid view returned from registry, expecting ScanCode, got: %@", layerView);
            } else {
                [layerView play];
            }
        }
    }];
}
/** 视频暂停 */
RCT_EXPORT_METHOD(pause:(nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,AlphaVideoPlayerLayer *> *viewRegistry) {
        if (reactTag == nil) {
            RCTLogError(@"Invalid view returned from registry, expecting AlphaVideoPlayerLayer");
        } else {
            AlphaVideoPlayerLayer *layerView = viewRegistry[reactTag];
            if (![layerView isKindOfClass:[AlphaVideoPlayerLayer class]]) {
                RCTLogError(@"Invalid view returned from registry, expecting ScanCode, got: %@", layerView);
            } else {
                [layerView pause];
            }
        }
    }];
}
/** 释放 */
RCT_EXPORT_METHOD(clear:(nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,AlphaVideoPlayerLayer *> *viewRegistry) {
        if (reactTag == nil) {
            RCTLogError(@"Invalid view returned from registry, expecting AlphaVideoPlayerLayer");
        } else {
            AlphaVideoPlayerLayer *layerView = viewRegistry[reactTag];
            if (![layerView isKindOfClass:[AlphaVideoPlayerLayer class]]) {
                RCTLogError(@"Invalid view returned from registry, expecting ScanCode, got: %@", layerView);
            } else {
                [layerView clear];
            }
        }
    }];
}

/** 预加载缓存 */
RCT_EXPORT_METHOD(advanceDownload:(NSArray *)cacheUrls) {
    dispatch_async(dispatch_get_main_queue(), ^{
        AlphaVideoPlayerLayer *layerView = [AlphaVideoPlayerLayer new];
        [layerView advanceDownload:cacheUrls];
    });
}

@end
