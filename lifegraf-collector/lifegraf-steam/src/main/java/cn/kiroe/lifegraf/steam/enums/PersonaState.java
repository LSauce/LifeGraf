package cn.kiroe.lifegraf.steam.enums;

import lombok.Getter;

@Getter
public enum PersonaState {

    OFFLINE(0, "Offline"),
    ONLINE(1, "Online"),
    BUSY(2, "Busy"),
    AWAY(3, "Away"),
    SNOOZE(4, "Snooze"),
    LOOKING_TO_TRADE(5, "Looking to Trade"),
    LOOKING_TO_PLAY(6, "Looking to Play");

    private final int value;
    private final String desc;
    PersonaState(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
