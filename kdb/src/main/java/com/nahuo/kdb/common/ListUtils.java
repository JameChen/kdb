package com.nahuo.kdb.common;

import java.util.List;

public class ListUtils {

    /**
     * @description 判断list是不是空
     * @created 2015-4-13 下午5:51:24
     * @author ZZB
     */
    public static boolean isEmpty(List list) {
        boolean isEm;
        if (list != null) {
            if (list.size() > 0) {
                isEm = false;
            } else {
                isEm = true;
            }
        } else {
            isEm = true;
        }
        return isEm;
    }

    /**
     * @description 获取list的长度
     * @created 2015-4-13 下午5:53:18
     * @author ZZB
     */
    public static int getSize(List list) {
        return list == null ? 0 : list.size();
    }
}
