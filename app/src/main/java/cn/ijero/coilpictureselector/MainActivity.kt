package cn.ijero.coilpictureselector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.ijero.coilpictureselector.databinding.ActivityMainBinding
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectButton.setOnClickListener {
            XXPermissions.with(this).permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            chooseImage(this@MainActivity, arrayListOf(), 6)
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        }

                    })

        }

    }

    //方法 单独抽出来的
    fun chooseImage(context: Activity, pathList: List<LocalMedia>, selectNum: Int) {
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频
                .theme(R.style.picture_default_style) //主题样式(不设置为默认样式)
                .imageEngine(CoilEngine.instance)
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true) // 是否显示拍照按钮 true or false
                .isZoomAnim(true) // 图片列表点击 缩放效果 默认true
                .maxSelectNum(selectNum) // 最大图片选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionData(pathList)// 是否传入已选图片 List list
                .isPreviewImage(true) //是否可预览图片 true or false
                .isPreviewEggs(true)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true
                .isEnableCrop(false)//是否开启裁剪
                .isCompress(true) //是否压缩
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compressQuality(80)// 压缩质量 默认90 int
                .isReturnEmpty(false)//未选择数据时按确定是否可以退出
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            binding.outputTextView.text = selectList.toString()
        }
    }
}