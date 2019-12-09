package org.smartregister.simprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.Identification;
import com.simprints.libsimprints.Tier;

import java.util.ArrayList;

import static com.simprints.libsimprints.Constants.SIMPRINTS_PACKAGE_NAME;

/**
 * Author : Isaya Mollel on 2019-10-30.
 */
public class SimPrintsIdentifyActivity extends AppCompatActivity {

    public static final String PUT_EXTRA_REQUEST_CODE = "result_code";

    private int REQUEST_CODE;
    private String moduleId;

    public static void StartSimprintsIdentifyActivity(Activity context, String moduleId, int requestCode){
        Intent intent = new Intent(context, SimPrintsIdentifyActivity.class);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID, moduleId);
        intent.putExtra(PUT_EXTRA_REQUEST_CODE, requestCode);
        context.startActivityForResult(intent, requestCode);
    }

    public static void ConfirmIdentification(Activity context, String sessionid, String selectedGuid) {
        SimPrintsHelper simPrintsHelper = new SimPrintsHelper(SimPrintsLibrary.getInstance().getProjectId(),
                SimPrintsLibrary.getInstance().getUserId());
        simPrintsHelper.confirmIdentity(context, sessionid, selectedGuid);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SimPrintsUtils.isPackageInstalled(SIMPRINTS_PACKAGE_NAME,getPackageManager())){
            SimPrintsUtils.downloadSimprintIdApk(this);
            return;
        }
        moduleId = getIntent().getStringExtra(Constants.SIMPRINTS_MODULE_ID);
        REQUEST_CODE = getIntent().getIntExtra(PUT_EXTRA_REQUEST_CODE, 111);

        startIdentification();

    }

    private void startIdentification(){
        try{
            SimPrintsHelper simPrintsHelper = new SimPrintsHelper(SimPrintsLibrary.getInstance().getProjectId(),
                    SimPrintsLibrary.getInstance().getUserId());
            Intent intent = simPrintsHelper.identify(moduleId);
            startActivityForResult(intent, REQUEST_CODE);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK && requestCode == REQUEST_CODE){

            Boolean check = data.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK, false);
            ArrayList<Identification> identifications = data
                    .getParcelableArrayListExtra(Constants.SIMPRINTS_IDENTIFICATIONS);

            if (check && identifications != null && identifications.size() > 0){

                ArrayList<Identification> bestMatchedIdentifications = getBestMatchIdentification(identifications);
                ArrayList<SimPrintsIdentification> simPrintsIdentifications;

                if ( bestMatchedIdentifications.size() == 0 ){
                    simPrintsIdentifications = new ArrayList<>();
                }else {
                    simPrintsIdentifications = new ArrayList<>();
                    for (Identification identification : bestMatchedIdentifications){
                        SimPrintsIdentification simPrintsIdentification = new SimPrintsIdentification(identification.getGuid());
                        simPrintsIdentifications.add(simPrintsIdentification);
                    }
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra(SimPrintsConstantHelper.INTENT_DATA, simPrintsIdentifications);
                returnIntent.putExtra(Constants.SIMPRINTS_SESSION_ID, data.getStringExtra(Constants.SIMPRINTS_SESSION_ID));
                setResult(RESULT_OK,returnIntent);
                finish();

            }else {
                showFingerPrintFail(this, new OnDialogButtonClick() {
                    @Override
                    public void onOkButtonClick() {
                        startIdentification();
                    }

                    @Override
                    public void onCancelButtonClick() {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED,returnIntent);
                        finish();
                    }
                });
            }
        }
    }

    private ArrayList<Identification> getBestMatchIdentification(ArrayList<Identification> identifications){
        ArrayList<Identification> ids = new ArrayList<>();

        for (Identification identification : identifications){
            if (identification.getTier() == Tier.TIER_1 ||
                identification.getTier() == Tier.TIER_2){
                ids.add(identification);
            }
        }

        return ids;
    }

    private void showFingerPrintFail(Context context, final OnDialogButtonClick onDialogButtonClick){
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
