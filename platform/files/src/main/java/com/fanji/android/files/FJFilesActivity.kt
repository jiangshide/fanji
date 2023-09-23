package com.fanji.android.files

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fanji.android.files.databinding.ActivityFileBinding
import com.fanji.android.files.utils.FilePickerConst.PDF
import com.fanji.android.files.utils.FilePickerConst.PPT
import com.fanji.android.files.utils.FilePickerConst.DOC
import com.fanji.android.files.utils.FilePickerConst.TXT
import com.fanji.android.files.utils.FilePickerConst.ZIP
import com.fanji.android.files.utils.FilePickerConst.XLS

import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

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
        binding.fileViewPager.adapter = binding.fileViewPager.create(supportFragmentManager)
            .setTitles(
                arrayListOf(PDF, DOC, PPT, TXT, XLS, ZIP)
            )
            .setFragment(
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(this, binding.fileTab, binding.fileViewPager)

//        binding.commonHead.topTitle.text = resources.getStringArray(R.array.files)[mType]
    }
}