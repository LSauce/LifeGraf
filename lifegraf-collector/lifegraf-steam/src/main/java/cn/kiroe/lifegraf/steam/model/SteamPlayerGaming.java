package cn.kiroe.lifegraf.steam.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("steam_player_gaming")
public class SteamPlayerGaming {

    @TableId
    private LocalDateTime time;
    private String steamId;
    private String gameId;
    private String gameName;
    private Long personaState;
}
