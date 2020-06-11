package org.smartregister.simprint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.simprints.libsimprints.Constants;
import com.simprints.libsimprints.Tier;
import com.simprints.libsimprints.Verification;

import static com.simprints.libsimprints.Constants.SIMPRINTS_PACKAGE_NAME;
import static com.simprints.libsimprints.Constants.SIMPRINTS_REFUSAL_FORM;

public class SimPrintsVerifyActivity extends AppCompatActivity {

    public static final String PUT_EXTRA_REQUEST_CODE =  "result_code";


    private int REQUEST_CODE;


    public static void startSimprintsVerifyActivity(Activity context, String moduleId, String guId, int requestCode){
        Intent intent = new Intent(context, SimPrintsVerifyActivity.class);
        intent.putExtra(Constants.SIMPRINTS_MODULE_ID,moduleId);
        intent.putExtra(Constants.SIMPRINTS_VERIFY_GUID,guId);
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
        String moduleId = getIntent().getStringExtra(Constants.SIMPRINTS_MODULE_ID);
        String guId = getIntent().getStringExtra(Constants.SIMPRINTS_VERIFY_GUID);
        REQUEST_CODE = getIntent().getIntExtra(PUT_EXTRA_REQUEST_CODE,111);
        try{
            SimPrintsHelper simprintsHelper = new SimPrintsHelper(SimPrintsLibrary.getInstance().getProjectId(),
                    SimPrintsLibrary.getInstance().getUserId());
            Intent intent = simprintsHelper.verify(moduleId, guId);
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
            Boolean check = data.getBooleanExtra(Constants.SIMPRINTS_BIOMETRICS_COMPLETE_CHECK,false);
            if(check){

                 if (data.getParcelableExtra(SIMPRINTS_REFUSAL_FORM) == null) {
                     SimPrintsVerification simprintsVerification;
                     Verification verification = data.getParcelableExtra(Constants.SIMPRINTS_VERIFICATION);
                     if(verification == null || TextUtils.isEmpty(verification.getGuid())){
                         simprintsVerification = new SimPrintsVerification(null);
                         simprintsVerification.setCheckStatus(false);
                         simprintsVerification.setTier(null);
                     }else{
                         simprintsVerification = new SimPrintsVerification(verification.getGuid());
                         simprintsVerification.setCheckStatus(true);
                         simprintsVerification.setTier(verification.getTier());
                         simprintsVerification.setMaskedTier(getMaskedTier(verification.getTier()));
                     }
                     Intent returnIntent = new Intent();
                     returnIntent.putExtra(SimPrintsConstantHelper.INTENT_DATA,simprintsVerification);
                     setResult(RESULT_OK,returnIntent);
                     finish();
                 } else {
                     Toast.makeText(this, R.string.biometric_declined_message, Toast.LENGTH_SHORT).show();
                     finish();
                 }

            }else {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED,returnIntent);
                finish();
            }

        }
    }

    SimPrintsVerification.MaskedTier getMaskedTier(Tier tier){
        SimPrintsVerification.MaskedTier maskedTier;
        switch (tier){
            case TIER_1:
                maskedTier =  SimPrintsVerification.MaskedTier.TIER_1;
                break;
            case TIER_2:
                maskedTier = SimPrintsVerification.MaskedTier.TIER_2;
                break;
            case TIER_3:
                maskedTier = SimPrintsVerification.MaskedTier.TIER_3;
                break;
            case TIER_4:
                maskedTier = SimPrintsVerification.MaskedTier.TIER_4;
                break;
            case TIER_5:
                maskedTier = SimPrintsVerification.MaskedTier.TIER_5;
                break;
            default:
                maskedTier = null;
        }

        return maskedTier;
    }

}
