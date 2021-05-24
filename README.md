# ArcFace4.0
基于[虹软人脸识别](https://ai.arcsoft.com.cn/)增值版Android SDK V4.0,封装人脸识别方法。支持口罩识别。
> [增值版Android SDK V4.0文档](https://github.com/shenbengit/ArcFace4.0/blob/master/ARCSOFT_ARC_FACE_DEVELOPER'S_GUIDE_V4.0.pdf)    
> [虹软人脸识别3.1封装](https://github.com/shenbengit/ArcFace)

## 效果展示
限制识别区域

![ArcFace](https://github.com/shenbengit/ArcFace/blob/master/screenshots/ArcFace01.gif)

不限制识别区域

![ArcFace](https://github.com/shenbengit/ArcFace/blob/master/screenshots/ArcFace02.gif)
### 将JitPack存储库添加到您的项目中(项目根目录下build.gradle文件)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### 添加依赖
[![](https://jitpack.io/v/shenbengit/ArcFace4.svg)](https://jitpack.io/#shenbengit/ArcFace4)
```gradle
dependencies {
    //必选(armeabi-v7a|arm64-v8a)至少添加一个
    implementation 'com.github.shenbengit.ArcFace4:lib:Tag'
    //可选，支持armeabi-v7a
    implementation 'com.github.shenbengit.ArcFace4:lib-v7a:Tag'
    //可选，支持arm64-v8a
    implementation 'com.github.shenbengit.ArcFace4:lib-v8a:Tag'
}
```

项目内依赖[Fotoapparat](https://github.com/RedApparat/Fotoapparat)用于摄像头预览。

## 使用事例

布局示例
```Xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.shencoder.arcface.view.FaceCameraView
        android:id="@+id/faceCameraView"
        android:layout_width="match_parent"
        android:layout_height="450dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

代码示例
>  基本使用
```kotlin
    val faceCameraView: FaceCameraView = findViewById(R.id.faceCameraView)
    val configuration = FaceConfiguration.builder(this, object : OnRecognizeCallback {
        /**
         * 检测到的人脸数量
         * <p>运行在子线程</p>
         *
         * @param num 人脸数量
         * @param faceIds faceId
         */
        override fun detectFaceNum(num: Int, faceIds: List<Int>) {

        }

        /**
         * 有人，仅在有变化时调用一次
         * <p>运行在子线程</p>
         */
        override fun someone() {

        }

        /**
         * 无人，仅在有变化时调用一次
         * <p>运行在子线程</p>
         */
        override fun nobody() {

        }

        /**
         * 如果不想自动比对的话，可以通过此接口返回识别到的人脸特征码，仅在[FaceConfiguration.enableCompareFace] 为false时才会回调
         * <p>运行在子线程</p>
         *
         * @param faceId 人脸Id
         * @param feature 人脸特征码
         * @param recognizeInfo 识别到的其他信息，包含活体值、年龄、性别、人脸角度等信息
         * @param camera 预览数据
         * @param width 预览数据宽度
         * @param height 预览数据高度
         */
         override fun onGetFaceFeature(
             faceId: Int,
             feature: ByteArray,
             recognizeInfo: RecognizeInfo,
             nv21: ByteArray,
             width: Int,
             height: Int
         ) {
             
         }

        /**
         * 识别成功后结果回调，仅回调一次，直到人脸离开画面
         * <p>运行在子线程</p>
         *
         * @param bean 识别的数据 [faceFeatureList] 的子项
         * @param similar 识别通过的相似度
         * @param recognizeInfo 识别到的其他信息，包含活体值、年龄、性别、人脸角度等信息
         * @param camera 预览数据
         * @param width 预览数据宽度
         * @param height 预览数据高度
         * 
         * @return 人脸绘制框上成功时绘制的文字
         */
        override fun onRecognized(
            bean: FaceFeatureDataBean,
            similar: Float,
            recognizeInfo: RecognizeInfo,
            nv21: ByteArray,
            width: Int,
            height: Int
        ): String? {
            println("人脸比对成功-相似度:" + similar + ",recognizeInfo:" + recognizeInfo.toString())
            return "识别成功"
        }

        /**
         * 识别相似度阈值，有效值范围(0.0f,1.0f)
         */
        override fun similarThreshold(): Float {
            return 0.8f
        }

        /**
         * 待比较人脸数据集合，需要自己封装传入
         */
        override fun faceFeatureList(): List<FaceFeatureDataBean> {
            return listOf()
        }

    })
        .setDetectFaceOrient(DetectFaceOrientPriority.ASF_OP_0_ONLY)//人脸检测角度
        .enableRecognize(true)//是否需要识别
        .setDetectFaceScaleVal(30)//用于数值化表示的最小人脸尺寸，该尺寸代表人脸尺寸相对于图片长边的占比。
        .setLivenessType(LivenessType.IR)//活体检测类型
        .setRgbLivenessThreshold(0.6f)//设置RGB可见光活体阈值
        .setIrLivenessThreshold(0.7f)//设置IR红外活体阈值
        .enableImageQuality(true)//是否启用图像质量阈值
        .setImageQualityThreshold(0.35f)//图像质量阈值
        .setDetectFaceMaxNum(1)//最大需要检测的人脸个数
        .recognizeKeepMaxFace(true)//是否仅识别最大人脸
        .enableRecognizeAreaLimited(false)//是否限制识别区域
        .setRecognizeAreaLimitedRatio(0.7f)//识别区域屏占比，正方形，位置在预览画面正中间
        .setDetectInfo(
            DetectInfo(
                age = true,
                gender = true,
                angle = true
            )
         )//相关属性检测，年龄、性别、3d角度这个功能依附于 [livenessType]，需要为[LivenessType.RGB]
         .setRgbCameraFcing(CameraFacing.BACK)//彩色RGB摄像头类型
         .setIrCameraFcing(CameraFacing.FRONT)//红外IR摄像头类型，目前不支持
         .setPreviewSize(PreviewSize(1280, 720))//摄像头预览分辨率，彩色摄像头和红外都支持的预览分辨率
         .setDrawFaceRect(
             DrawFaceRect(
                 isDraw = true,
                 unknownColor = Color.YELLOW,
                 failedColor = Color.RED,
                 successColor = Color.GREEN,
                 rgbOffsetX = 0,
                 rgbOffsetY = 0,
                 irOffsetX = -15,
                 irOffsetY = 0,
              )
         )//人脸识别框绘制相关
         .isRgbMirror(true)//RGB预览画面是否镜像
         .isIrMirror(false)//IR预览画面是否镜像
         .setExtractFeatureErrorRetryCount(3)//人脸特征提取出错重试次数，超过置为失败状态
         .setRecognizeFailedRetryInterval(1000)//人脸识别失败后，重试间隔，单位：毫秒
         .setLivenessErrorRetryCount(3)//体检测出错重试次数
         .setLivenessFailedRetryInterval(1000)//活体检测失败后，重试间隔，单位：毫秒
         .enableCompareFace(true)//是否启用人脸比对
         .setViewfinderText("请将人脸置于识别框内")
         .setViewfinderGravity(ViewfinderView.TextLocation.BOTTOM)
         .enableMask(true)//是否启用口罩识别
         .setFaceSizeLimit(160)//人脸大小限制
         .setImageQualityNoMaskRecognizeThreshold(0.49f)//图像质量检测阈值：适用于不戴口罩且人脸识别场景
         .setImageQualityMaskRecognizeThreshold(0.29f)//图像质量检测阈值：适用于戴口罩且人脸识别场景
         .setOnErrorCallback(object : OnErrorCallback {
             override fun onError(type: FaceErrorType, errorCode: Int, errorMessage: String) {
                
             }
         })//识别中错误回调
         .build()
    //设置人脸相关参数，如果确认人脸已经激活且直接进行人脸识别则设备true
    faceCameraView.setConfiguration(configuration, false)
    faceCameraView.setLifecycleOwner(this)
    //摄像头开启异常监听
    faceCameraView.setOnCameraListener(object : OnCameraListener {
            override fun onRgbCameraError(exception: Exception) {

            }

            override fun onIrCameraError(exception: Exception) {
                
            }
     })
    //在合适的地方调用此方法，设置为true且人脸已激活才会提交预览数据
    faceCameraView.enableFace(true)
```
>  虹软人脸激活相关 FaceActive
* 在线激活
```kotlin
FaceActive.activeOnline(context: Context, activeKey: String, appId: String, sdkKey: String, callback: OnActiveCallback?)
```
* 离线激活
```kotlin
FaceActive.activeOffline(context: Context, filePath: String,callback: OnActiveCallback?)
```
* 是否已经激活人脸
```kotlin
FaceActive.isActivated(context: Context): Boolean
```
* 生成设备指纹信息，用于离线激活（请自行获取内存卡读写权限）
```kotlin
FaceActive.generateActiveDeviceInfo(context: Context, saveFilePath: String, callback: OnActiveDeviceInfoCallback?)
```
>  人脸比对和生成特征码相关 FaceServer (可以自行封装成单例模式)
* 初始化
```kotlin
/**
  * 初始化人脸引擎
  * @param context 上下文
  * @param faceOrient 人脸检测角度，单一角度检测，不支持[DetectFaceOrientPriority.ASF_OP_ALL_OUT]
  * [DetectFaceOrientPriority.ASF_OP_0_ONLY]
  * [DetectFaceOrientPriority.ASF_OP_90_ONLY]
  * [DetectFaceOrientPriority.ASF_OP_180_ONLY]
  * [DetectFaceOrientPriority.ASF_OP_270_ONLY]
  */
fun init(context: Context, faceOrient: DetectFaceOrientPriority) 
```
* 比对人脸 1:N
```kotlin
/**
  * 比对人脸 1:N
  * @param faceFeature 要比对的人脸特征码
  * @param features 待比对的人脸列表
  * 
  * @return null:说明比对列表为空或者人脸引擎出错；返回相似度最大的[features]中的数据
  */
fun compareFaceFeature(faceFeature: FaceFeature, features: List<FaceFeatureDataBean>): CompareResult?
```
* 比对人脸 1:1
```kotlin
/**
  * 比对两组特征码 1:1
  * 
  * @return 返回相似度
  */
fun compareFaceFeature(feature1: ByteArray, feature2: ByteArray): Float
```
* 通过Bitmap提取特征码
```kotlin
/**
  * 通过Bitmap提取特征码
  * 最好在子线程运行
  * 
  * @return 特征码
  */
fun extractFaceFeature(bitmap: Bitmap?): ByteArray?
```
* 通过nv21数据提取特征码
```kotlin
/**
  * 摄像机预览数据提取人脸特征码
  * 最好在子线程运行
  * @param nv21 摄像机数据
  * @param width 预览宽度
  * @param height 预览高度
  * 
  * @return 特征码
  */
fun extractFaceFeature(nv21: ByteArray, width: Int, height: Int): ByteArray?
```
* 销毁资源
```kotlin
/**
  * 销毁资源
  */
fun destroy()
```
>  人脸检测 FaceDetect (可以自行封装成单例模式)
* 初始化
```kotlin
/**
  * 初始化人脸引擎
  * @param context 上下文
  * @param enableImageQuality 启用图片质量检测
  * @param detectFaceMaxNum 检测人脸数量
  * @param detectFaceOrient 检测人脸方向
  */
fun init(
    context: Context,
    enableImageQuality: Boolean = false,
    detectFaceMaxNum: Int,
    detectFaceOrient: DetectFaceOrientPriority
)
```
* 人脸检测回调
```kotlin
/**
  * 人脸检测回调
  */
fun setFaceDetectCallback(callback: FaceDetectCallback?)
```
* 销毁资源
```kotlin
* 传入预览数据
/**
  * 传入预览数据
  */
fun onPreviewFrame(rgbNV21: ByteArray,previewWidth: Int,previewHeight: Int)
```
* 销毁资源
```kotlin
/**
  * 销毁资源
  */
fun destroy()
```
>  人脸特征码转换工具 FeatureCovertUtil
* ByteArray特征码数据转为16进制字符串
```kotlin
FeatureCovertUtil.byteArrayToHexString(feature: ByteArray): String
```
* 16进制字符串转为ByteArray特征码数据
```kotlin
FeatureCovertUtil.hexStringToByteArray(hexStr: String): ByteArray
```
