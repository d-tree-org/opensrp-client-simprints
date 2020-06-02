package org.smartregister.simprint.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.simprint.R;
import org.smartregister.simprint.contract.EmptyResultFragmentContract;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterFragmentModel;
import org.smartregister.simprint.presenter.EmptyResultFragmentPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class EmptyResultFragment extends BaseRegisterFragment implements EmptyResultFragmentContract.View {


    @Override
    protected int getLayout() {
        return R.layout.empty_results;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.empty_results,container, false);
        this.rootView = view;

        this.setupViews(view);

        return view;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        clientsView = view.findViewById(R.id.recycler_view);
        clientsView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        clientsView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this.getActivity(), 1);
        clientsView.addItemDecoration(itemDecor);

        clientsView.setVisibility(View.VISIBLE);
        presenter.processViewConfigurations();
        presenter.initializeQueries(getMainCondition());

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setVisibility(android.view.View.VISIBLE);
    }


    @Override
    protected void initializePresenter() {
        this.presenter = new EmptyResultFragmentPresenter(this, new SimprintsIdentificationRegisterFragmentModel(), null);
    }


    @Override
    public void setUniqueID(String qrCode) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {

    }

    @Override
    protected String getMainCondition() {
        return null;
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
    public void showNotFoundPopup(String opensrpId) {

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

    }

    @Override
    public EmptyResultFragmentContract.Presenter presenter() {
        return (EmptyResultFragmentContract.Presenter) presenter;
    }

    @Override
    public void setFamilyHead(String familyHead) {

    }

    @Override
    public void setPrimaryCaregiver(String primaryCaregiver) {

    }



}
