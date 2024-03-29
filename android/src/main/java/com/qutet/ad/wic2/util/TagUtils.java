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

package com.qutet.ad.wic2.util;

import com.qutet.ad.wic2.Config;

/**
 * Utility methods for parsing sessions tags.
 */
public class TagUtils {

    public static boolean isTrackTag(String tagString) {
        return tagString != null && tagString.startsWith(
                Config.Tags.CATEGORY_TRACK + Config.Tags.CATEGORY_SEP);
    }

    public static boolean isThemeTag(String tagString) {
        return tagString != null && tagString.startsWith(
                Config.Tags.CATEGORY_THEME + Config.Tags.CATEGORY_SEP);
    }
}
