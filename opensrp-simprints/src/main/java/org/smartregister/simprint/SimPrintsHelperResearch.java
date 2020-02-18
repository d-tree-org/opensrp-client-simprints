package org.smartregister.simprint;

import android.content.Intent;

import com.simprints.libsimprints.Constants;

public class SimPrintsHelperResearch {

    final private String projectId;
    final private String userId;
    private String age;

    public SimPrintsHelperResearch(String projectId, String userId, String age) {
        this.projectId = projectId;
        this.userId = userId;
        this.age = age;
    }

    public Intent register(String moduleId) {
        Intent intent = new Intent("com.simprints.afyatek.REGISTER");
        intent.putExtra(Constants.SIMPRINTS_PROJECT_ID, projectId);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        intent.putExtra("userId", userId);
        intent.putExtra("age", age);
        return intent;
    }

    public Intent identify(String moduleId) {
        Intent intent = new Intent("com.simprints.afyatek.IDENTIFY");
        intent.putExtra(Constants.SIMPRINTS_PROJECT_ID, projectId);
        intent.putExtra(Constants.SIMPRINTS_USER_ID, userId);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        intent.putExtra("age", age);
        return intent;
    }
}
