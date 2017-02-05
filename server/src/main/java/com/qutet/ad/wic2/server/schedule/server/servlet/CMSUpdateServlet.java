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
package com.qutet.ad.wic2.server.schedule.server.servlet;

import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.qutet.ad.wic2.server.schedule.Config;
import com.qutet.ad.wic2.server.schedule.model.JsonDataSource;
import com.qutet.ad.wic2.server.schedule.model.JsonDataSources;
import com.qutet.ad.wic2.server.schedule.server.cloudstorage.CloudFileManager;
import com.qutet.ad.wic2.server.schedule.server.input.VendorDynamicInput;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that runs the Updater flow. Can be called from a cron job or directly.
 * The parameter force="true" forces a new update, even if the API session data has
 * not changed since the last run.
 *
 */
public class CMSUpdateServlet extends HttpServlet {

  private static final HashSet<String> nonAdminUsers = new HashSet<String>();
  static {
    nonAdminUsers.add("ldale@google.com");
  }

  private final UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if ("true".equals(req.getParameter("cron"))) {
      // Request is triggered by cron script, treat as POST
      doPost(req, resp);
      return;
    }

    if (!performBasicChecking(req, resp)) {
      return;
    }
    boolean showOnly = "true".equals(req.getParameter("show"));
    if (showOnly) {
      process(resp, true);
    } else {
      redirectToConfirmationPage(req, resp);
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (!performBasicChecking(req, resp)) {
      return;
    }
    String userConfirmation = req.getParameter("user_confirmation");
    if (userConfirmation == null) {
      redirectToConfirmationPage(req, resp);
      return;
    }
    process(resp, false);

  }
  private void process(HttpServletResponse resp, boolean showOnly) throws IOException {
    // everything ok, let's update
    StringBuilder summary = new StringBuilder();
    JsonObject contents = new JsonObject();
    JsonDataSources sources = new VendorDynamicInput().fetchAllDataSources();
    for (String entity: sources) {
      JsonArray array = new JsonArray();
      JsonDataSource source = sources.getSource(entity);
      for (JsonObject obj: source) {
        array.add(obj);
      }
      summary.append(entity).append(": ").append(source.size()).append("\n");
      contents.add(entity, array);
    }

    if (showOnly) {
      // Show generated contents to the output
      resp.setContentType("application/json");
      Writer writer = Channels.newWriter(Channels.newChannel(resp.getOutputStream()), "UTF-8");
      JsonWriter outputWriter = new JsonWriter(writer);
      outputWriter.setIndent("  ");
      new Gson().toJson(contents, outputWriter);
      outputWriter.flush();

    } else {
      // Write file to cloud storage
      CloudFileManager fileManager = new CloudFileManager();
      fileManager.createOrUpdate("__raw_session_data.json", contents, true);

      // send email
      Message message = new Message();
      message.setSender(Config.EMAIL_FROM);
      message.setSubject("[wic2-data-update] Manual sync from CMS");
      message.setTextBody(
          "Hey,\n\n"
          + "(this message is autogenerated)\n"
          + "This is a heads up that "+userService.getCurrentUser().getEmail()+" has just updated the IOSched 2015 data from the Vendor CMS.\n\n"
              + "Here is a brief status of what has been extracted from the Vendor API:\n"
              + summary
              + "\n\n"
              + "If you want to check the most current data that will soon be sync'ed to the IOSched Android app, "
              + "check this link: http://storage.googleapis.com/wic2-updater-dev.appspot.com/__raw_session_data.json\n"
              + "This data will remain unchanged until someone with proper privileges updates it again on https://wic2-updater-dev.appspot.com/cmsupdate\n\n"
              + "Thanks!\n\n"
              + "A robot on behalf of the IOSched team!\n\n"
              + "PS: you are receiving this either because you are an admin of the IOSched project or "
              + "because you are in a hard-coded list of I/O organizers. If you don't want to "
              + "receive it anymore, pay me a beer and ask kindly.");
      // TODO(arthurthompson): Reimplement mailing, it currently fails due to invalid sender.
      //MailServiceFactory.getMailService().sendToAdmins(message);

      resp.sendRedirect("/admin/schedule/updateok.html");
    }
  }

  private void redirectToConfirmationPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      req.getRequestDispatcher("/admin/schedule/cmsupdate.html").forward(req, resp);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean performBasicChecking(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!userService.isUserLoggedIn()) {
      resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
      return false;
    }
    if (!isValidUser()) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Sorry, your user has no permission. If you think you should have, you know who to contact to get it.");
      return false;
    }
    // Ignore cron when running in production.
    if ("true".equals(req.getParameter("cron")) && !Config.STAGING) {
      return false;
    }
    return true;
  }

  private boolean isValidUser() {
    return userService.isUserLoggedIn() &&
        ( userService.isUserAdmin() ||
            nonAdminUsers.contains(userService.getCurrentUser().getEmail()));
  }
}
