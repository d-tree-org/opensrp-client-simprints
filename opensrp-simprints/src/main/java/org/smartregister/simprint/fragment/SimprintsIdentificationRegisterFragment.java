package org.smartregister.simprint.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.simprint.R;
import org.smartregister.simprint.contract.SimprintsIdentificationRegisterFragmentContract;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterFragmentModel;
import org.smartregister.simprint.presenter.SimprintIdentificationRegisterFragmentPresenter;
import org.smartregister.simprint.provider.SimprintIdentificationRegisterProvider;
import org.smartregister.simprint.util.JsonFormUtil;
import org.smartregister.simprint.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.smartregister.simprint.util.Utils.convertDpToPixel;

public class SimprintsIdentificationRegisterFragment extends
        BaseRegisterFragment implements SimprintsIdentificationRegisterFragmentContract.View {

    public static final String SESSION_ID_EXTRA = "session_id";
    public static final String RESULTS_GUID_EXTRA = "result_guids";

    private static String sessionID = "";
    private static ArrayList<String> resultsGuid = new ArrayList<>();

    android.content.Context activity;

    private static SimprintsIdentificationRegisterFragment instance;

    public SimprintsIdentificationRegisterFragment newInstance(Context context){
        this.activity = context;
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;
        instance = new SimprintsIdentificationRegisterFragment();
        if (baseRegisterActivity != null){
            if (baseRegisterActivity.getIntent().getExtras() != null){
                sessionID = baseRegisterActivity.getIntent().getExtras().getString(SESSION_ID_EXTRA);
                resultsGuid = baseRegisterActivity.getIntent().getExtras().getStringArrayList(RESULTS_GUID_EXTRA);
            }
        }
        return instance;
    }

    public SimprintsIdentificationRegisterFragment(){
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

        ArrayList<String> baseEntityIds = new ArrayList<>();
        for(String id : resultsGuid){
            baseEntityIds.add(JsonFormUtil.lookForClientBaseEntityIds(id));
        }

        presenter = new SimprintIdentificationRegisterFragmentPresenter(this, new SimprintsIdentificationRegisterFragmentModel(), null, baseEntityIds);
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

    }

    @Override
    public void showNotFoundPopup(String s) {

    }
}
