package com.neoscaler.cryptotrends.common;

public final class SharedPrefsKeys {

  // Setting keys

  @Deprecated
  public final static String SHOW_ONLY_FAVORITES = "viewsettings.showonlyfavorites";

  /* Valid values: ALL, FAVS, WATCHED */
  public final static String CURRLIST_BOTTOMNAV_ACTIVE = "viewsettings.currencylist.bottomnav.active";

  public final static String SETTINGS_GENERAL_DISPLAYCURRENCY = "settings.currencylist.basecurrency";

  public final static String SETTINGS_SMARTALARMS_GLOBALMARKET_ACTIVE = "settings.smartalarms.globalmarket.active";
  public final static String SETTINGS_SMARTALARMS_GLOBALMARKET_THRESHOLD = "settings.smartalarms.globalmarket.percentthreshold";

  public final static String SMARTALARMS_GLOBALMARKETCAP = "smartalarm.globalmarket.marketcap";
  public final static String SMARTALARMS_GLOBALMARKETCAP_LASTUPDATED = "smartalarm.globalmarket.lastUpdated";

  public final static String SETTINGS_GENERAL_ANALYTICS_ACTIVE = "settings.general.analytics.active";


  // No instantiation
  private SharedPrefsKeys() {
  }
}
