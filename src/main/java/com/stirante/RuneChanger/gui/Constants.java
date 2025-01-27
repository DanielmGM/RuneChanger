package com.stirante.RuneChanger.gui;

import com.stirante.RuneChanger.model.github.Version;

public class Constants {
    public static final int MARGIN = 10;

    //app name
    public static final String APP_NAME = "RuneChanger";

    //version string
    public static final String VERSION_STRING = Version.INSTANCE.version;

    //github latest release url
    public static final String LATEST_RELEASE_URL = "https://api.github.com/repos/stirante/RuneChanger/releases/latest";

    //percentage positions and lengths
    public static final float RUNE_BUTTON_POSITION_X = 0.3015625f;
    public static final float RUNE_BUTTON_POSITION_Y = 0.9305556f;
    public static final float RUNE_MENU_X = 0.36640626f;
    public static final float RUNE_MENU_Y = 0.9236111f;
    public static final float RUNE_ITEM_HEIGHT = 0.050000012f;
    public static final float RUNE_ITEM_WIDTH = 0.15000004f;
    public static final float QUICK_CHAT_X = 0.2678125f;
    public static final float QUICK_CHAT_Y = 0.96666664f;
    public static final float RUNE_BUTTON_SIZE = 0.039f;
    public static final float FONT_SIZE = 0.0208333f;
    public static final float CHAMPION_TILE_SIZE = 0.097222224f;
    public static final float CHAMPION_SUGGESTION_WIDTH = 0.1388889f;
}
