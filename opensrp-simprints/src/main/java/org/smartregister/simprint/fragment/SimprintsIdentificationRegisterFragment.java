package org.smartregister.simprint.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import com.simprints.libsimprints.SimHelper;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.simprint.R;
import org.smartregister.simprint.SimPrintsHelper;
import org.smartregister.simprint.SimPrintsLibrary;
import org.smartregister.simprint.activity.SimprintsIdentificationRegisterActivity;
import org.smartregister.simprint.contract.SimprintsIdentificationRegisterFragmentContract;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterFragmentModel;
import org.smartregister.simprint.presenter.SimprintIdentificationRegisterFragmentPresenter;
import org.smartregister.simprint.provider.SimprintIdentificationRegisterProvider;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.smartregister.simprint.activity.SimprintsIdentificationRegisterActivity.REQUEST_CODE;
import static org.smartregister.simprint.util.Utils.convertDpToPixel;

public class SimprintsIdentificationRegisterFragment extends
        BaseRegisterFragment implements SimprintsIdentificationRegisterFragmentContract.View {

    private static String sessionID = "";
    private static ArrayList<Pair<String, String>> simprintsIdBaseEntityIdPair = new ArrayList<>();
    private CommonPersonObject patient;

    private static ArrayList<String> clientIds = new ArrayList<>();

    public SimprintsIdentificationRegisterFragment(ArrayList<Pair<String, String>> results, String sessionId){
        super();
        simprintsIdBaseEntityIdPair = results;
        for (Pair<String, String> pair : results){
            clientIds.add(pair.second);
        }
        sessionID = sessionId;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        rootView = view;

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setVisibility(android.view.View.VISIBLE);

        // NavigationMenu.getInstance(this.getActivity(), null, toolbar);

        android.view.View navBarContainer = view.findViewById(R.id.register_nav_bar_container);
        navBarContainer.setFocusable(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        android.view.View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.primary_color);
        searchBarLayout.setPadding(
                searchBarLayout.getPaddingLeft(),
                searchBarLayout.getPaddingTop(),
                searchBarLayout.getPaddingRight(),
                (int) convertDpToPixel(10.0F, this.getActivity())
        );

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(R.string.fingerprint_identification_title);
            titleView.setPadding(0, titleView.getTop(), 0, titleView.getPaddingBottom());
        }

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_simprints_identification;
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

        SimprintIdentificationRegisterProvider provider = new SimprintIdentificationRegisterProvider(
                getActivity(),commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }


    @Override
    public SimprintsIdentificationRegisterFragmentContract.Presenter presenter() {
        return (SimprintsIdentificationRegisterFragmentContract.Presenter) presenter;
    }

    @Override
    protected void initializePresenter() {

        if (getActivity() == null)
            return;
        presenter = new SimprintIdentificationRegisterFragmentPresenter(this, new SimprintsIdentificationRegisterFragmentModel(), null, clientIds);
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return this.presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

        if (view.getId() == R.id.patient_column){
            if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_view_normal") {
                confirmSelectedClient(view);
            }
        }

        if (view.getId() == R.id.next_arrow){
            if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_next_arrow") {
                confirmSelectedClient(view);
            }
        }

        if (view.getId() == R.id.textview_none_of_above){
            if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_none_of_above") {
                handleNoneSelected(view);
            }
        }
    }
    private void handleNoneSelected(android.view.View view) {
        String sessionid = sessionID;
        // A call back to SimPrint to notify that none of the item on the list was selected
        SimprintsIdentificationRegisterActivity activity = (SimprintsIdentificationRegisterActivity) this.getActivity();
        activity.startSimPrintsConfirmation(sessionid, "none_selected", null);
    }


    private void confirmSelectedClient(android.view.View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();

            //SimPrint Confirmation of the selected client
            String baseEntityId = pc.entityId();
            String simPrintsGuid = getSimPrintGuid(baseEntityId);
            String sessionid = sessionID;
            SimprintsIdentificationRegisterActivity activity = (SimprintsIdentificationRegisterActivity) this.getActivity();
            String relational_id = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "relationalid", false);
            patient = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyRegister.tableName)
                    .findByCaseID(relational_id);
            activity.startSimPrintsConfirmation(sessionid, simPrintsGuid, patient);

        }
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    private String getSimPrintGuid(String baseEntityId) {
        String simprintsGuid = "";
        for (Pair<String, String> pair : simprintsIdBaseEntityIdPair){
            if (pair.second.equals(baseEntityId)){
                simprintsGuid = pair.first;
            }
        }
        return simprintsGuid;

    }

}
