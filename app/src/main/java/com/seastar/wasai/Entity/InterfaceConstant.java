package com.seastar.wasai.Entity;

import com.seastar.wasai.BuildConfig;

/**
* @ClassName: InterfaceConstant 
* @Description: 接口地址配置
* @author 杨腾
* @date 2015年4月8日 上午9:06:23
 */
public class InterfaceConstant {

	public final static String INTERFACE_HOST = BuildConfig.HOST;
	public final static String SHARE_HOST = BuildConfig.HOST_SHARE;
	public final static String ORDER_HOST = BuildConfig.HOST_ORDER;
	public final static String ACTIVITY_HOST = BuildConfig.HOST_ACTIVITY;

	/**
	 * 导购列表
	 */
	public final static String GUIDE_LIST = INTERFACE_HOST + "/guide/list";
	
	public final static String GUIDE_SHORT_LIST = INTERFACE_HOST + "/guide/shortlist";
	
	/**
	 * 标签
	 */
	public final static String TAG = INTERFACE_HOST + "/tags";
	
	/**
	 * 导购详情
	 */
	public final static String GUIDE = INTERFACE_HOST + "/guide";
	
	/**
	 * 哇点列表
	 */
	public final static String FOCUS_LIST = INTERFACE_HOST + "/focus/list";
	
	/**
	 * 哇点详情
	 */
	public final static String FOCUS = INTERFACE_HOST + "/focus";
	
	/**
	 * 专题列表
	 */
	public final static String TOPIC_LIST = INTERFACE_HOST + "/topic/list";
	
	/**
	 * 专题详情
	 */
	public final static String TOPIC = INTERFACE_HOST + "/topic";
	
	/**
	 * 分类列表
	 */
	public final static String CATEGORY_LIST = INTERFACE_HOST + "/catelog/list";
	
	/**
	 * 分类详情
	 */
	public final static String CATEGORY = INTERFACE_HOST + "/catelog";
	
	/**
	 * 商品列表
	 */
	public final static String ITEM_LIST = INTERFACE_HOST + "/item/category";

	/**
	 * 大数据推荐商品列表
	 */
	public final static String ITEM_LIST_BIG_DATA = INTERFACE_HOST + "/item/recommend";

	public final static String SUPER_ITEM_LIST = INTERFACE_HOST + "/itemsuper/home/list";

	public final static String SUPER_ITEM_LIST_CAT = INTERFACE_HOST + "/itemsuper/list";

	/**
	 * 商品详情
	 */
	public final static String ITEM = INTERFACE_HOST + "/item";

	/**
	 * 超返商品详情
	 */
	public final static String ITEM_SUPER = INTERFACE_HOST + "/itemsuper";

	/**
	 * 奖品详情
	 */
	public final static String AWARD = INTERFACE_HOST + "/activity/prize";

	/**
	 * 评论
	 */
	public static final String COMMENT = INTERFACE_HOST + "/comment";
	
	/**
	 * 导购收藏
	 */
	public static final String FAVORITE = INTERFACE_HOST + "/favorite";

	/**
	 * 商品收藏夹
	 */
	public static final String WISHLIST = INTERFACE_HOST + "/wishlist";
	public static final String GET_ITEM_DETAIL = INTERFACE_HOST + "/item";
	
	/**
	 * 商品收藏
	 */
	public static final String WISH = INTERFACE_HOST + "/wishlist/wish";

	/**
	 * SESSION
	 */
	public static final String USER_SESSION = INTERFACE_HOST + "/user/session";

	/**
	 * User
	 */
	public static final String USER = INTERFACE_HOST + "/user";

	/**
	 * 查找对应导购是否被用户收藏
	 */
	public static final String FAVORITE_GUIDE = INTERFACE_HOST + "/favorite/guide";
	/**
	 * 查找导购的tag
	 */
	public static final String GUIDE_TAGS = INTERFACE_HOST + "/tags/guides";

	/**
	 * 发送短信
	 */
	public static final String SENDSMS = INTERFACE_HOST + "/user/send_sms";
	
	/**
	 * 验证短信码
	 */
	public static final String CHECKSMSCODE = INTERFACE_HOST + "/user/check_smscode";
	
	/**
	 * 登录
	 */
	public static final String LOGIN = INTERFACE_HOST + "/user/login";
	
	/**
	 * 检查帐号是否存在
	 */
	public static final String EXIST = INTERFACE_HOST + "/user/exist";
	
	/**
	 * 修改密码
	 */
	public static final String ALTERPWD = INTERFACE_HOST + "/user/alter_password";

	public static final String BIND_ALIPAY = INTERFACE_HOST + "/user/bind_alipay";

	public static final String EXCHANGE = INTERFACE_HOST + "/user/exchange";

	/**
	 * 活动
	 */
	public static final String ACTIVITY_LIST = INTERFACE_HOST + "/activity/list";

	/**
	 * 活动详情
	 */
	public static final String ACTIVITY_DETAIL = INTERFACE_HOST + "/activity";

	/**
	 * 登出
	 */
	public static final String LOGINOUT = INTERFACE_HOST + "/user/logout";	
	
	/**
	 * 通知后台创建session
	 */
	public static final String USERSESSION = INTERFACE_HOST + "/user/session";	
	
	/**
	 * 导购 分享
	 */
	public static final String GUIDE_SHARE = SHARE_HOST + "/share/guide";

	/**
	 * 商品 分享
	 */
	public static final String ITEM_SHARE = SHARE_HOST + "/share/item";

	/**
	 * 奖品 分享
	 */
	public static final String AWARD_SHARE = SHARE_HOST + "/share/award";

	/**
	 * 活动 分享
	 */
	public static final String ACTIVITY_SHARE = SHARE_HOST + "/share/activity";

	/**
	 * 店铺 分享
	 */
	public static final String STORE_SHARE = SHARE_HOST + "/share/store";

	/**
	 * 应用 分享
	 */
	public static final String APP_SHARE = SHARE_HOST + "/share/app";
	
	
	/**
	 * 日志
	 */
	public static final String USER_ACTION_LOG = INTERFACE_HOST + "/log";

	/**
	 * 修改昵称
	 */
	public static final String UPDATE_NICKNAME = INTERFACE_HOST + "/user/alter_nickname";

	/**
	 * 修改头像
	 */
	public static final String UPLOAD_AVATAR = INTERFACE_HOST + "/user/alter_avatar";
	
	/**
	 * 版本
	 */
	public static final String GET_UPDATE_VERSION = INTERFACE_HOST + "/version";	
	
	public static final String PRODUCT_CATEGORY_LIST = INTERFACE_HOST + "/category/list";
	
	/**
	 * 分享
	 */
	public static final String SHARE = INTERFACE_HOST + "/share";


	public final static String SEARCH_LIST = INTERFACE_HOST + "/search";
	public final static String KEYWORD_LIST = INTERFACE_HOST + "/keyword";


	public final static String SIGNIN_POINTS_LIST = INTERFACE_HOST + "/signin/points/";



	public final static String GET_FOCUS_LIST = INTERFACE_HOST + "/marquee/list";

	public final static String GET_ICON_MENU_LIST = INTERFACE_HOST + "/iconmenu/list";

	public final static String STORE_DETAIL = INTERFACE_HOST + "/shopsale";

	public final static String STORE_LIST = INTERFACE_HOST + "/shopsale/list";

	public final static String SIGIN_PRIZE_LIST = INTERFACE_HOST + "/signin/prize?";

	public final static String FANLI_WALLET_AMOUNT = INTERFACE_HOST + "/fanli/wallet/amount?";

	public final static String FANLI_INCOME = INTERFACE_HOST + "/fanli/wallet/income/bc?";

	public final static String FANLI_TOBEAVAIL = INTERFACE_HOST + "/fanli/wallet/tobeavail/bc?";

	public final static String SIGIN_TOTALPOINTS = INTERFACE_HOST + "/signin/totalPoints/";

	public final static String POST_ORDERS = ORDER_HOST + "/order/tmp";

	public final static String ORDER = INTERFACE_HOST + "/fanli/order/bc";
	public final static String ORDER_DETAIL = INTERFACE_HOST + "/fanli/order/detail/bc";

	public final static String ORDER_BACK = INTERFACE_HOST + "/fanli/order/chargeback";

	public final static String FANLI_WALLET_EXPENDITURE = INTERFACE_HOST + "/fanli/wallet/expenditure?";

	public final static String PRODUCT_ITEMSUPER = INTERFACE_HOST + "/itemsuper/list/category"; //商品页返利


	public static String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	public static String WX_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

	public static String FIND_ORDER = INTERFACE_HOST +"/fanli/order/appeal/add";

	public static String HELP_CENTER = SHARE_HOST +"/help";
	public static String HELP_COURSE = SHARE_HOST +"/help/course";
	public static String SCHOOL = SHARE_HOST +"/school";
	public static String CONTACT = SHARE_HOST +"/contact";

	public final static String ACTIVITY_POINTS_EXCHANGE = INTERFACE_HOST + "/activity/points/exchange"; //积分兑换

}
