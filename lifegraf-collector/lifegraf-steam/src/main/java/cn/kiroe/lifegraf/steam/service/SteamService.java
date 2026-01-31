package cn.kiroe.lifegraf.steam.service;

import cn.kiroe.lifegraf.steam.model.SteamPlayer;
import com.lukaspradel.steamapi.data.json.friendslist.Friend;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SteamService {

    void saveNowGaming(List<String> steamIds);

    List<Friend> getFriends(String steamId);

    void saveNowGaming();
}
