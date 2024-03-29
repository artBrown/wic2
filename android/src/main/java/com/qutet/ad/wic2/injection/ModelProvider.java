package com.qutet.ad.wic2.injection;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.net.Uri;

import com.qutet.ad.wic2.archframework.Model;
import com.qutet.ad.wic2.explore.ExploreIOModel;
import com.qutet.ad.wic2.feedback.FeedbackHelper;
import com.qutet.ad.wic2.feedback.SessionFeedbackModel;
import com.qutet.ad.wic2.model.ScheduleHelper;
import com.qutet.ad.wic2.myschedule.MyScheduleModel;
import com.qutet.ad.wic2.session.SessionDetailModel;
import com.qutet.ad.wic2.util.SessionsHelper;
import com.qutet.ad.wic2.videolibrary.VideoLibraryModel;

/**
 * Provides a way to inject stub classes when running integration tests.
 */
public class ModelProvider {

    private static SessionDetailModel stubSessionDetailModel = null;

    private static MyScheduleModel stubMyScheduleModel = null;

    private static SessionFeedbackModel stubSessionFeedbackModel = null;

    private static VideoLibraryModel stubVideoLibraryModel = null;

    private static ExploreIOModel stubExploreIOModel = null;

    public static SessionDetailModel provideSessionDetailModel(Uri sessionUri, Context context,
            SessionsHelper sessionsHelper, LoaderManager loaderManager) {
        if (stubSessionDetailModel != null) {
            return stubSessionDetailModel;
        } else {
            return new SessionDetailModel(sessionUri, context, sessionsHelper, loaderManager);
        }
    }

    public static MyScheduleModel provideMyScheduleModel(ScheduleHelper scheduleHelper,
            Context context) {
        if (stubMyScheduleModel != null) {
            return stubMyScheduleModel.initStaticDataAndObservers();
        } else {
            return new MyScheduleModel(scheduleHelper, context).initStaticDataAndObservers();
        }
    }

    public static SessionFeedbackModel provideSessionFeedbackModel(Uri sessionUri, Context context,
            FeedbackHelper feedbackHelper, LoaderManager loaderManager) {
        if (stubSessionFeedbackModel != null) {
            return stubSessionFeedbackModel;
        } else {
            return new SessionFeedbackModel(loaderManager, sessionUri, context, feedbackHelper);
        }
    }

    public static VideoLibraryModel provideVideoLibraryModel(Uri videoUri, Uri myVideosUri,
            Uri filterUri, Activity activity, LoaderManager loaderManager) {
        if (stubVideoLibraryModel != null) {
            return stubVideoLibraryModel;
        } else {
            return new VideoLibraryModel(activity, loaderManager, videoUri, myVideosUri, filterUri);
        }
    }

    public static ExploreIOModel provideExploreIOModel(Uri sessionsUri, Context context,
            LoaderManager loaderManager) {
        if (stubExploreIOModel != null) {
            return stubExploreIOModel;
        } else {
            return new ExploreIOModel(context, sessionsUri, loaderManager);
        }
    }

    public static void setStubModel(Model model) {
        if (model instanceof  ExploreIOModel) {
            stubExploreIOModel = (ExploreIOModel) model;
        } else if (model instanceof  VideoLibraryModel) {
            stubVideoLibraryModel = (VideoLibraryModel) model;
        } else if (model instanceof SessionFeedbackModel) {
            stubSessionFeedbackModel = (SessionFeedbackModel) model;
        } else if (model instanceof SessionDetailModel) {
            stubSessionDetailModel = (SessionDetailModel) model;
        } if (model instanceof MyScheduleModel) {
            stubMyScheduleModel = (MyScheduleModel) model;
        }
    }

}
