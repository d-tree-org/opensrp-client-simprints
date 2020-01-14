package org.smartregister.simprint.contract;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Set;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public interface SimprintsIdentificationRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View{
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);
        SimprintsIdentificationRegisterFragmentContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter{
        String getMainCondition();

        String getDefaultSortQuery();
    }

    interface Model{

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);

    }

}
