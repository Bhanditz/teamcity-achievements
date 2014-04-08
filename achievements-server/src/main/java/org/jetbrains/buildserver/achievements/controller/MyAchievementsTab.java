package org.jetbrains.buildserver.achievements.controller;

import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.buildserver.achievements.impl.Achievement;
import org.jetbrains.buildserver.achievements.impl.AchievementsGrantor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAchievementsTab extends SimpleCustomTab {
  private final AchievementsGrantor myAchievementsGrantor;

  public MyAchievementsTab(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor pluginDescriptor, @NotNull AchievementsGrantor achievementsGrantor) {
    super(pagePlaces);
    setPluginName(pluginDescriptor.getPluginName());
    setPlaceId(PlaceId.MY_TOOLS_TABS);
    setIncludeUrl(pluginDescriptor.getPluginResourcesPath("/myAchievements.jsp"));
    register();

    myAchievementsGrantor = achievementsGrantor;
  }

  @NotNull
  @Override
  public String getTabTitle(@NotNull HttpServletRequest request) {
    final SUser user = SessionUser.getUser(request);
    List<Achievement> granted = myAchievementsGrantor.getGrantedAchievements(user);
    return granted.isEmpty() ? "My Achievements" : "My Achievements (" + granted.size() + ")";
  }

  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    super.fillModel(model, request);
    final SUser user = SessionUser.getUser(request);

    List<Achievement> granted = myAchievementsGrantor.getGrantedAchievements(user);
    List<AchievementBean> beans = new ArrayList<AchievementBean>();
    for (Achievement a: granted) {
      beans.add(new AchievementBean(a));
    }
    model.put("achievements", beans);
  }
}