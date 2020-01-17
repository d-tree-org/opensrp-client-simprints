package org.smartregister.simprint.activity;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.simprint.R;
import org.smartregister.simprint.contract.SimprintsIdentificationRegisterContract;
import org.smartregister.simprint.fragment.EmptyResultFragment;
import org.smartregister.simprint.fragment.SimprintsIdentificationRegisterFragment;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterModel;
import org.smartregister.simprint.presenter.SimprintsIdentificationRegisterPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class SimprintsIdentificationRegisterActivity extends BaseRegisterActivity implements SimprintsIdentificationRegisterContract.View {

    public SimprintsIdentificationRegisterActivity(){

    }

    @Override
    protected void registerBottomNavigation() {
        this.bottomNavigationView = (BottomNavigationView) this.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public SimprintsIdentificationRegisterContract.Presenter presenter() {
        return (SimprintsIdentificationRegisterContract.Presenter) presenter;
    }

    @Override
    protected void initializePresenter() {
        presenter = new SimprintsIdentificationRegisterPresenter(this, new SimprintsIdentificationRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        ArrayList<String> results = (ArrayList<String>)this.getIntent().getSerializableExtra("result_guids");
        if (results.size() > 0){
            return new EmptyResultFragment();
        }else {
            return new SimprintsIdentificationRegisterFragment().newInstance(this);
        }
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {

    }
}
