//
//  filter.metal
//  AlphaVideo
//
//  Created by koren on 2021/4/21.
//  Copyright © 2021 Facebook. All rights reserved.
//

#include <metal_stdlib>
using namespace metal;


#include <CoreImage/CoreImage.h>

extern "C" { namespace  coreimage {
    float4 maskVideoMetal(sample_t s,sample_t m){
        return float4(s.rgb,m.r);
        }
    }
}
