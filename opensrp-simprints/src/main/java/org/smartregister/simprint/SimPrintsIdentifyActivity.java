package org.smartregister.simprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.Identification;

import org.smartregister.CoreLibrary;
import org.smartregister.simprint.activity.SimprintsIdentificationRegisterActivity;

import java.util.ArrayList;

import static com.simprints.libsimprints.Constants.SIMPRINTS_PACKAGE_NAME;
import static com.simprints.libsimprints.Constants.SIMPRINTS_REFUSAL_FORM;

/**
 * Author : Isaya Mollel on 2019-10-30.
 */
public class SimPrintsIdentifyActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Boolean is_reseach_enabled;
    public static final String PUT_EXTRA_REQUEST_CODE = "result_code";

    private int REQUEST_CODE;
    private String moduleId;
    private String userId;

    private boolean callRiddler = false;

    public static void startSimprintsIdentifyActivity(Activity context, String moduleId, int requestCode) {
        Intent intent = new Intent(context, SimPrintsIdentifyActivity.class);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        intent.putExtra(PUT_EXTRA_REQUEST_CODE, requestCode);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getApplication().getSharedPreferences("AllSharedPreferences", MODE_PRIVATE);
        is_reseach_enabled = preferences.getBoolean("IS_SIMPRINTS_RESEARCH_ENABLED", false);

        boolean appsPresent = checkAppsInstalled(is_reseach_enabled);
        if (!appsPresent){
            SimPrintsUtils.downloadSimprintIdApk(this);
            return;
        }

        moduleId = getIntent().getStringExtra(Constants.SIMPRINTS_MODULE_ID);
        userId = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();

        /**
         * // DIRTY
         * Enforce simprints projectId to production if the client module is mailimojatraining
         * This temporarily handles the issue of having a single build for both training and testing server
         */
        if (moduleId.equalsIgnoreCase(getResources().getString(R.string.training_module_name))){
            SimPrintsLibrary.getInstance().setProjectId(getResources().getString(R.string.simprints_production_project_id));
        }

        REQUEST_CODE = getIntent().getIntExtra(PUT_EXTRA_REQUEST_CODE, 111);

        startIdentification();

    }

    private void startIdentification() {
        if (callRiddler) {
            try {
                SimPrintsHelperResearch simPrintsHelperResearch = new SimPrintsHelperResearch(SimPrintsLibrary.getInstance().getProjectId(),
                        userId);
                Intent intent = simPrintsHelperResearch.identify(moduleId.toLowerCase());
                startActivityForResult(intent, REQUEST_CODE);
            } catch (IllegalStateException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            try {
                SimPrintsHelper simPrintsHelper = new SimPrintsHelper(SimPrintsLibrary.getInstance().getProjectId(),
                        userId);
                Intent intent = simPrintsHelper.identify(moduleId.toLowerCase());
                startActivityForResult(intent, REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    boolean checkAppsInstalled(boolean researchEnabled){

        boolean _results;

        boolean ridlerPresent = SimPrintsUtils.isPackageInstalled("com.simprints.riddler", getPackageManager());
        boolean isSimprintsIdAvailable = SimPrintsUtils.isPackageInstalled(SIMPRINTS_PACKAGE_NAME,getPackageManager());

        if (!isSimprintsIdAvailable){
            _results = false;
        }else {
            _results = true;
            if (researchEnabled && ridlerPresent){
                callRiddler = true;
            }
        }
        return _results;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            Boolean check = data.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK, false);
            if (check) {

                 if (data.getParcelableExtra(SIMPRINTS_REFUSAL_FORM) == null) {
                     ArrayList<Identification> identifications = data
                             .getParcelableArrayListExtra(Constants.SIMPRINTS_IDENTIFICATIONS);

                     ArrayList<String> resultsGuids = new ArrayList<>();
                     String sessionId = "";
                     sessionId = data.getStringExtra("sessionId");

                     if (check && identifications != null && identifications.size() > 0){
                         ArrayList<Identification> topResults = getTopResults(identifications);
                         for (Identification identification : topResults){
                             resultsGuids.add(identification.getGuid());
                         }

                     }

                     Intent intent = new Intent(this, SimprintsIdentificationRegisterActivity.class);
                     intent.putExtra(SimprintsIdentificationRegisterActivity.CURRENT_SESSION_EXTRA, sessionId);
                     intent.putExtra(SimprintsIdentificationRegisterActivity.RESULTS_LIST_EXTRA, resultsGuids);
                     startActivity(intent);
                     finish();
                 } else {
                     Toast.makeText(this, R.string.biometric_declined_message, Toast.LENGTH_SHORT).show();
                     finish();
                 }

            } else {
                finish();
            }

        } else {
            showFingerPrintFail(this, new OnDialogButtonClick() {
                @Override
                public void onOkButtonClick() {
                    startIdentification();
                }

                @Override
                public void onCancelButtonClick() {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
        }
    }

    private ArrayList<Identification> getTopResults(ArrayList<Identification> unsortedIdentifications) {
        ArrayList<Identification> identifications = unsortedIdentifications;
        ArrayList<Identification> sortedIdentifications = new ArrayList<>();
        for (int i = 0; i < identifications.size(); i++) {
            for (int j = 0; j < identifications.size() - 1 - i; j++) {
                if (identifications.get(j).getConfidence() > identifications.get(j + 1).getConfidence()) {
                    Identification tempIdentification = identifications.get(j);
                    identifications.set(j, identifications.get(j + 1));
                    identifications.set(j + 1, tempIdentification);
                }
            }
        }

        if (identifications.size() > 3) {
            for (int i = identifications.size() - 1; i >= identifications.size() - 3; i--) {
                sortedIdentifications.add(identifications.get(i));
            }
        } else {
            sortedIdentifications = identifications;
        }

        return sortedIdentifications;
    }

    private void showFingerPrintFail(Context context, final OnDialogButtonClick onDialogButtonClick) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(getString(R.string.fail_result));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.scan_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogButtonClick.onOkButtonClick();
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogButtonClick.onCancelButtonClick();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
