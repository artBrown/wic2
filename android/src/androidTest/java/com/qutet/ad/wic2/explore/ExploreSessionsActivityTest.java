/*
 * Copyright (c) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.qutet.ad.wic2.explore;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.qutet.ad.wic2.testutils.BaseActivityTestRule;
import com.qutet.ad.wic2.testutils.NavigationUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExploreSessionsActivityTest {

    @Rule
    public BaseActivityTestRule<ExploreSessionsActivity> mActivityRule =
            new BaseActivityTestRule<ExploreSessionsActivity>(
                    ExploreSessionsActivity.class, null, true);

    @Test
    public void navigationIcon_DisplaysAsUp() {
        NavigationUtils.checkNavigationIconIsUp();
    }
}
