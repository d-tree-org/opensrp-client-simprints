package org.smartregister.simprint.sample.application;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.simprint.SimPrintsLibrary;
import org.smartregister.simprint.sample.BuildConfig;
import org.smartregister.simprint.sample.MainActivity;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class SampleApplication extends DrishtiApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context, null);
        SimPrintsLibrary.init(mInstance.getApplicationContext(), BuildConfig.SIMPRINTS_PROJECT_ID,"global_module");
        ConfigurableViewsLibrary.init(context, getRepository());

        getRepository().getReadableDatabase();
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
    }


    @Override
    public void logoutCurrentUser() {

    }

    @Override
    public String getPassword() {
        return "sample-password";
    }

}