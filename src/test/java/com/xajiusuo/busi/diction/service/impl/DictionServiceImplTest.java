package com.xajiusuo.busi.diction.service.impl;

import com.xajiusuo.busi.diction.service.DictionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hadoop on 19-10-14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DictionServiceImplTest {


    @Autowired
    DictionService dictionService;

    @Test
    public void testGetValueByKey(){
        System.out.println(dictionService.getValueByKey("k0101"));
    }
}
