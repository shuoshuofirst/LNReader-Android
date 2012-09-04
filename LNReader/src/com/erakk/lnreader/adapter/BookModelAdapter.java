package com.erakk.lnreader.adapter;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.erakk.lnreader.R;
import com.erakk.lnreader.dao.NovelsDao;
import com.erakk.lnreader.model.BookModel;
import com.erakk.lnreader.model.NovelContentModel;
import com.erakk.lnreader.model.PageModel;

public class BookModelAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<BookModel> groups;
	private int read = Color.parseColor("#888888");
	private int notRead = Color.parseColor("#dddddd");
	private int notReadDark = Color.parseColor("#222222");

	public BookModelAdapter(Context context, ArrayList<BookModel> groups) {
		this.context = context;
		this.groups = groups;
	}

//	@Override
//	public void notifyDataSetChanged() {
//		for(Iterator<BookModel> iBook = groups.iterator(); iBook.hasNext();) {
//			BookModel book = iBook.next();
//			for(Iterator<PageModel> iPage = book.getChapterCollection().iterator(); iPage.hasNext();) {
//				PageModel temp = iPage.next();
//				Log.d("notifyDataSetChanged", "downloaded: " + temp.getTitle() + " " + temp.isDownloaded());
//			}
//		}
//		super.notifyDataSetChanged();
//	}
	
	public void addItem(PageModel item, BookModel group) {
		if (!groups.contains(group)) {
			groups.add(group);
		}
		int index = groups.indexOf(group);
		ArrayList<PageModel> ch = groups.get(index).getChapterCollection();
		ch.add(item);
		groups.get(index).setChapterCollection(ch);
	}

	public PageModel getChild(int groupPosition, int childPosition) {
		ArrayList<PageModel> chList = groups.get(groupPosition).getChapterCollection();
		return chList.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		PageModel child = getChild(groupPosition, childPosition);
		LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = infalInflater.inflate(R.layout.expandchapter_list_item, null);
		
		TextView tv = (TextView) view.findViewById(R.id.novel_chapter);
		tv.setText(child.getTitle());
		tv.setTag(child.getPage());
		
		if(child.isFinishedRead()) {
			tv.setTextColor(read);
		}
		else {
			if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("invert_colors", false)) {
				tv.setTextColor(notRead);
			}
			else {
				tv.setTextColor(notReadDark);
			}
		}
		
		TextView tvIsDownloaded = (TextView) view.findViewById(R.id.novel_is_downloaded);
		Log.d("getChildView", "Downloaded " + child.getTitle() + " id " + child.getId() + " : " + child.isDownloaded() );
		if(tvIsDownloaded != null) {
			if(!child.isDownloaded()) {
				tvIsDownloaded.setVisibility(TextView.GONE);
			}
			else {
				tvIsDownloaded.setVisibility(TextView.VISIBLE);
			}
		}
		
		TextView tvLastUpdate = (TextView) view.findViewById(R.id.novel_last_update);
		if(tvLastUpdate != null){
			tvLastUpdate.setText("Last Update: " + child.getLastUpdate().toString());
		}
		
		TextView tvLastCheck = (TextView) view.findViewById(R.id.novel_last_check);
		if(tvLastCheck != null){
			tvLastCheck.setText("Last Check: " + child.getLastCheck().toString());
		}
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		ArrayList<PageModel> chList = groups.get(groupPosition).getChapterCollection();
		return chList.size();
	}

	public BookModel getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		BookModel group =  getGroup(groupPosition);
		LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inf.inflate(R.layout.expandvolume_list_item, null);
		TextView tv = (TextView) view.findViewById(R.id.novel_volume);
		tv.setText(group.getTitle());
		
		// check if all chapter is read
		boolean readAll = true;
		for(Iterator<PageModel> iPage = group.getChapterCollection().iterator(); iPage.hasNext();) {
			PageModel page = iPage.next();
			if(!page.isFinishedRead()) {
				readAll = false;
				break;
			}
		}
		if(readAll) {
			tv.setTextColor(read);
		}
		else {
			if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("invert_colors", false)) {
				tv.setTextColor(notRead);
			}
			else {
				tv.setTextColor(notReadDark);
			}
		}
		
		return view;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

//	public void invertColorMode(boolean invert) {
//		invertColors = invert;
//		this.notifyDataSetInvalidated();
//	}
}