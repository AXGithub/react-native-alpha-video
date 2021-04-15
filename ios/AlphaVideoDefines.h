//
//  AlphaVideoDefines.h
//  AlphaVideo
//
//  Created by koren on 2021/4/14.
//  Copyright © 2021 Facebook. All rights reserved.
//

#ifndef AlphaVideoDefines_h
#define AlphaVideoDefines_h

#import <CoreImage/CoreImage.h>

static CIColorKernel *videoKernel = nil;

typedef NS_ENUM(NSUInteger,AlphaVideoMaskDirection){
    alphaVideoMaskDirectionLeftToRight = 1<<1,//白幕在左 实际效果在右
    alphaVideoMaskDirectionRightToLeft = 1<<2,//白幕在右 实际效果在左
    alphaVideoMaskDirectionTopToBottom = 1<3,//白幕在上 实际效果在下
    alphaVideoMaskDirectionBottomToTop = 1<<4//白幕在下  实际效果在上
};

#endif /* AlphaVideoDefines_h */
