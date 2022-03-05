package com.ice.good.lib.ui.switchtab;

import android.content.Context;
import android.graphics.Color;

import com.ice.good.lib.lib.util.ResUtil;
import com.ice.good.lib.ui.R;

import java.util.ArrayList;
import java.util.List;

public class TabUtil {
    public static List<TabInfo> getTabInfoList(Context context) {
        List<TabInfo> list = new ArrayList<>();
        TabInfo info1 = new TabInfo("Tab1左", R.layout.switch_tab_item_style1, ResUtil.INSTANCE.getColor(R.color.tab_color_1), Color.WHITE, R.drawable.switch_tab_left_nor, R.drawable.switch_tab_left_pre);
        list.add(info1);

        TabInfo info3 = new TabInfo("Tab2中", R.layout.switch_tab_item_style1, ResUtil.INSTANCE.getColor(R.color.tab_color_1), Color.WHITE, R.drawable.switch_tab_center_nor, R.drawable.switch_tab_center_pre);
        list.add(info3);

        TabInfo info5 = new TabInfo("Tab3右", R.layout.switch_tab_item_style1, ResUtil.INSTANCE.getColor(R.color.tab_color_1), Color.WHITE, R.drawable.switch_tab_right_nor, R.drawable.switch_tab_right_pre);
        list.add(info5);
        return list;
    }
}
