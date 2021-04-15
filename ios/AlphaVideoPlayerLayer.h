//
//  AlphaVideoPlayerLayer.h
//  AlphaVideo
//
//  Created by koren on 2021/4/14.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "AlphaVideoDefines.h"
#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTComponent.h>


@interface AlphaVideoPlayerLayer : UIView

/// 播放完成回调
@property(nonatomic, copy) RCTBubblingEventBlock onDidPlayFinish;

/// 是否静音
@property (nonatomic, assign) BOOL muted;

/// 循环次数 默认为1次 <=0为无限循环
@property (nonatomic, assign) NSInteger loop;

/// 合成方向 默认为 白幕在左
@property (nonatomic, assign) AlphaVideoMaskDirection maskDirection;

/// 视频数据源
@property (nonatomic, copy) NSString *source;

/// 设置静音或者锁屏模式下静音且不引起混音App中断
@property (nonatomic, assign) BOOL isAmbient;

/// 初始化
- (id)initWithBridge:(RCTBridge *)bridge;

/// 视频播放
- (void)play;

/// 视频暂停
- (void)pause;

/// 释放
- (void)clear;

@end
