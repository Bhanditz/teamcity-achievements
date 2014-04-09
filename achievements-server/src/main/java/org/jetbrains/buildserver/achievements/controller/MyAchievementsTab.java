package org.jetbrains.buildserver.achievements.controller;

import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.buildserver.achievements.impl.Achievement;
import org.jetbrains.buildserver.achievements.impl.AchievementsConfig;
import org.jetbrains.buildserver.achievements.impl.AchievementsGrantor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAchievementsTab extends SimpleCustomTab {
  private static final String TAB_TITLE = "My Achievements";
  private final AchievementsGrantor myAchievementsGrantor;
  private final AchievementsConfig myAchievementsConfig;

  public MyAchievementsTab(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor pluginDescriptor, @NotNull AchievementsGrantor achievementsGrantor, @NotNull AchievementsConfig achievementsConfig) {
    super(pagePlaces);
    setPluginName(pluginDescriptor.getPluginName());
    setPlaceId(PlaceId.MY_TOOLS_TABS);
    setIncludeUrl(pluginDescriptor.getPluginResourcesPath("/myAchievements.jsp"));
    setTabTitle(TAB_TITLE);
    register();

    myAchievementsConfig = achievementsConfig;
    myAchievementsGrantor = achievementsGrantor;
  }

  @NotNull
  @Override
  public String getTabTitle(@NotNull HttpServletRequest request) {
    final SUser user = SessionUser.getUser(request);
    List<Achievement> granted = myAchievementsGrantor.getGrantedAchievements(user);
    return TAB_TITLE + (granted.isEmpty() ? "" : " (" + granted.size() + ")");
  }

  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
    super.fillModel(model, request);
    final SUser user = SessionUser.getUser(request);

    List<Achievement> granted = myAchievementsGrantor.getGrantedAchievements(user);

    List<Achievement> allAchievements = new ArrayList<Achievement>();
    if (TeamCityProperties.getBoolean("teamcity.development.mode")) {
      allAchievements.addAll(myAchievementsConfig.getAchievements());
      allAchievements.removeAll(granted);
    }

    model.put("grantedAchievements", granted);
    model.put("availableAchievements", allAchievements);
  }
}
