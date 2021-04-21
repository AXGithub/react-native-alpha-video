# react-native-alpha-video

## Getting started

`$ npm install react-native-alpha-video --save`

### Mostly automatic installation

`$ react-native link react-native-alpha-video`

## 视图
#### AlphaVideoView
`AlphaVideoView用来渲染动画，扩展UIView`

## 事件调用
#### AlphaVideoModule
##### 获取本地地址
`AlphaVideoModule.getAssets(require('./wwww.mp4'))`

##### 预加载缓存
`AlphaVideoModule.advanceDownload(list)`
## 属性
`URL，或是本地 NSBundle.mainBundle / assets 文件--- string类型`
#### source
`是否循环播放,默认不循环--- bool类型`
#### loop
`是否静音,默认不静音--- bool类型`
#### muted
## 回调
`视频播放完成后，回调`
#### onDidPlayFinish
## 事件
##### 播放
```javascript
play:() => void
```
##### 暂停
```javascript
pause:() => void
```
##### 停止
```javascript
stop:() => void
```
##### 释放
```javascript
clear:() => void
```
#### 预加载
```javascript
advanceDownload(urls: Array<String>): void
```

## Usage
```javascript
import AlphaVideo from 'react-native-alpha-video';

// TODO: What to do with the module?
AlphaVideo;
```
## 注意点
iOS使用需要在项目中创建一个metal文件`filter.metal`文件内容
![image](https://gitee.com/ZLforever/source/raw/master/image1.png)
```javascript
#include <metal_stdlib>
using namespace metal;
#include <CoreImage/CoreImage.h>

extern "C" { namespace  coreimage {
    float4 maskVideoMetal(sample_t s,sample_t m){
        return float4(s.rgb,m.r);
        }
    }
}

```
然后在Build Setting中搜索meta,如下
![image](https://gitee.com/JedShi/asstes-clone/raw/master/pic/image.png)