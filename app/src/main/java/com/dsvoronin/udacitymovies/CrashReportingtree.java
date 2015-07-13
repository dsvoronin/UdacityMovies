package com.dsvoronin.udacitymovies;

import android.util.Log;

import timber.log.Timber;

public class CrashReportingtree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        //todo report crashes only
        if (priority > Log.WARN) {
            Log.e(tag, message, t);
        }
    }
}
