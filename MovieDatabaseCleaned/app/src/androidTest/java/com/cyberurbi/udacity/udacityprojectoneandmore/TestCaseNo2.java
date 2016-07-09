package com.cyberurbi.udacity.udacityprojectoneandmore;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by Marcin Gruszecki
 * on 2015-07-27
 */
public class TestCaseNo2 extends ApplicationTestCase<Application> {

    TestCaseNo2()
    {
        super(Application.class);

        boolean bOne = true;
        boolean bTwo = true;

        assertEquals(bOne,bTwo);

    }
}
