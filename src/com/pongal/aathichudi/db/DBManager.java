package com.pongal.aathichudi.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pongal.aathichudi.model.Item;
import com.pongal.aathichudi.model.MaximRow;

public class DBManager {
	Context context;
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		this.context = context;
		helper = new DBHelper(context);
		db = helper.openDatabase();
		helper.close();
	}

	public Item getContents() {
		List<MaximRow> rows = getItems();
		Log.d(null, "Size" + rows.size());
		Map<Integer, Item> refMap = new HashMap<Integer, Item>();
		for(MaximRow row : rows) {
			refMap.put(row.id, new Item(row));
		}
		Item root = null;
		for(MaximRow row : rows){
			Item currItem = refMap.get(row.id);
			if(row.group_id != 0) {
				Item parent = refMap.get(row.group_id);
				parent.addChild(currItem);
			} else {
				root = currItem;
			}
		}
		return root;
	}

	List<MaximRow> getItems() {
		List<MaximRow> items = new ArrayList<MaximRow>();
		Cursor cursor;
		try {
			cursor = db.query("contents", new String[] { "id", "text",
					"shortDesc", "group_id" }, null, null, null, null, null,
					null);

			while (cursor.moveToNext()) {
				items.add(new MaximRow(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getInt(3)));
			}
			cursor.close();

		} catch (SQLException e) {
			Log.d("DB ERROR", e.toString());
			e.printStackTrace();
		}
		db.close();
		return items;
	}

	public List<MaximRow> getMaxims() {
		List<MaximRow> items = new ArrayList<MaximRow>();
		Cursor cursor;
		try {
			cursor = db.query("contents", new String[] { "text",
					"shortDesc"}, "shortDesc != ''", null, null, null, null,
					null);

			while (cursor.moveToNext()) {
				items.add(new MaximRow(null, cursor.getString(0),
						cursor.getString(1), null));
			}
			cursor.close();

		} catch (SQLException e) {
			Log.d("DB ERROR", e.toString());
			e.printStackTrace();
		}
		db.close();
		return items;
	}
}