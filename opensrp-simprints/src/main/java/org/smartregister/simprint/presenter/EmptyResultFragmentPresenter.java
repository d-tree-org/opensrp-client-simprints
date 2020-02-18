package org.smartregister.simprint.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.simprint.contract.EmptyResultFragmentContract;;
import org.smartregister.simprint.contract.SimprintsIdentificationRegisterFragmentContract;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.simprint.model.SimprintsIdentificationRegisterFragmentModel;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.TreeSet;

public class EmptyResultFragmentPresenter implements EmptyResultFragmentContract.Presenter {

    protected WeakReference<EmptyResultFragmentContract.View> viewReference;

    protected SimprintsIdentificationRegisterFragmentModel model;

    protected RegisterConfiguration config;


    protected Set<View> visibleColumns = new TreeSet<>();

    private String viewConfigurationIdentifier;

    public EmptyResultFragmentPresenter(EmptyResultFragmentContract.View view, SimprintsIdentificationRegisterFragmentModel model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();

    }

    @Override
    public String getMainCondition() {
        return null;
    }

    @Override
    public String getDefaultSortQuery() {
        return null;
    }

    @Override
    public void setFamilyHead(String familyHead) {

    }

    @Override
    public void setPrimaryCaregiver(String primaryCaregiver) {

    }

    @Override
    public void processViewConfigurations() {


        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }

    }

    private void setVisibleColumns(Set<View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected EmptyResultFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void initializeQueries(String mainCondition) {

    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String uniqueId) {

    }
}
