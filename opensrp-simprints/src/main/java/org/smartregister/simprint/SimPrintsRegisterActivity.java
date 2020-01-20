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
import com.simprints.libsimprints.Registration;

import static com.simprints.libsimprints.Constants.SIMPRINTS_PACKAGE_NAME;

public class SimPrintsRegisterActivity extends AppCompatActivity {

    private static final String PUT_EXTRA_REQUEST_CODE =  "result_code";


    private int REQUEST_CODE;
    private String moduleId;



    public static void startSimprintsRegisterActivity(Activity context, String moduleId, int requestCode){
        Intent intent = new Intent(context, SimPrintsRegisterActivity.class);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID,moduleId);
        intent.putExtra(PUT_EXTRA_REQUEST_CODE,requestCode);
        context.startActivityForResult(intent,requestCode);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SimPrintsUtils.isPackageInstalled(SIMPRINTS_PACKAGE_NAME,getPackageManager())){
            SimPrintsUtils.downloadSimprintIdApk(this);
            return;
        }
        moduleId = getIntent().getStringExtra(Constants.SIMPRINTS_MODULE_ID);
        REQUEST_CODE = getIntent().getIntExtra(PUT_EXTRA_REQUEST_CODE,111);
        startRegister();

    }
    private void startRegister(){
        try{
            SimPrintsHelper simprintsHelper = new SimPrintsHelper(SimPrintsLibrary.getInstance().getProjectId(),
                    SimPrintsLibrary.getInstance().getUserId());
            Intent intent = simprintsHelper.enroll(moduleId);
            startActivityForResult(intent,REQUEST_CODE);
        }catch (IllegalStateException e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( data!=null && resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            Registration registration = data.getParcelableExtra(Constants.SIMPRINTS_REGISTRATION);
            Boolean check = data.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK,false);

            if(check){
                SimPrintsRegistration simprintsRegistration;
                if(registration == null || TextUtils.isEmpty(registration.getGuid())){
                    simprintsRegistration = new SimPrintsRegistration(null);
                    simprintsRegistration.setCheckStatus(false);
                }else{
                    simprintsRegistration = new SimPrintsRegistration(registration.getGuid());
                    simprintsRegistration.setCheckStatus(true);
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SimPrintsConstantHelper.INTENT_DATA,simprintsRegistration);
                setResult(RESULT_OK,returnIntent);
                finish();
            }else {
                showFingerPrintFail(this, new OnDialogButtonClick() {
                    @Override
                    public void onOkButtonClick() {
                        startRegister();
                    }

                    @Override
                    public void onCancelButtonClick() {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED,returnIntent);
                        finish();
                    }
                });
            }



        }else{
            showFingerPrintFail(this, new OnDialogButtonClick() {
                @Override
                public void onOkButtonClick() {
                    startRegister();
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
    private void showFingerPrintFail(Context context, final OnDialogButtonClick onDialogButtonClick){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(getString(R.string.fail_result));
        alertDialog.setCancelable(false);
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
