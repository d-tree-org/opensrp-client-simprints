package org.smartregister.simprint.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.SimHelper;


import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;
import org.smartregister.simprint.R;
import org.smartregister.simprint.SimPrintsHelperResearch;
import org.smartregister.simprint.SimPrintsLibrary;
import org.smartregister.simprint.SimPrintsUtils;
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
    private CommonPersonObject selectedClientFamily;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startSimPrintsConfirmation(String sessionId, String selectedGuid, CommonPersonObjectClient selectedClient) {
        CommonPersonObject client;
        String dob = null;
        if (selectedClient != null) {
            String relational_id = org.smartregister.family.util.Utils.getValue(selectedClient.getColumnmaps(), "relationalid", false);
            this.selectedClientFamily = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyRegister.tableName)
                    .findByCaseID(relational_id);
            client = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(selectedClient.entityId());
            dob = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), "dob", false);
        }


        Boolean is_reseach_enabled = getApplication().getSharedPreferences("AllSharedPreferences", MODE_PRIVATE)
                .getBoolean("IS_SIMPRINTS_RESEARCH_ENABLED", false);
        if (is_reseach_enabled && SimPrintsUtils.isPackageInstalled("com.simprints.riddler", getPackageManager())) {
            if (!selectedGuid.equalsIgnoreCase("none_selected")) {
                SimPrintsHelperResearch simPrintsHelperResearch = new SimPrintsHelperResearch(SimPrintsLibrary.getInstance().getProjectId(),
                        SimPrintsLibrary.getInstance().getUserId(), dob);
                Intent intent = simPrintsHelperResearch.confirmIdentity(selectedGuid, sessionId);
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                //Call confirm identity by passing the research so that SimprintsID wont complain about age not being set
                SimHelper simPrintsHelper = new SimHelper(SimPrintsLibrary.getInstance().getProjectId(), SimPrintsLibrary.getInstance().getUserId());
                Intent intent = simPrintsHelper.confirmIdentity(this, sessionId, selectedGuid);
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            SimHelper simPrintsHelper = new SimHelper(SimPrintsLibrary.getInstance().getProjectId(), SimPrintsLibrary.getInstance().getUserId());
            Intent intent = simPrintsHelper.confirmIdentity(this, sessionId, selectedGuid);
            startActivityForResult(intent, REQUEST_CODE);
        }
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

        if (selectedClientFamily != null) {
            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().profileActivity);
            intent.putExtra("family_base_entity_id", selectedClientFamily.getCaseId());
            intent.putExtra("family_head",
                    org.smartregister.family.util.Utils.getValue(selectedClientFamily.getColumnmaps(), "family_head", false));
            intent.putExtra("primary_caregiver",
                    org.smartregister.family.util.Utils.getValue(selectedClientFamily.getColumnmaps(), "primary_caregiver", false));
            intent.putExtra("village_town",
                    org.smartregister.family.util.Utils.getValue(selectedClientFamily.getColumnmaps(), "village_town", false));
            intent.putExtra("family_name",
                    org.smartregister.family.util.Utils.getValue(selectedClientFamily.getColumnmaps(), "first_name", false));
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
