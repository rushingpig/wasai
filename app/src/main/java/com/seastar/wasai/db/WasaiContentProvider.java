package com.seastar.wasai.db;

import java.util.HashMap;

import com.seastar.wasai.db.WasaiProviderMetaData.GuideTableMetaData;
import com.seastar.wasai.db.WasaiProviderMetaData.SearchHistoryData;
import com.seastar.wasai.db.WasaiProviderMetaData.ShoppingGuideArticlesData;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
/**
 *  contentProvide 数据库
 * @author Jamie
 *
 */
public class WasaiContentProvider extends ContentProvider {
	
	public static final UriMatcher mUriMatcher;
	private DatabaseHelper dh;
	
	public static final int INCOMING_USERACTION_COLLECTION = 1;
	public static final int INCOMING_USERACTION_SINGLE = 2;
	public static HashMap<String, String> mUserActionMap; 
	
	public static final int INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION = 3;
	public static final int INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE = 4;
	public static HashMap<String, String> mUserActionMap1; 
	
	public static final int INCOMING_SEARCH_HISTORY_COLLECTION = 5;
	public static final int INCOMING_SEARCH_HISTORY_SINGLE = 6;
	public static HashMap<String, String> mSearchMap; 
	
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "guide", INCOMING_USERACTION_COLLECTION);
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "guide/#", INCOMING_USERACTION_SINGLE);
		
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "shopping_guide_articles", INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION);
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "shopping_guide_articles/#", INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE);
		
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "search_history_data", INCOMING_SEARCH_HISTORY_COLLECTION);
		mUriMatcher.addURI(WasaiProviderMetaData.AUTHORIY, "search_history_data/#", INCOMING_SEARCH_HISTORY_SINGLE);
		
		
		
		mUserActionMap = new HashMap<String, String>();
//		mUserActionMap.put(GuideTableMetaData._ID, GuideTableMetaData._ID);
		mUserActionMap.put(GuideTableMetaData.SESSION_ID, GuideTableMetaData.SESSION_ID);
		mUserActionMap.put(GuideTableMetaData.TARGET, GuideTableMetaData.TARGET);
		mUserActionMap.put(GuideTableMetaData.TARGET_ID, GuideTableMetaData.TARGET_ID);
		mUserActionMap.put(GuideTableMetaData.OPERATION, GuideTableMetaData.OPERATION);
		mUserActionMap.put(GuideTableMetaData.TIMESTAMP, GuideTableMetaData.TIMESTAMP);
		
		
	
		mUserActionMap1 = new HashMap<String, String>();
//		mUserActionMap.put(ShoppingGuideArticlesData._ID, ShoppingGuideArticlesData._ID);
		mUserActionMap1.put(ShoppingGuideArticlesData.SESSION_ID, ShoppingGuideArticlesData.SESSION_ID);
		mUserActionMap1.put(ShoppingGuideArticlesData.GUIDE_ID, ShoppingGuideArticlesData.GUIDE_ID);
		mUserActionMap1.put(ShoppingGuideArticlesData.ITEM_ID, ShoppingGuideArticlesData.ITEM_ID);
		mUserActionMap1.put(ShoppingGuideArticlesData.TIMESTAMP, ShoppingGuideArticlesData.TIMESTAMP);
		
		
		mSearchMap = new HashMap<String, String>();
		mSearchMap.put(SearchHistoryData.SEARCH_NAME, SearchHistoryData.SEARCH_NAME);
		mSearchMap.put(SearchHistoryData.SEARCH_TIME, SearchHistoryData.SEARCH_TIME);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dh.getWritableDatabase();
		int count = 0;
		try {
			switch (mUriMatcher.match(uri)) {
				
			case INCOMING_USERACTION_COLLECTION:
				count = db.delete(GuideTableMetaData.TABLE_NAME_GUIDE, selection, selectionArgs);
				break;
			case INCOMING_USERACTION_SINGLE:
				String userActionId = uri.getPathSegments().get(1);
				count = db.delete(GuideTableMetaData.TABLE_NAME_GUIDE, GuideTableMetaData._ID
						+ "="
						+ userActionId
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				break;
				
			case INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION:
				count = db.delete(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES, selection, selectionArgs);
				break;
			case INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE:
				String userActionId1 = uri.getPathSegments().get(1);
				count = db.delete(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES, ShoppingGuideArticlesData._ID
						+ "="
						+ userActionId1
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				break;
				
			case INCOMING_SEARCH_HISTORY_COLLECTION:
				count = db.delete(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA, selection, selectionArgs);
				break;
			case INCOMING_SEARCH_HISTORY_SINGLE:
				String searchId = uri.getPathSegments().get(1);
				count = db.delete(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA, SearchHistoryData._ID
						+ "="
						+ searchId
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			getContext().getContentResolver().notifyChange(uri, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
			
			case INCOMING_USERACTION_COLLECTION:
				return GuideTableMetaData.GUIDE_TYPE;
			case INCOMING_USERACTION_SINGLE:
				return GuideTableMetaData.GUIDE_TYPE_ITEM;
				
			case INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION:
				return ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_TYPE;
			case INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE:
				return ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_TYPE_ITEM;
				
			case INCOMING_SEARCH_HISTORY_COLLECTION:
				return SearchHistoryData.SEARCH_HISTORY_TYPE;
			case INCOMING_SEARCH_HISTORY_SINGLE:
				return SearchHistoryData.SEARCH_HISTORY_TYPE_ITEM;
			
			
		default:
			throw new IllegalArgumentException("Unknown URI" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final String table;
		final Uri notifyUri;
		switch (mUriMatcher.match(uri)) {
			case INCOMING_USERACTION_COLLECTION:
				table = GuideTableMetaData.TABLE_NAME_GUIDE;
				notifyUri = GuideTableMetaData.GUIDE_URI;
				break;
				
			case INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION:
				table = ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES;
				notifyUri = ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_URI;
				break;
				
			case INCOMING_SEARCH_HISTORY_COLLECTION:
				table = SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA;
				notifyUri = SearchHistoryData.SEARCH_HISTORY_URI;
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
 		}
		
		SQLiteDatabase db = dh.getWritableDatabase();
		long rowId = db.insert(table, null, values);
		if(rowId > 0){
			Uri mInsertedUri = ContentUris.withAppendedId(notifyUri, rowId);
			getContext().getContentResolver().notifyChange(mInsertedUri, null);
			return mInsertedUri;
		}
		throw new SQLException("Failed to insert row into" + uri);
		
		
	}
	
	private ContentResolver mResolver;
	
	@Override
	public boolean onCreate() {
		dh = new DatabaseHelper(getContext(), WasaiProviderMetaData.DATABASE_NAME);
		final Context context = getContext();
		mResolver = context.getContentResolver();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(uri)) {

			
		case INCOMING_USERACTION_COLLECTION:
			qb.setTables(GuideTableMetaData.TABLE_NAME_GUIDE);
			qb.setProjectionMap(mUserActionMap);
			break;
		case INCOMING_USERACTION_SINGLE:
			qb.setTables(GuideTableMetaData.TABLE_NAME_GUIDE);
			qb.setProjectionMap(mUserActionMap);
			qb.appendWhere(GuideTableMetaData._ID + "=" + uri.getPathSegments().get(1));
			break;
			
		case INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION:
			qb.setTables(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES);
			qb.setProjectionMap(mUserActionMap1);
			break;
		case INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE:
			qb.setTables(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES);
			qb.setProjectionMap(mUserActionMap1);
			qb.appendWhere(ShoppingGuideArticlesData._ID + "=" + uri.getPathSegments().get(1));
			break;
			
			
		case INCOMING_SEARCH_HISTORY_COLLECTION:
			qb.setTables(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA);
			qb.setProjectionMap(mSearchMap);
			break;
		case INCOMING_SEARCH_HISTORY_SINGLE:
			qb.setTables(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA);
			qb.setProjectionMap(mSearchMap);
			qb.appendWhere(SearchHistoryData._ID + "=" + uri.getPathSegments().get(1));
			break;
		
		}
//		String orderBy;
//		if(TextUtils.isEmpty(sortOrder)){
//			orderBy = GuideTableMetaData.DEFAULT_SORT_ORDER;
//		}else {
//			orderBy = sortOrder;
//		}

		SQLiteDatabase db = dh.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri contentUri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dh.getWritableDatabase();
		int updates = 0;
		try {
			switch(mUriMatcher.match(contentUri)) {
				
			case INCOMING_USERACTION_COLLECTION:
				updates = db.update(GuideTableMetaData.TABLE_NAME_GUIDE, values, selection, selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
			case INCOMING_USERACTION_SINGLE:
				String userActionId = contentUri.getPathSegments().get(1);
				updates = db.update(GuideTableMetaData.TABLE_NAME_GUIDE, values, GuideTableMetaData._ID
						+ "="
						+ userActionId
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
				
			case INCOMING_SHOPPING_GUIDE_ARTICLES_COLLECTION:
				updates = db.update(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES, values, selection, selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
			case INCOMING_SHOPPING_GUIDE_ARTICLES_SINGLE:
				String userActionId1 = contentUri.getPathSegments().get(1);
				updates = db.update(ShoppingGuideArticlesData.TABLE_NAME_SHOPPING_GUIDE_ARTICLES, values, ShoppingGuideArticlesData._ID
						+ "="
						+ userActionId1
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
				
				
			case INCOMING_SEARCH_HISTORY_COLLECTION:
				updates = db.update(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA, values, selection, selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
			case INCOMING_SEARCH_HISTORY_SINGLE:
				String searchId = contentUri.getPathSegments().get(1);
				updates = db.update(SearchHistoryData.TABLE_NAME__SEARCH_HISTORY_DATA, values, SearchHistoryData._ID
						+ "="
						+ searchId
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
								+ ')' : ""), selectionArgs);
				mResolver.notifyChange(contentUri, null);
				return updates;
				
			default:
				throw new IllegalArgumentException("Can not find the URI: "
						+ contentUri);
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return updates;
	}

}
