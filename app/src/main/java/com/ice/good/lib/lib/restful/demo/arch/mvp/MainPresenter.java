package com.ice.good.lib.lib.restful.demo.arch.mvp;

import androidx.annotation.NonNull;

import com.ice.good.lib.lib.restful.Callback;
import com.ice.good.lib.lib.restful.Response;
import com.ice.good.lib.lib.restful.demo.http.ApiFactory;
import com.ice.good.lib.lib.restful.demo.http.LoginModel;
import com.ice.good.lib.lib.restful.demo.http.TestApi;

public class MainPresenter extends MainContract.Presenter {
    @Override
    public void login() {

        ApiFactory.INSTANCE.create(TestApi.class).login("ice", "ice").enqueue(new Callback<LoginModel>(){

            @Override
            public void fail(@NonNull Throwable throwable) {

            }

            @Override
            public void success(@NonNull Response<LoginModel> loginModelResponse) {
                if(view!=null && view.isAlive()){
                    view.onLogin(loginModelResponse.getData());
                }
            }
        });
    }
}
