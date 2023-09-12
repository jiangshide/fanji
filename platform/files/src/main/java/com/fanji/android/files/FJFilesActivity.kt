package com.fanji.android.files

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fanji.android.files.databinding.ActivityFileBinding
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.IMG

/**
 * @author: jiangshide
 * @date: 2023/9/9
 * @email: 18311271399@163.com
 * @description:
 */
class FJFilesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileBinding

    companion object {
        private var mType: Int = 0
        private var mFileListener: FileListener? = null
        fun openFile(context: Context, type: Int, fileListener: FileListener) {
            mType = type
            mFileListener = fileListener
            context.startActivity(Intent(context, FJFilesActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.commonHead.topTitle.text = resources.getStringArray(R.array.files)[mType]
        val list = FJFiles.fileList(IMG)
        LogUtil.e("----jsd----file--->>>", "--------list.size:", list.size, " | list:" + list)
//        binding.fileRecycleView.create()
    }
}