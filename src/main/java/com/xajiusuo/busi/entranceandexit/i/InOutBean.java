package com.xajiusuo.busi.entranceandexit.i;

import java.util.Date;

/**
 * Created by shirenjing on 2019/7/5.
 */
public interface InOutBean {

    /***
     * @Author shirenjing
     * @Description 对应编号
     * @Date 18:18 2019/7/5
     * @Param []
     * @return
     **/
    String getId();

    /***
     * @Author shirenjing
     * @Description 通过时间
     * @Date 18:18 2019/7/5
     * @Param []
     * @return
     **/
    Date getPasstime();

    /***
     * @Author shirenjing
     * @Description 门禁卡号
     * @Date 18:18 2019/7/5
     * @Param []
     * @return
     **/
    String getEntrancecardno();

    /**
     * @Author shirenjing
     * @Description 进出类型：0出1进
     * @Date 18:27 2019/7/5
     * @Param []
     * @return
     **/
    String getPasstype();





}
