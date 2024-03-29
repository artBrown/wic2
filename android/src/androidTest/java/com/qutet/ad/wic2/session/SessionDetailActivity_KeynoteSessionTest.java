/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qutet.ad.wic2.session;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.qutet.ad.wic2.R;
import com.qutet.ad.wic2.mockdata.SessionsMockCursor;
import com.qutet.ad.wic2.mockdata.SpeakersMockCursor;
import com.qutet.ad.wic2.mockdata.TagMetadataMockCursor;
import com.qutet.ad.wic2.provider.ScheduleContract;
import com.qutet.ad.wic2.testutils.BaseActivityTestRule;
import com.qutet.ad.wic2.testutils.NavigationUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests for {@link SessionDetailActivity} when showing a keynote session.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SessionDetailActivity_KeynoteSessionTest {

    public static final String SESSION_ID = "__keynote__";

    private Uri mSessionUri = ScheduleContract.Sessions.buildSessionUri(SESSION_ID);

    @Rule
    public BaseActivityTestRule<SessionDetailActivity> mActivityRule =
            new BaseActivityTestRule<SessionDetailActivity>(SessionDetailActivity.class,
                    // Create a stub model to simulate a keynote session
                    new StubSessionDetailModel(mSessionUri,
                            InstrumentationRegistry.getTargetContext(),
                            SessionsMockCursor.getCursorForKeynoteSession(),
                            SpeakersMockCursor.getCursorForNoSpeaker(),
                            TagMetadataMockCursor.getCursorForSingleTagMetadata()), true) {
                        @Override
                        protected Intent getActivityIntent() {
                            // Create intent to load the session.
                            return new Intent(Intent.ACTION_VIEW, mSessionUri);
                        }
                    };

    @Test
    public void sessionTitle_ShowsCorrectTitle() {
        onView(withId(R.id.session_title)).check(matches(
                allOf(withText(SessionsMockCursor.FAKE_TITLE_KEYNOTE), isDisplayed())));
    }

    @Test
    public void headerImage_IsVisible() {
        onView(withId(R.id.session_photo)).check(matches(isDisplayed()));
    }

    @Test
    public void speakersSection_IsNotVisible() {
        onView(withId(R.id.session_speakers_block)).check(matches(not(isDisplayed())));
    }

    @Test
    public void tagSection_IsNotVisible() {
        onView(withId(R.id.session_tags_container)).check(matches(not(withEffectiveVisibility(
                ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void navigationIcon_DisplaysAsUp() {
        NavigationUtils.checkNavigationIconIsUp();
    }
}
