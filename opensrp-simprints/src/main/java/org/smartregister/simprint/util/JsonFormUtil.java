package org.smartregister.simprint.util;

import android.app.Application;
import android.database.Cursor;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.Repository;
import org.smartregister.simprint.SimPrintsLibrary;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2019-12-09.
 */
public class JsonFormUtil extends JsonFormUtils {

    public static String lookForClientBaseEntityIds(String guid){
        String baseEntityId = "";
        Client client = null;

        String queryClient = "select json from client where json LIKE '%"+guid+"%' ";
        Cursor cursor = SimPrintsLibrary.getInstance().getRepository().getReadableDatabase().rawQuery(queryClient, null);

        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()){
                client = org.smartregister.util.AssetHandler.jsonStringToJava(cursor.getString(0), Client.class);
                baseEntityId = client.getBaseEntityId();
                cursor.moveToNext();
            }
        }catch (Exception e){
            Timber.e(e, e.toString());
        }finally {
            cursor.close();
        }

        return baseEntityId;
    }

}
