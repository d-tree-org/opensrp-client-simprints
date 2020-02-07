package org.smartregister.simprint.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.simprint.R;
import org.smartregister.simprint.contract.SimprintsIdentificationRegisterContract;
import org.smartregister.simprint.fragment.EmptyResultFragment;
import org.smartregister.simprint.fragment.SimprintsIdentificationRegisterFragment;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterModel;
import org.smartregister.simprint.presenter.SimprintsIdentificationRegisterPresenter;
import org.smartregister.simprint.util.JsonFormUtil;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class SimprintsIdentificationRegisterActivity extends BaseRegisterActivity implements SimprintsIdentificationRegisterContract.View {

    public static final String RESULTS_LIST_EXTRA = "results_list";
    public static final String CURRENT_SESSION_EXTRA = "current_session";

    public ArrayList<String> identifiedClients = new ArrayList<>();
    public String sessionId = "";

    private ArrayList<String> resultsList = new ArrayList<>();

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

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            resultsList = bundle.getStringArrayList(RESULTS_LIST_EXTRA);
            sessionId = bundle.getString(CURRENT_SESSION_EXTRA);

        }

        ArrayList<String> clientIds = new ArrayList<>();
        for (String clientSimprintsId : resultsList){
            String baseEntityId = JsonFormUtil.lookForClientBaseEntityIds(clientSimprintsId);
            if (baseEntityId != null && !StringUtils.isEmpty(baseEntityId)){
                clientIds.add(baseEntityId);
            }
        }

        if (clientIds.size() > 0){
            identifiedClients = clientIds;
            return new SimprintsIdentificationRegisterFragment(clientIds, sessionId);
        }else {
            return new EmptyResultFragment();
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
