package org.smartregister.simprint;

import android.content.Intent;

import com.simprints.libsimprints.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimPrintsHelperResearch {

    final private String projectId;
    final private String userId;
    private String age;

    public SimPrintsHelperResearch(String projectId, String userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

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
        // The age is date of birth
        intent.putExtra("age", formatDateForRiddler(age));
        return intent;
    }

    public Intent identify(String moduleId) {
        Intent intent = new Intent("com.simprints.afyatek.IDENTIFY");
        intent.putExtra(Constants.SIMPRINTS_PROJECT_ID, projectId);
        intent.putExtra(Constants.SIMPRINTS_USER_ID, userId);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        return intent;
    }

    public Intent confirmIdentity(String selectedGuid, String sessionId) {
        Intent intent = new Intent("com.simprints.afyatek.CONFIRM_IDENTITY");
        intent.putExtra(Constants.SIMPRINTS_PROJECT_ID, projectId);
        intent.putExtra(Constants.SIMPRINTS_SESSION_ID, sessionId);
        intent.putExtra(Constants.SIMPRINTS_SELECTED_GUID, selectedGuid);

        // The age is date of birth
        String formattedAge = "";
        try {
            Date ageDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.age);
            formattedAge = new SimpleDateFormat("yyyy-MM-dd").format(ageDate);
        }catch (Exception e){
            e.printStackTrace();
        }

        intent.putExtra("age", formattedAge);
        return intent;
    }

    private String formatDateForRiddler(String dob) {
        SimpleDateFormat dateFormatForRiddler = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatFromNativeForms = new SimpleDateFormat("dd-MM-yyyy");
        String formatedDate = null;
        try {
            // Get the date from the date picker in Date format for changing it
            Date dobIndate = dateFormatFromNativeForms.parse(dob);
            formatedDate = dateFormatForRiddler.format(dobIndate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatedDate;
    }
}
