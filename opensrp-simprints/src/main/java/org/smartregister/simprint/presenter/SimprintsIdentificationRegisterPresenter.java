package org.smartregister.simprint.presenter;

import org.smartregister.simprint.contract.SimprintsIdentificationRegisterContract;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class SimprintsIdentificationRegisterPresenter implements SimprintsIdentificationRegisterContract.Presenter {

    protected WeakReference<SimprintsIdentificationRegisterContract.View> viewReference;
    protected SimprintsIdentificationRegisterContract.Model model;

    public SimprintsIdentificationRegisterPresenter(SimprintsIdentificationRegisterContract.View view, SimprintsIdentificationRegisterContract.Model model){
        viewReference = new WeakReference<>(view);
        this.model = model;
    }

    private SimprintsIdentificationRegisterContract.View getView(){
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void registerViewConfigurations(List<String> list) {

    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {

    }

    @Override
    public void onDestroy(boolean b) {

    }

    @Override
    public void updateInitials() {

    }
}
