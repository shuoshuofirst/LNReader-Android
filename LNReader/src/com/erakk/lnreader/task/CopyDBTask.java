package com.erakk.lnreader.task;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.erakk.lnreader.LNReaderApplication;
import com.erakk.lnreader.R;
import com.erakk.lnreader.callback.CallbackEventData;
import com.erakk.lnreader.callback.ICallbackEventData;
import com.erakk.lnreader.callback.ICallbackNotifier;
import com.erakk.lnreader.dao.NovelsDao;

public class CopyDBTask extends AsyncTask<Void, ICallbackEventData, Void> implements ICallbackNotifier {

	private static final String TAG = CopyDBTask.class.toString();
	private ICallbackNotifier callback;
	private final String source;
	private final boolean makeBackup;
	private final String filename;

	public CopyDBTask(boolean makeBackup, ICallbackNotifier callback, String source, String filename) {
		this.makeBackup = makeBackup;
		this.source = source;
		this.callback = callback;
		this.filename = filename;
	}

	@Override
	public void onProgressCallback(ICallbackEventData message) {
		publishProgress(message);
	}

	@Override
	protected Void doInBackground(Void... params) {
		Context ctx = LNReaderApplication.getInstance().getApplicationContext();
		try {
			copyDB(makeBackup);
		} catch (Exception e) {
			String message;
			if (makeBackup) {
				message = ctx.getResources().getString(R.string.copy_db_task_backup_error);
			}
			else {
				message = ctx.getResources().getString(R.string.copy_db_task_restore_error);
			}
			publishProgress(new CallbackEventData(message, source));
			Log.e(TAG, message, e);
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(ICallbackEventData... values) {
		Log.d(TAG, values[0].getMessage());
		if (callback != null)
			callback.onProgressCallback(new CallbackEventData(values[0].getMessage(), source));
	}

	private void copyDB(boolean makeBackup) throws IOException {
		Context ctx = LNReaderApplication.getInstance().getApplicationContext();
		if (makeBackup)
			publishProgress(new CallbackEventData(ctx.getResources().getString(R.string.copy_db_task_backup_start), source));
		else
			publishProgress(new CallbackEventData(ctx.getResources().getString(R.string.copy_db_task_restore_start), source));

		String filePath = NovelsDao.getInstance().copyDB(makeBackup, filename);
		if (filePath == "null") {
			publishProgress(new CallbackEventData(ctx.getResources().getString(R.string.database_not_found), source));
		} else {
			if (makeBackup)
				publishProgress(new CallbackEventData(ctx.getResources().getString(R.string.copy_db_task_backup_complete, filePath), source));
			else
				publishProgress(new CallbackEventData(ctx.getResources().getString(R.string.copy_db_task_restore_complete), source));
		}
	}

	public void setCallbackNotifier(ICallbackNotifier notifier) {
		this.callback = notifier;
	}
}
