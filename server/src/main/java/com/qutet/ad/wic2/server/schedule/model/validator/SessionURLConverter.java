/*
 * Copyright 2014 Google Inc. All rights reserved.
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
package com.qutet.ad.wic2.server.schedule.model.validator;

import com.google.gson.JsonPrimitive;

public class SessionURLConverter extends Converter {

  private static final String SESSION_BASE_URL = "https://events.google.com/io2016/schedule?sid=";

  public SessionURLConverter() {
  }

  @Override
  public JsonPrimitive convert(JsonPrimitive sessionId) {
    if (sessionId == null) {
      return null;
    }
    // TODO: Replace with URL shortener and move this processing to a taskqueue.
    return new JsonPrimitive(SESSION_BASE_URL + sessionId.getAsString());
  }
}
