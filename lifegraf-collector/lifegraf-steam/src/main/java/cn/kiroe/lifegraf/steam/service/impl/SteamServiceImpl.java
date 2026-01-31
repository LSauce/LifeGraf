package cn.kiroe.lifegraf.steam.service.impl;

import cn.kiroe.lifegraf.steam.config.SteamProperties;
import cn.kiroe.lifegraf.steam.converter.SteamConverter;
import cn.kiroe.lifegraf.steam.mapper.SteamPlayerGamingMapper;
import cn.kiroe.lifegraf.steam.mapper.SteamPlayerMapper;
import cn.kiroe.lifegraf.steam.model.SteamPlayer;
import cn.kiroe.lifegraf.steam.model.SteamPlayerGaming;
import cn.kiroe.lifegraf.steam.service.SteamService;
import com.lukaspradel.steamapi.data.json.friendslist.Friend;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetFriendListRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SteamServiceImpl implements SteamService {
    private final SteamPlayerMapper steamPlayerMapper;
    private final SteamPlayerGamingMapper steamPlayerGamingMapper;
    private final SteamWebApiClient steamWebApiClient;
    private final SteamConverter converter;
    private final SteamProperties steamProperties;

    public SteamServiceImpl(SteamPlayerMapper steamPlayerMapper, SteamPlayerGamingMapper steamPlayerGamingMapper, SteamWebApiClient steamWebApiClient, SteamConverter steamConverter, SteamProperties steamProperties) {
        this.steamPlayerMapper = steamPlayerMapper;
        this.steamPlayerGamingMapper = steamPlayerGamingMapper;
        this.steamWebApiClient = steamWebApiClient;
        this.converter = steamConverter;
        this.steamProperties = steamProperties;
    }

    @SneakyThrows
    @Override
    public void saveNowGaming(List<String> steamIds) {
        //  获取玩家状态
        GetPlayerSummariesRequest summariesRequest = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(steamIds);
        GetPlayerSummaries getPlayerSummaries = steamWebApiClient.<GetPlayerSummaries>processRequest(summariesRequest);
        List<Player> playerList = getPlayerSummaries.getResponse().getPlayers();
        List<SteamPlayer> steamPlayers = playerList.stream().map(player -> {
            SteamPlayer steamPlayer = converter.dto2po(player);
            Object gameId = player.getAdditionalProperties().get("gameid");
            Object gameName = player.getAdditionalProperties().get("gameextrainfo");
            steamPlayer.setGameId(gameId == null ? "" : String.valueOf(gameId));
            steamPlayer.setGameName(gameName == null ? "" : String.valueOf(gameName));
            return steamPlayer;
        }).toList();
        // 保存玩家基本信息
        steamPlayerMapper.insertOrUpdate(steamPlayers);

        LocalDateTime now = LocalDateTime.now();
        List<SteamPlayerGaming> gamingList = playerList.stream().map(player -> {
            SteamPlayerGaming steamPlayerGaming = new SteamPlayerGaming();
            steamPlayerGaming.setSteamId(player.getSteamid());
            steamPlayerGaming.setTime(now);
            Map<String, Object> objectMap = player.getAdditionalProperties();
            Object gameId = objectMap.get("gameid");
            Object gameName = objectMap.get("gameextrainfo");
            if(gameId != null) {
                steamPlayerGaming.setGameId(String.valueOf(gameId));
            }
            if(gameName != null) {
                steamPlayerGaming.setGameName(String.valueOf(gameName));
            }
             steamPlayerGaming.setPersonaState(player.getPersonastate());
            return steamPlayerGaming;
        }).toList();
        // 保存游戏记录
        steamPlayerGamingMapper.insert(gamingList);
    }

    @SneakyThrows
    @Override
    public List<Friend> getFriends(String steamId) {
        GetFriendListRequest getFriendListRequest = SteamWebApiRequestFactory.createGetFriendListRequest(steamId);
        GetFriendList getFriendList = steamWebApiClient.<GetFriendList>processRequest(getFriendListRequest);
        return getFriendList.getFriendslist().getFriends();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNowGaming() {
        log.info("开始保存 Steam 玩家正在游戏信息");
        String mySteamId = steamProperties.getMySteamId();
        List<String> steamIdList = new ArrayList<>(getFriends(mySteamId).stream().map(Friend::getSteamid).toList());
        steamIdList.add(mySteamId);
        saveNowGaming(steamIdList);
        log.info("保存 Steam 玩家正在游戏信息完成");
    }
}
