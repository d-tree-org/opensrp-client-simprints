package org.smartregister.simprint.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.SimHelper;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.Utils;
import org.smartregister.simprint.R;
import org.smartregister.simprint.SimPrintsLibrary;
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
import java.util.Map;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class SimprintsIdentificationRegisterActivity extends BaseRegisterActivity implements SimprintsIdentificationRegisterContract.View {

    public static final String RESULTS_LIST_EXTRA = "results_list";
    public static final String CURRENT_SESSION_EXTRA = "current_session";

    public String sessionId = "";

    private ArrayList<String> resultsList = new ArrayList<>();
    public static final int REQUEST_CODE = 3322;
    private CommonPersonObject selectedClient;

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

        ArrayList<Pair<String, String>> simprintsBaseEntityPairs = new ArrayList<>();

        for (String clientSimprintsId : resultsList){
            String baseEntityId = JsonFormUtil.lookForClientBaseEntityIds(clientSimprintsId);
            if (baseEntityId != null && !StringUtils.isEmpty(baseEntityId)){
                Pair<String, String> pair = new Pair<>(clientSimprintsId, baseEntityId);
                simprintsBaseEntityPairs.add(pair);
            }
        }

        if (simprintsBaseEntityPairs.size() > 0){
            return new SimprintsIdentificationRegisterFragment(simprintsBaseEntityPairs, sessionId);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startSimPrintsConfirmation(String sessionId, String selectedGuid, CommonPersonObject selectedClient) {
        this.selectedClient = selectedClient;
        Utils.startAsyncTask(new ConfirmIdentificationTask(this, sessionId, selectedGuid), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == REQUEST_CODE) {
            Boolean check = data.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK, false);
            if (check) {
                goTotheFamilyProfileOfSelected();
            }
        }
    }

    private void goTotheFamilyProfileOfSelected() {

        if (selectedClient != null) {
            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().profileActivity);
            intent.putExtra("family_base_entity_id", selectedClient.getCaseId());
            intent.putExtra("family_head",
                    org.smartregister.family.util.Utils.getValue(selectedClient.getColumnmaps(), "family_head", false));
            intent.putExtra("primary_caregiver",
                    org.smartregister.family.util.Utils.getValue(selectedClient.getColumnmaps(), "primary_caregiver", false));
            intent.putExtra("village_town",
                    org.smartregister.family.util.Utils.getValue(selectedClient.getColumnmaps(), "village_town", false));
            intent.putExtra("family_name",
                    org.smartregister.family.util.Utils.getValue(selectedClient.getColumnmaps(), "first_name", false));
            intent.putExtra("go_to_due_page", false);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

    }



    ////////////////////////////////////////////////////////////////////
    //      Inner Class | SimPrints Identification Confirmation
    ///////////////////////////////////////////////////////////////////

    private class ConfirmIdentificationTask extends AsyncTask<Void, Void, Void> {

        private String sessiodId;
        private String selectedGuid;
        private Context context;

        private ConfirmIdentificationTask(Context context, String sessiodId, String selectedGuid) {
            this.sessiodId = sessiodId;
            this.selectedGuid = selectedGuid;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SimHelper simPrintsHelper = new SimHelper(SimPrintsLibrary.getInstance().getProjectId(),
                    SimPrintsLibrary.getInstance().getUserId());
            Intent intent = simPrintsHelper.confirmIdentity(context, sessiodId, selectedGuid);
            startActivityForResult(intent, REQUEST_CODE);
            return null;
        }
    }

}
