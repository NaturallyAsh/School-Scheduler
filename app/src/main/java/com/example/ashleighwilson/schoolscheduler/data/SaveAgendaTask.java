package com.example.ashleighwilson.schoolscheduler.data;

import android.content.Context;
import android.os.AsyncTask;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.models.AgendaModel;

public class SaveAgendaTask extends AsyncTask<AgendaModel, Void, AgendaModel> {

    private Context context;

    public SaveAgendaTask() {
        super();
        this.context = MySchedulerApp.getInstance();
    }

    @Override
    protected AgendaModel doInBackground(AgendaModel... params) {
        AgendaModel model = params[0];

        model = DbHelper.getInstance().addAgenda(model);

        return model;
    }

    @Override
    protected void onPostExecute(AgendaModel model) {
        super.onPostExecute(model);
    }
}
