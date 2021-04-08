package com.shencoder.arcfacedemo

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.arcsoft.face.MaskInfo
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.shencoder.arcface.callback.OnErrorCallback
import com.shencoder.arcface.callback.OnRecognizeCallback
import com.shencoder.arcface.configuration.*
import com.shencoder.arcface.constant.FaceErrorType
import com.shencoder.arcface.face.FaceActive
import com.shencoder.arcface.face.FaceServer
import com.shencoder.arcface.face.model.RecognizeInfo
import com.shencoder.arcface.util.FeatureCovertUtil
import com.shencoder.arcface.view.FaceCameraView
import com.shencoder.arcface.view.ViewfinderView

class MainActivity : AppCompatActivity() {

    private val faceServer = FaceServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val faceCameraView: FaceCameraView = findViewById(R.id.faceCameraView)
        val btn: Button = findViewById(R.id.btn)
        btn.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_person)
            val feature1 = faceServer.extractFaceFeature(bitmap)
            if (feature1 != null) {
                val byteArrayToHexString = FeatureCovertUtil.byteArrayToHexString(feature1)
                println("提取的人脸特征码:${byteArrayToHexString.length}")
            }

            val bitmapToBase64 = BitmapUtil.bitmapToBase64(bitmap)
            val base64ToBitmap = BitmapUtil.base64ToBitmap(bitmapToBase64!!)
            val feature2 = faceServer.extractFaceFeature(base64ToBitmap)
            if (feature2 != null) {
                val byteArrayToHexString = FeatureCovertUtil.byteArrayToHexString(feature2)
                println("提取的人脸特征码:${byteArrayToHexString.length}")
            }
            val compareFaceFeature = faceServer.compareFaceFeature(feature1!!, feature2!!)
            println("相似度:" + compareFaceFeature)
        }
        val btnEnableIr: Button = findViewById(R.id.btnEnableIr)
        btnEnableIr.setOnClickListener {
            faceCameraView.enableIrDetectFaces(true)
        }
        val btnDisableIr: Button = findViewById(R.id.btnDisableIr)
        btnDisableIr.setOnClickListener {
            faceCameraView.enableIrDetectFaces(false)
        }
        faceServer.init(this, DetectFaceOrientPriority.ASF_OP_0_ONLY)
        val faceFeature =
            "0080FA4400009C42E9A65CBDB3B26CBC581286BC6C7ADDBD85F6B93DFB7E013D81F2FE3C85A3E73D5B1484BCABCA093D13EEB43D26CD603DE2F9CD3B282CAC3D375A2F3CB3DF58BCE631C23B5480D63CB2A8F23D6BBE50BDD8CB963DEB8519BCE17EEFBC7B79F53C8F4E01BDA35A953C6D158F3D59E6DFBC0AC611BD0D3CCC3DEC00ACBC1DB4A63CB67CA33BDAA6DC39AC61013C57625CBD315B7DBA5EE8AFBB870AA93C418F98BC97E4B03D493A9A3C2F746C3CBCFABABDF3B91F3DCF9D5EBC8992D33DAA7D35BDAEFC823DEB14263D59ED59BD3CEFC0BD143A0F3DEB800B3DC87CCC3CB4E3E73D4CC664BD12DA40BDEBB9B6BD58646BBDDE193A3E1A5064BD401D6ABD32FFF3BC906CD7BCA06D2EBDA68919BE94DA353D35A93139764EA9BDF1123A3D95D1B13B81D7173EA4F7263DF54536BD8E7E75BD8915993C4AE9A03D1483853C45E4693C360185BDFC14F1BDFDA9103BE71DCDBD42FF96BDF1F34D3CCF3E1F3C51FF52BC6ADB66BC98D35CBD620494BD0A99813CE79407BC55152FBCFC0F953C3799DE3B18BD3ABD2DD3563DC39ACDBDD0AB053D719882BB345FA33C8F3343BDB90C903C4C6D2A3D640C4B3D66099BBDE9EC09BE3D54873D3EBCE43C840FEB3D24C71DBDC75C87BD7D74343D1C5F92BDEDF7C33C13A0693C0B117D3D100D8A3D497D00BDB37D9E3C0DD312BD48DD533D995C833C97241C3A23CFA43D7346F53C7E1B773C7A31C2BD759793BCE3F6803BBCA3CCBDB43287BDE48D0F3DAE36BB3D099E7ABD30D3FEBC09F48CBDFA85273EA7BDC2BDF8BC563DD65C4E3D2D1407BD09908A3DDAE5DE3C86031F3D03D2B33DEF52213DEC29EFBA631CB43C6C2A00BE52A1B43DCD7BABBC334DC73DED32463B4A4742BD98D9E83C6D8A0A3E3FFA6B3DEF321F3CAC82493D5A4691BD82C9863DD8F704BD9202963CCAA67EBC83F0403DC2DEEC3B97402F3DD1BF733D4EF5123BC9CE143CE9BC06BDB059AFBD526B16BDB7DDBC3CA72C27BDEFB9843D950784BD974909BD303AB53D9BAA13BDCAFE91B94EA942BD571A09BEC054AEBDBC1C143E4FAF78BD1411973DB5D718BB707CB6BC8FA730BC2766F33C2CE4193D09925CBC2F067CBC945977BC63FE17BC6321AE3C26BF163E81BD83BD2BED283D28D10D3C29A51BBCCF4DC93C5C20EF3D4DC5A43CA5A29FBD39B30DBD8475F73AB3B5673DF3E6153D71A805BBE26199BDC9E8663DF067943C965D223DEA562E3C8061B73C5C1C383C8AC1DC3DABC605BE0E41783CC4FC30BCE8E51BBE454293BD6115FF3C52D515BEE47AD53DCCFC21BDB59B96BDB2CBE13D69BD0FBEDD2984BD1A6E2C3EAAB9393DD5C7233DC0C2D23CFA63343DDF40C9BDFFD0843B47A302BD62376D3C1A942A3D4C2D7EBD0EC60BBC22A4963C036A3E3C3B5DA4BB75E5F93DA9DFACBD69F15DBD7ABD3A3CB180A8BD3CF6533D369DB23A01212DBCF015A0BD23BBF73C62DBD63C530351BD7B97323D96968ABD9C992ABCDA5E143D2DFCD3BD309EB2BDCFA0B93D5B4A203DF548933C86C2E93CF902D13C2D00A63DC3C9863D966A80BC6477BE3DBE82A8BD5D48953D88CC163C703AB93B6286F63D149F9ABD297680BC4764B53DA428013B90F6BDBDA8D6A13C7F179B3D1488B43D96F5C93D43729C3C09F7AA3C54B9C43DFEB682BDCC77CCBD7F2DEE3C69CA6B3C0D888B3D8595903DD3FBB6BC3108613DF25ABEBD8E03F33CBDB5E5BDFF1E2B3D54FBD73D8464523D710A1F3D234B6A3C3B45393D51B3A33CE281813CBB6EC2BDF0F8843D5CB111BD900B7BBDBD2217BCFC334EBDCCD3653CD9AE033D22613E3C98F4953DB4C5A0BC1CC41BBE8FF6CCBDF76F30BC8CE06EBDFB7F5EBBB44272BCBA881EBC3D85AEBDDB1514BDF3150ABD32C791BDF7CAC73D8935A0BA55A1123CA7890DBC903AA8BC3E3605BD754CE2BC0DB1C63CB10420BED00BE13DE6E6213DAAD01D3D232695BD14EEB23DA0E00DBD4697F73C6FAB8EBDCD0B963D5E6370BC6A7E42BD3ED69DBDF204183DCF90EFBC7FA3743DE0D6AF3D0C4D633D04E0F4BDAAF782BC7FF5013D2588FBBC52F4B43DE6ACBABD753E07BEAF0D10BD1FA702BD38CE053DEDBD8BBCBD18763D5D723B3DE1F696BD22CE6F3D6F0C233DF580023E7AEECE3CB7A35EBDEA8FFBBCBB82113D0AAE4D3D97C9BE3D8F8F98BCD659933D32BE57BD4D31913CB069843D72CCBD3D977EC2BDBE871D3DC1CC05BD3DE2A43B931AEB3C5CD34CBD259F77BCB238BB3C439FE23C99A44A3D713EA63C7A9ED33D7E1F873CEA0395BD07C6213D4CAAA53D5BD7B33D44D2793CDFCEACBD514606BD78E3A53C8DB210BDCD75993D93F0D4BDF10048BD38DE8B3D106BE93D2B8F073D8C53B7BC342603BDE8AEC8BDA21EDDBD8431CFBC2C6D153E5780833D50ABBDBDB1C23E3D1A81FA3C853B46BD1328123DD7A1E3BCAB408DBCF742523CCD318BBDEDF61A3DC76112BD99C6C33C2AC90ABD092E92BD03FF87BC3AF3A6BD769B303D3D4A7BBD7611BB3C61AE803DE3E64F3D967E6139C9ABA6BCD529BCBCCFC94ABD68E7793D2B435FBC4B7B50BE1FD6073CAC70A8BC9158853C0A676F3D0352D0BCFB1AE13DF64FACBB771D4D3C9F52113D660677BC29674ABD76CE2FBC4902E7BC5079853D2EE292BD43A950BC798B9EBDF325083E7E4B883D2C3AA1BD7B38B4BDBCBF8A3B8E4F063CFA0417BC33F126BC820FC53D6B86E2BA304CA9BC1100A8BD634BD83A99E1973D8B1906BDF02A1D3DB9DE0CBD8DF1A4BD31CA443DD59B8ABCDC40A03DCC54373E98CC403D3ACFAB3D90CBEE3C8E5C98BD10FCBA3CC8269ABD4E19C23BC16386BCAB059DBD9D288ABD5C2F28BDA5F09CBCBEC8443DDF3A73BD112952BD77DAC038E1FF0B3C9E5523BDD44490BDA7B12D3E4235EEBC"

        val config = FaceConfiguration.builder(this, object : OnRecognizeCallback {
            override fun nobody() {
                println("无人")
            }

            override fun someone() {
                println("有人")
            }

            override fun onGetFaceFeature(
                faceId: Int,
                feature: ByteArray,
                recognizeInfo: RecognizeInfo,
                nv21: ByteArray
            ) {
                println("人脸特征码大小:" + feature.size)
                val extractFaceFeature = faceServer.extractFaceFeature(nv21, 1280, 720)
                val compareFaceFeature =
                    faceServer.compareFaceFeature(feature, extractFaceFeature!!)
                println("相似度:" + compareFaceFeature)
            }

            override fun onRecognized(
                bean: FaceFeatureDataBean,
                similar: Float,
                recognizeInfo: RecognizeInfo
            ): String? {
                println("人员识别成功:$recognizeInfo")
                return "识别成功,${similar}"
            }

            override fun faceFeatureList(): List<FaceFeatureDataBean> {
                return listOf(
                    FaceFeatureDataBean(
                        Any(),
                        FeatureCovertUtil.hexStringToByteArray(faceFeature)
                    )
                )
            }
        })
            .setDetectFaceOrient(DetectFaceOrientPriority.ASF_OP_0_ONLY)
            .enableRecognize(true)
            .setLivenessType(LivenessType.RGB)
            .setRgbLivenessThreshold(0.6f)
            .setIrLivenessThreshold(0.7f)
            .enableImageQuality(true)
            .setDetectFaceMaxNum(2)
            .recognizeKeepMaxFace(false)
            .enableRecognizeAreaLimited(false)
            .setRecognizeAreaLimitedRatio(0.625f)
            .setDetectInfo(DetectInfo(age = true, gender = true, angle = true))
            .setRgbCameraFcing(CameraFacing.BACK)
            .setIrCameraFcing(CameraFacing.FRONT)
            .setPreviewSize(PreviewSize(1280, 720))
            .setDrawFaceRect(DrawFaceRect())
            .isRgbMirror(true)
            .isIrMirror(true)
            .setExtractFeatureErrorRetryCount(3)
            .setRecognizeFailedRetryInterval(1000)
            .setLivenessErrorRetryCount(3)
            .setLivenessFailedRetryInterval(1000)
            .enableCompareFace(true)
            .setViewfinderText("请进行人脸识别")
            .setViewfinderGravity(ViewfinderView.TextLocation.BOTTOM)
            .enableMask(false)
            .setFaceSizeLimit(160)
            .setImageQualityNoMaskRecognizeThreshold(0.49f)
            .setImageQualityMaskRecognizeThreshold(0.29f)
            .setOnErrorCallback(object : OnErrorCallback {
                override fun onError(type: FaceErrorType, errorCode: Int, errorMessage: String) {

                }
            })
            .build()
        faceCameraView.setConfiguration(config, FaceActive.isActivated(this), true)
        faceCameraView.setLifecycleOwner(this)

//        Handler(Looper.getMainLooper()).postDelayed({
//            val activated = FaceActive.isActivated(this)
//            if (activated.not()) {
//                FaceActive.activeOffline(
//                    this,
//                    Environment.getExternalStorageDirectory().absolutePath + File.separator + "active_result.dat",
//                    object : OnActiveCallback {
//                        override fun activeCallback(isSuccess: Boolean, code: Int) {
//                            if (isSuccess) {
//                                faceCameraView.enableFace(true)
//                            }
//                            println("人脸激活是否成功:" + isSuccess + ",code:" + code + ",threadName:" + Thread.currentThread().name)
//                        }
//                    })
//            } else {
//                faceCameraView.enableFace(true)
//            }
//        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        faceServer.destroy()
    }
}