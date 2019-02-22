package com.jaiz.utils;

import net.paoding.rose.jade.annotation.SQL;

public interface TestInterface {

    // TODO 记得调试完毕后删除

    @SQL("SELECT tp.carAge,ROUND(SUM(tp.REMAINING_RATE) / COUNT(1) * 100, 2) rate FROM " +
            "(SELECT BRAND_NAME,REMAINING_RATE," +
            "(CASE WHEN (:dateYear - YEAR (FACTORY_TIME)) < 1 THEN 1 ELSE (:dateYear - YEAR (FACTORY_TIME)) END) carAge " +
            "FROM BOSS_SUCCESS_AUCTION_REMAINING_RATE WHERE BRAND_NAME = :brand AND FACTORY_TIME < :nowDate " +
            ") tp GROUP BY tp.carAge HAVING tp.carAge < :year ORDER BY tp.carAge")
    String testDao();
}
