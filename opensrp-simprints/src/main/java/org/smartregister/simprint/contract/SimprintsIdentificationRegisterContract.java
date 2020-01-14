package org.smartregister.simprint.contract;

import org.smartregister.view.contract.BaseRegisterContract;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public interface SimprintsIdentificationRegisterContract {

    interface View extends BaseRegisterContract.View {
        SimprintsIdentificationRegisterContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

    }

    interface Model {

    }

}
