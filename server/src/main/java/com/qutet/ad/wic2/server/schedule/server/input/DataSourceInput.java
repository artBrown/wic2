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
package com.qutet.ad.wic2.server.schedule.server.input;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.qutet.ad.wic2.server.schedule.input.fetcher.EntityFetcher;
import com.qutet.ad.wic2.server.schedule.input.fetcher.VendorAPIEntityFetcher;
import com.qutet.ad.wic2.server.schedule.model.JsonDataSource;
import com.qutet.ad.wic2.server.schedule.model.JsonDataSources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Encapsulates the logic to extract a set of datasources from a source.
 */
public abstract class DataSourceInput<EnumType extends Enum<EnumType>> {
  static Logger LOG = Logger.getLogger(VendorAPIEntityFetcher.class.getName());

  public abstract Class<EnumType> getType();

  private EntityFetcher fetcher;

  public DataSourceInput(EntityFetcher fetcher) {
    this.fetcher = fetcher;
  }

  public EntityFetcher getFetcher() {
    return fetcher;
  }

  void setFetcher(EntityFetcher fetcher) {
    this.fetcher = fetcher;
  }

  public JsonDataSources fetchAllDataSources() throws IOException {
    JsonDataSources sources = new JsonDataSources();
    for (EnumType type: getType().getEnumConstants()) {
      JsonArray data = fetch(type);
      if (LOG.isLoggable(Level.INFO)) {
        LOG.info("result for "+type+": entities="+data.size());
      }
      sources.addSource(new JsonDataSource(type, data));
    }
    return sources;
  }

  public JsonArray fetch(EnumType entityType) throws IOException {
    JsonElement element = getFetcher().fetch(entityType, null);
    if (element == null) {
      return null;
    }
    if (element.isJsonArray()) {
        return element.getAsJsonArray();
    } else {
      throw new JsonParseException("Invalid response from DataSourceInput. Expected "
          + "a JsonArray, but fetched "+element.getClass().getName()
          +". Entity fetcher is "+getFetcher());
    }  }

}
