package org.smartregister.simprint.sample;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import org.smartregister.simprint.SimPrintsConstantHelper;
import org.smartregister.simprint.SimPrintsIdentification;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.simprint.SimPrintsLibrary;
import org.smartregister.simprint.SimPrintsRegisterActivity;
import org.smartregister.simprint.SimPrintsRegistration;
import org.smartregister.simprint.SimPrintsVerifyActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_REGISTER = 1234;
    private static final int REQUEST_CODE_VERIFY = 1334;
    private static final int REQUEST_CODE_IDENTIFY = 1445;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimPrintsLibrary.init(MainActivity.this, BuildConfig.SIMPRINTS_PROJECT_ID,"global_module");

        findViewById(R.id.capture_finger_print_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimPrintsRegisterActivity.startSimprintsRegisterActivity(MainActivity.this,"mpower",REQUEST_CODE_REGISTER);

            }
        });
        findViewById(R.id.verify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimPrintsVerifyActivity.startSimprintsVerifyActivity(MainActivity.this,"mpower","12312312312",REQUEST_CODE_VERIFY);

            }
        });

        findViewById(R.id.identify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimPrintsIdentifyActivity.startSimprintsIdentifyActivity(MainActivity.this, "global_module", REQUEST_CODE_IDENTIFY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){

            switch (requestCode){
                case  REQUEST_CODE_REGISTER:
                    SimPrintsRegistration simprintsRegistration =(SimPrintsRegistration) data.getSerializableExtra(SimPrintsConstantHelper.INTENT_DATA);
                    ((TextView)findViewById(R.id.text_status)).setText("GUID:"+simprintsRegistration.getGuid()+":status:"+simprintsRegistration.getCheckStatus());
                    break;
                case REQUEST_CODE_VERIFY:
                    SimPrintsRegistration verifyResults = (SimPrintsRegistration) data.getSerializableExtra(SimPrintsConstantHelper.INTENT_DATA);
                    ((TextView)findViewById(R.id.text_status)).setText("verification status:"+verifyResults.getCheckStatus());
                    break;
                case REQUEST_CODE_IDENTIFY:
                    ArrayList<SimPrintsIdentification> identifications = (ArrayList<SimPrintsIdentification>) data.getSerializableExtra(SimPrintsConstantHelper.INTENT_DATA);
                    StringBuilder identificationsResults = new StringBuilder();
                    for (SimPrintsIdentification identification : identifications){
                        identificationsResults.append(identification.getGuid());
                        identificationsResults.append("\n");
                    }
                    ((TextView)findViewById(R.id.text_status)).setText("Identification Results:"+identificationsResults);
                    break;

            }
        }
    }
}
