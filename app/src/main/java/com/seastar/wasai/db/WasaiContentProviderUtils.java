package com.seastar.wasai.db;

import java.util.ArrayList;
import java.util.HashSet;

import com.seastar.wasai.Entity.SearchHistoryDataEntity;
import com.seastar.wasai.Entity.ShoppingGuideArticlesEntity;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.db.WasaiProviderMetaData.GuideTableMetaData;
import com.seastar.wasai.db.WasaiProviderMetaData.SearchHistoryData;
import com.seastar.wasai.db.WasaiProviderMetaData.ShoppingGuideArticlesData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author Jamie
 *
 */
public class WasaiContentProviderUtils {

	private static WasaiContentProviderUtils sWasaiContentProviderUtils;
	private Context mContext;
	private ContentResolver mContentResolver;
	
	public synchronized static WasaiContentProviderUtils getInstance(Context ctx) {
        if (sWasaiContentProviderUtils == null) {
        	sWasaiContentProviderUtils = new WasaiContentProviderUtils(ctx);
        }

        return sWasaiContentProviderUtils;
    }
	
	private WasaiContentProviderUtils(Context ctx) {
        mContext = ctx;
        mContentResolver = mContext.getContentResolver();
    }
	
	
	//add jamie  用户在app/guide/item停留时间记录，
	private ContentValues createUserActionToContentValues(UserActionOfGuide message){
		final ContentValues values = new ContentValues (); 
		values.put(GuideTableMetaData.SESSION_ID, message.session_id);
		values.put(GuideTableMetaData.TARGET, message.target);
		values.put(GuideTableMetaData.TARGET_ID, message.target_id);
		values.put(GuideTableMetaData.OPERATION, message.operation);
		values.put(GuideTableMetaData.TIMESTAMP, message.timestamp);
		return values;
	}
	
	//添加用户行为到表中   = 用户在app/guide/item停留时间记录，
	public boolean addUserAtionMessage(UserActionOfGuide message){
		ContentValues values = new ContentValues();
		values = createUserActionToContentValues(message);
		final Uri uri = mContentResolver.insert(GuideTableMetaData.GUIDE_URI, values);
		return Integer.parseInt(uri.getLastPathSegment()) > 0 ? true : false;
	}
	
	//从表中删除全部消息 = 用户在app/guide/item停留时间记录，
	public boolean deleteAllUserActionMessage(){
    	int count = mContentResolver.delete(GuideTableMetaData.GUIDE_URI, null, null);
    	return count > 0 ? true : false;
	}
	
	//查询userAction数据 =  用户在app/guide/item停留时间记录，
	public ArrayList<UserActionOfGuide> queryUserActionMessage(){
		ArrayList<UserActionOfGuide> items = new ArrayList<UserActionOfGuide>();
		final Cursor cursor = mContentResolver.query(GuideTableMetaData.GUIDE_URI, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				UserActionOfGuide item = new UserActionOfGuide();
//				item.mID = cursor.getInt(cursor.getColumnIndexOrThrow(GuideTableMetaData._ID));
				item.session_id = cursor.getString(cursor.getColumnIndexOrThrow(GuideTableMetaData.SESSION_ID));
				item.target = cursor.getString(cursor.getColumnIndexOrThrow(GuideTableMetaData.TARGET));
				item.target_id = cursor.getString(cursor.getColumnIndexOrThrow(GuideTableMetaData.TARGET_ID));
				item.operation = cursor.getString(cursor.getColumnIndexOrThrow(GuideTableMetaData.OPERATION));
				item.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(GuideTableMetaData.TIMESTAMP));
				items.add(item);
			}while (cursor.moveToNext());
		}
		cursor.close();
		return items;
	}
	
	//add jamie == 导购文章内点击商品链接记录，
	private ContentValues createUserActionOfShoppingGuideArticlesToContentValues(ShoppingGuideArticlesEntity message){
		final ContentValues values = new ContentValues (); 
//		values.put(ShoppingGuideArticlesData.SESSION_ID, message.mSession_id);
		values.put(ShoppingGuideArticlesData.GUIDE_ID, message.guide_id);
		values.put(ShoppingGuideArticlesData.ITEM_ID, message.item_id);
		values.put(ShoppingGuideArticlesData.TIMESTAMP, message.timestamp);
		return values;
	}
	
	//添加用户行为到表中   = 导购文章内点击商品链接记录,，
	public boolean addUserAtionOfShoppingGuideArticlesData(ShoppingGuideArticlesEntity entity){
		ContentValues values = new ContentValues();
		values = createUserActionOfShoppingGuideArticlesToContentValues(entity);
		final Uri uri = mContentResolver.insert(ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_URI, values);
		return Integer.parseInt(uri.getLastPathSegment()) > 0 ? true : false;
	}
	
	//从表中删除全部消息 = 导购文章内点击商品链接记录,
	public boolean deleteAllUserActionOfShoppingGuideArticlesData(){
    	int count = mContentResolver.delete(ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_URI, null, null);
    	return count > 0 ? true : false;
	}
	
	//查询userAction数据 =  导购文章内点击商品链接记录,，
	public ArrayList<ShoppingGuideArticlesEntity> queryUserActionOfShoppingGuideArticlesData(){
		ArrayList<ShoppingGuideArticlesEntity> items = new ArrayList<ShoppingGuideArticlesEntity>();
		final Cursor cursor = mContentResolver.query(ShoppingGuideArticlesData.SHOPPING_GUIDE_ARTICLES_URI, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				ShoppingGuideArticlesEntity item = new ShoppingGuideArticlesEntity();
//				item.mID = cursor.getInt(cursor.getColumnIndexOrThrow(ShoppingGuideArticlesData._ID));
				item.session_id = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingGuideArticlesData.SESSION_ID));
				item.guide_id = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingGuideArticlesData.GUIDE_ID));
				item.item_id = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingGuideArticlesData.ITEM_ID));
				item.timestamp = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingGuideArticlesData.TIMESTAMP));
				items.add(item);
			}while (cursor.moveToNext());
		}
		cursor.close();
		return items;
	}
	
	
	private ContentValues createSearchHistoryDataToContentValues(SearchHistoryDataEntity message){
		final ContentValues values = new ContentValues ();
		values.put(SearchHistoryData.SEARCH_NAME, message.search_name);
		values.put(SearchHistoryData.SEARCH_TIME, message.search_time);
		return values;
	}
	
	public boolean addSearchHistoryData(SearchHistoryDataEntity entity){
		ContentValues values = new ContentValues();
		values = createSearchHistoryDataToContentValues(entity);

		final Uri uri = mContentResolver.insert(SearchHistoryData.SEARCH_HISTORY_URI, values);
		return Integer.parseInt(uri.getLastPathSegment()) > 0 ? true : false;
	}
	
	public boolean deleteAllSearchHistoryData(){
    	int count = mContentResolver.delete(SearchHistoryData.SEARCH_HISTORY_URI, null, null);
		Log.e("info","count " + count);
		return count > 0 ? true : false;
	}
	public boolean updateSearchHistoryData(SearchHistoryDataEntity entity){
		ContentValues values = new ContentValues();
		values.put(SearchHistoryData.SEARCH_NAME, entity.search_name);
		values.put(SearchHistoryData.SEARCH_TIME, entity.search_time);
		int count = mContentResolver.update(SearchHistoryData.SEARCH_HISTORY_URI,values, "search_name = ?" , new String[]{entity.search_name});
		return  count > 0 ? true : false;
	}

	public ArrayList<SearchHistoryDataEntity> querySearchHistoryData(){
		ArrayList<SearchHistoryDataEntity> items = new ArrayList<SearchHistoryDataEntity>();
		final Cursor cursor = mContentResolver.query(SearchHistoryData.SEARCH_HISTORY_URI, null, null, null, "search_time desc");
		if(cursor.moveToFirst()){
			do{
				SearchHistoryDataEntity item = new SearchHistoryDataEntity();
				item.search_name = cursor.getString(cursor.getColumnIndexOrThrow(SearchHistoryData.SEARCH_NAME));
				item.search_time = cursor.getString(cursor.getColumnIndexOrThrow(SearchHistoryData.SEARCH_TIME));
				items.add(item);
			}while (cursor.moveToNext());
		}
		cursor.close();
		return items;
	}
}
