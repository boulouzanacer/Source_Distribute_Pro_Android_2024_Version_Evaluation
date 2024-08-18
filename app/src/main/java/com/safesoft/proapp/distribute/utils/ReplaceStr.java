package com.safesoft.proapp.distribute.utils;

/**
 * Project: PrintSet0517<br/>
 * Created by Tony on 2018/6/25.<br/>
 * Description:
 */

public class ReplaceStr {

    public synchronized static String ReplaceString(String text) {

        return text.replace("'", "'''");
    }

}
