package org.smartregister.simprint;

import android.content.Intent;
import android.provider.SyncStateContract;

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
        intent.putExtra("projectId", projectId);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        intent.putExtra("userId", userId);
        intent.putExtra("age", age);
        return intent;
    }
}
