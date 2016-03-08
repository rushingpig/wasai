package com.seastar.wasai.db;

import android.net.Uri;
import android.provider.BaseColumns;
/**
 * @author Jamie
 *
 */
public class WasaiProviderMetaData {
	public static final String AUTHORIY = "com.seastar.wasai.db.wasaiprovidermetadata";
	public static final String DATABASE_NAME = "WasaiProvider.db";
	public static final int DATABASE_VERSION = 8;
	public static final String USERACTIONMANAGER_TABLE_NAME = "guide";
	public static final String SHOPPINGGUIDEARTICLESDATA_TABLE_NAME = "shopping_guide_articles";
	public static final String SEARCHHISTORYDATA_TABLE_NAME = "search_history_data";
	
	public static final class GuideTableMetaData implements BaseColumns{
//		public static final String DEFAULT_SORT_ORDER = "_id asc";
		/**
		 * 用户在app/guide/item停留时间记录，
		 */
		public static final String TABLE_NAME_GUIDE = "guide";
		public static final Uri GUIDE_URI = Uri.parse("content://" + AUTHORIY + "/guide");
		public static final String GUIDE_TYPE = "vnd.android.cursor.dir/vnd.wasaiprovider.guide";
		public static final String GUIDE_TYPE_ITEM = "vnd.android.cursor.item/vnd.wasaiprovider.guide";
		public static final String SESSION_ID = "session_id";
		public static final String TARGET = "target"; ////目标
		public static final String TARGET_ID = "target_id";  //"guide_id/item_id",	//目标id
		public static final String OPERATION = "operation"; //"start/end",	//操作类型
		public static final String TIMESTAMP = "timestamp"; ////时间戳
		
	}
	/**
	 * 导购文章内点击商品链接记录,
	 */
	public static final class ShoppingGuideArticlesData implements BaseColumns {
		public static final String TABLE_NAME_SHOPPING_GUIDE_ARTICLES = "shopping_guide_articles";
		public static final Uri SHOPPING_GUIDE_ARTICLES_URI = Uri.parse("content://" + AUTHORIY + "/shopping_guide_articles");
		public static final String SHOPPING_GUIDE_ARTICLES_TYPE = "vnd.android.cursor.dir/vnd.wasaiprovider.shopping_guide_articles";
		public static final String SHOPPING_GUIDE_ARTICLES_TYPE_ITEM = "vnd.android.cursor.item/vnd.wasaiprovider.shopping_guide_articles";
		public static final String SESSION_ID = "session_id";
		public static final String GUIDE_ID = "guide_id"; 
		public static final String ITEM_ID = "item_id"; 
		public static final String TIMESTAMP = "timestamp"; //时间戳
	}
	
	
	public static final class SearchHistoryData implements BaseColumns {
		public static final String TABLE_NAME__SEARCH_HISTORY_DATA = "search_history_data";
		public static final Uri SEARCH_HISTORY_URI = Uri.parse("content://" + AUTHORIY + "/search_history_data");
		public static final String SEARCH_HISTORY_TYPE = "vnd.android.cursor.dir/vnd.wasaiprovider.search_history_data";
		public static final String SEARCH_HISTORY_TYPE_ITEM = "vnd.android.cursor.item/vnd.wasaiprovider.search_history_data";
		public static final String SEARCH_TIME = "search_time";
		public static final String SEARCH_NAME = "search_name";
	}
	
}
