package cn.kiroe.lifegraf.steam.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("steam_player")
public class SteamPlayer {

    @TableId(type = IdType.NONE)
    private String steamId;
    private String avatar;

    private String avatarFull;

    private String avatarMedium;
    private Long communityVisibilityState;
    private Long lastLogoff;
    private Long locCityId;
    private String locCountryCode;
    private String locStateCode;
    private String personaName;
    private Long personaState;
    private Long personaStateFlags;
    private String primaryClanId;
    private Long profilesState;
    private String profileUrl;
    private String realName;
    private LocalDateTime timeCreated;
    private String gameId;
    private String gameName;

}
