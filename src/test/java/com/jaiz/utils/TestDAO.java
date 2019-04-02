package com.jaiz.utils;

import com.jaiz.utils.annotations.ScanTarget;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

@DAO
public interface TestDAO {

    @ScanTarget
    @SQL("SELECT bsa.ID,bsa.AUCTION_ID,bsa.AUCTION_ADMIN_ID,bsa.AUCTION_ADMIN_NAME," +
            "bsa.AUCTION_DISTRIBUTION_ID,bsa.AUCTION_DISTRIBUTION_TIME,bsa.AUCTION_STATUS," +
            "bsa.AUCTION_STATUS,bsa.AUCTION_TIME,bsa.SURE_LIST_TIME,bsa.FINAL_C_LOOK_CAR_TIME," +
            "bsa.FINAL_B_LOOK_CAR_ADDRESS,bsa.FINAL_DEALER_ID,bsa.FINAL_PRICE,bsa.MIND_PRICE," +
            "bsa.FINAL_STATUS,bsa.ZONE_ID,bsa.FINAL_B_LOOK_CAR_TIME,bsa.FINAL_C_LOOK_CAR_ADDRESS," +
            "bsa.RB_FAMILY,bsa.BRAND,bsa.SOURCE,bsa.AUCTION_REMAKE,bsa.FIELD_ID,bsa.LICENSE_NUMBER_NEW," +
            "bsa.BID_TIME,bsa.CHECK_MODE,bsa.RECEPTION_FINAL_PRICE,bsa.WORK_TYPE,bsa.UPDATE_TIME," +
            "bsa.TIMEOUT_STATUS,bsa.REQUEST_BUY_PERSON_ID,bsa.BOLT_SALED_STATUS,bsa.DATA_SOURCE_ID," +
            "bsa.CHANNEL_SOURCE_ID,bsa.AUDIT_STATUS,bsa.BID_NUM,bsa.DISTRIBUTE_STATE," +
            "bfw.ID AS followId,bfw.PERSON_TYPE,bfw.FOLLOW_PERSON_ID,bfw.VIOLATE_FLAG," +
            "bfw.FOLLOW_PERSON_NAME,bfw.FOLLOW_STATUS,bfw.NEXT_FOLLOW_TIME,bfw.PRICE," +
            "bfw.VIOLATE_ID,bfw.VIOLATE_PRICE,bfw.LOOK_CAR_TYPE_ID,bfw.LOOK_CAR_CITY_ID," +
            "bfw.LOOK_CAR_ADDRESS,bfw.LOOK_CAR_DEPT_ID,bfw.BID_PRICE,bfw.OLD_PRICE,bfw.MOBILE_PHONE," +
            "bfw.FINANCE_AUDIT,bfw.FINANCE_AUDIT_REMARK,bfw.AUCTION_FINISH_TIME,bfw.IS_SHOP," +
            "bfw.FOLLOW_UPDATE_TIME " +
            "FROM BOSS_SUCCESS_AUCTION bsa " +
            "JOIN BOSS_FOLLOW_WORKFLOW bfw " +
            "ON bsa.id=bfw.SUCCESS_AUCTION_ID " +
            "AND bfw.PERSON_TYPE=:personType #if(:personType==10){ " +
            "AND VIOLATE_ID=0 } " +
            "WHERE bsa.id=:successAuctionId " +
            "ORDER BY bfw.id DESC " +
            "LIMIT 0,1 ")
    String testSelect();

    @ScanTarget
    @SQL("SELECT bsa.ID,bsa.AUCTION_ID,bsa.AUCTION_ADMIN_ID,bsa.AUCTION_ADMIN_NAME from table")
    String test1();
}
