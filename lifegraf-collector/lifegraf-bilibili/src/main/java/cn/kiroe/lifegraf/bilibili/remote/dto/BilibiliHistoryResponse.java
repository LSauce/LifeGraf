package cn.kiroe.lifegraf.bilibili.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BilibiliHistoryResponse {

    private Integer code;
    private String message;
    private Integer ttl;
    private DataInfo data;

    @Data
    public static class DataInfo {
        private CursorInfo cursor;
        private List<TabInfo> tab;
        private List<ListInfo> list;
    }

    @Data
    public static class CursorInfo {
        private Long max;
        @JsonProperty("view_at")
        private Long viewAt;
        private String business;
        private Integer ps;
    }

    @Data
    public static class TabInfo {
        private String type;
        private String name;
    }

    @Data
    public static class ListInfo {
        private String title;
        @JsonProperty("long_title")
        private String longTitle;
        private String cover;
        private List<String> covers; // 可能为null
        private String uri;
        private HistoryInfo history;
        private Integer videos;
        @JsonProperty("author_name")
        private String authorName;
        @JsonProperty("author_face")
        private String authorFace;
        @JsonProperty("author_mid")
        private Long authorMid;
        @JsonProperty("view_at")
        private Long viewAt;
        private Integer progress;
        private String badge;
        @JsonProperty("show_title")
        private String showTitle;
        private Integer duration;
        private String current;
        private Integer total;
        @JsonProperty("new_desc")
        private String newDesc;
        @JsonProperty("is_finish")
        private Integer isFinish;
        @JsonProperty("is_fav")
        private Integer isFav;
        private Long kid;
        @JsonProperty("tag_name")
        private String tagName;
        @JsonProperty("live_status")
        private Integer liveStatus;
    }

    @Data
    public static class HistoryInfo {
        private Long oid;
        private Long epid;
        private String bvid;
        private Integer page;
        private Long cid;
        private String part;
        private String business;
        private Integer dt;
    }
}
