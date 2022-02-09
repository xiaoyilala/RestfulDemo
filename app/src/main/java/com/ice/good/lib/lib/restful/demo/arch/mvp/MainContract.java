package com.ice.good.lib.lib.restful.demo.arch.mvp;

import com.ice.good.lib.lib.restful.demo.arch.mvp.base.BasePresenter;
import com.ice.good.lib.lib.restful.demo.arch.mvp.base.BaseView;
import com.ice.good.lib.lib.restful.demo.http.LoginModel;

public interface MainContract {
    public interface View extends BaseView {
        void onLogin(LoginModel loginModel);
    }

    public abstract class Presenter extends BasePresenter<View> {
        public abstract void login();
    }
}
