package cn.kiroe.lifegraf.steam.converter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.kiroe.lifegraf.steam.model.SteamPlayer;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SteamConverter {
     @Mapping(source = "steamid", target = "steamId")
     @Mapping(source = "avatarfull", target = "avatarFull")
     @Mapping(source = "avatarmedium", target = "avatarMedium")
     @Mapping(source = "communityvisibilitystate", target = "communityVisibilityState")
     @Mapping(source = "lastlogoff", target = "lastLogoff")
     @Mapping(source = "loccityid", target = "locCityId")
     @Mapping(source = "loccountrycode", target = "locCountryCode")
     @Mapping(source = "locstatecode", target = "locStateCode")
     @Mapping(source = "personaname", target = "personaName")
     @Mapping(source = "personastate", target = "personaState")
     @Mapping(source = "personastateflags", target = "personaStateFlags")
     @Mapping(source = "primaryclanid", target = "primaryClanId")
     @Mapping(source = "profilestate", target = "profilesState")
     @Mapping(source = "profileurl", target = "profileUrl")
     @Mapping(source = "realname", target = "realName")
     @Mapping(source = "timecreated", target = "timeCreated")
     SteamPlayer dto2po(Player player);
    List<SteamPlayer> dtos2pos(List<Player> players);

    default LocalDateTime map(Long value) {
        if (value == null) {
            return LocalDateTime.now();
        }
        return LocalDateTimeUtil.ofUTC(value);
    }
}
