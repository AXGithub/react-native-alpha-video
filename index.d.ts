declare module 'react-native-alpha-video' {
    import React from 'react'
    import { ViewProps } from 'react-native'

    export interface IAlphaViewProps extends ViewProps {
        // 视频播放完成后，回调
        onDidPlayFinish?: () => void
        // URL，或是本地 NSBundle.mainBundle / assets 文件
        source?: string
        // 默认值为 0，用于指定动画循环次数，0 = 无限循环
        loop?: number
    }
}
