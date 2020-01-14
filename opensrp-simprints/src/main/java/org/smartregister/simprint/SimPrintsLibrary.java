package org.smartregister.simprint;

import android.content.Context;

import org.smartregister.repository.Repository;

public class SimPrintsLibrary {

    private static SimPrintsLibrary instance;
    private String projectId;
    private String userId;
    private Context context;
    private Repository repository;

    private SimPrintsLibrary(Context context, String projectId, String userId){
        this.context = context;
        this.projectId = projectId;
        this.userId = userId;

    }

    private SimPrintsLibrary(Context context, String projectId, String userId, Repository repository){
        this.context = context;
        this.projectId = projectId;
        this.userId = userId;
        this.repository = repository;

    }

    public static void init(Context context,String projectId,String userId){
        if(instance == null){
            instance = new SimPrintsLibrary(context,projectId,userId);
        }
    }

    public static void init(Context context,String projectId,String userId, Repository repository){
        if(instance == null){
            instance = new SimPrintsLibrary(context,projectId,userId, repository);
        }
    }

    public static SimPrintsLibrary getInstance() throws IllegalStateException{
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call "
                    + SimPrintsLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class ");
        }
        return instance;
    }
    public String getProjectId(){
        return projectId;
    }
    public String getUserId(){
        return userId;
    }

    public Context getContext() {
        return context;
    }

    public Repository getRepository() {
        return repository;
    }
}
