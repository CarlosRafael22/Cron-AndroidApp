package chron.carlosrafael.chatapp;

import android.content.Context;

/**
 * Created by CarlosRafael on 13/11/2016.
 */
public class ForegroundHandler {

    //VAI VIRAR UM SINGLETON PRA TER SO UM NO PROGRAMA
//    private static ForegroundHandler foregroundHandler;
//    private Context myContext;
//    private String activityName;
//    private String expecificCaseTitle;

    private static boolean activityVisible;

//    private ForegroundHandler(Context context){
//        myContext = context;
//    }
//
//    public static ForegroundHandler getForegroundHandler(Context context){
//        if(foregroundHandler == null){
//            foregroundHandler = new ForegroundHandler(context);
//        }
//        return foregroundHandler;
//    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }


//    public String getActivityName(){
//        return activityName;
//    }
//
//    public void setActivityName(String activity){
//        this.activityName = activity;
//    }
//
//    public String getExpecificCaseTitle() {
//        return expecificCaseTitle;
//    }
//
//    public void setExpecificCaseTitle(String expecificCaseName) {
//        this.expecificCaseTitle = expecificCaseName;
//    }
}
