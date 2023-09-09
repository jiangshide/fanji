package com.fanji.android.resource.base;

import androidx.annotation.Keep;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fanji.android.net.Net;
import com.fanji.android.net.vm.LiveResult;
import com.fanji.android.ui.FJTipsView;
import com.fanji.android.util.EncryptUtil;

/**
 * created by jiangshide on 2019-08-14.
 * email:18311271399@163.com
 */
@Keep
public class BaseVM extends ViewModel {
    protected Net net;
    private FJTipsView mFjTipsView;

    public BaseVM() {
        if (null == net) net = Net.INSTANCE;
    }

    public void loading(FJTipsView fjTipsView) {
        this.mFjTipsView = fjTipsView;
        mFjTipsView.setStatus(true, true, false);
    }

    public void finishLoading() {
        if (mFjTipsView != null) {
            mFjTipsView.setStatus(false, false, false);
        }
    }

    public static <T extends ViewModel> T of(FragmentActivity activity, Class<T> modelClass) {
//        return ViewModelProvider.of(activity).get(modelClass);
        return ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication()).create(modelClass);
    }

    public String md5(String psw) {
        return EncryptUtil.encryptMd5(psw);
    }

    public interface VMListener<T> {
        void onRes(LiveResult<T> res);
    }
}
