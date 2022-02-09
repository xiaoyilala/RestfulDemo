package com.ice.good.lib.lib.restful.demo.arch.mvp.base;

public class BasePresenter<IView extends BaseView> {

    public IView view;

    public void attach(IView view){
        this.view = view;
    }

    public void detach(){
        this.view = null;
    }
}
