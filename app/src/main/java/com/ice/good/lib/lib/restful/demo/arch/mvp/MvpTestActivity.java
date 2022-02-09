package com.ice.good.lib.lib.restful.demo.arch.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ice.good.lib.lib.restful.demo.arch.mvp.base.BaseActivity;
import com.ice.good.lib.lib.restful.demo.http.LoginModel;
import com.ice.good.lib.ui.amount.AmountView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MvpTestActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.login();

    }

    @Override
    public void onLogin(LoginModel loginModel) {

    }

}
