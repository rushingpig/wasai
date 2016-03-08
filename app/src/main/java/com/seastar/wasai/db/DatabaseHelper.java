package com.seastar.wasai.db;
import com.seastar.wasai.db.WasaiProviderMetaData.GuideTableMetaData;
import com.seastar.wasai.db.WasaiProviderMetaData.SearchHistoryData;
import com.seastar.wasai.db.WasaiProviderMetaData.ShoppingGuideArticlesData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * @author Jamie
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public DatabaseHelper(Context context, String name){
		this(context, name, WasaiProviderMetaData.DATABASE_VERSION);
	}
	
	public DatabaseHelper(Context context, String name, int version){
		this(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + WasaiProviderMetaData.USERACTIONMANAGER_TABLE_NAME + " (" 
				+ GuideTableMetaData._ID + " INTEGER PRIMARY KEY," 
				+ GuideTableMetaData.SESSION_ID + " TEXT," 
				+ GuideTableMetaData.TARGET + " TEXT," 
				+ GuideTableMetaData.TARGET_ID + " TEXT," 
				+ GuideTableMetaData.OPERATION +  " TEXT," 
				+ GuideTableMetaData.TIMESTAMP + " TEXT" + ");");
		
		db.execSQL("CREATE TABLE " + WasaiProviderMetaData.SHOPPINGGUIDEARTICLESDATA_TABLE_NAME + " (" 
				+ ShoppingGuideArticlesData._ID + " INTEGER PRIMARY KEY," 
				+ ShoppingGuideArticlesData.SESSION_ID + " TEXT," 
				+ ShoppingGuideArticlesData.GUIDE_ID + " TEXT," 
				+ ShoppingGuideArticlesData.ITEM_ID + " TEXT," 
				+ ShoppingGuideArticlesData.TIMESTAMP + " TEXT" + ");");
		
		db.execSQL("CREATE TABLE " + WasaiProviderMetaData.SEARCHHISTORYDATA_TABLE_NAME+ " (" 
				+ SearchHistoryData._ID + " INTEGER PRIMARY KEY,"
				+ SearchHistoryData.SEARCH_NAME +" TEXT,"
				+ SearchHistoryData.SEARCH_TIME + " TEXT" + ");");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + WasaiProviderMetaData.USERACTIONMANAGER_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + WasaiProviderMetaData.SHOPPINGGUIDEARTICLESDATA_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + WasaiProviderMetaData.SEARCHHISTORYDATA_TABLE_NAME);
		onCreate(db);
	}

}
