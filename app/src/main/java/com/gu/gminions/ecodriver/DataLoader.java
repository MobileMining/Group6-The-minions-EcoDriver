package com.gu.gminions.ecodriver;


import android.content.Context;
import android.content.AsyncTaskLoader;

public abstract class DataLoader<DataType> extends AsyncTaskLoader<DataType> {

    private DataType mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(DataType data) {
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
