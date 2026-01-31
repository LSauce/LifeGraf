package cn.kiroe.lifegraf.bilibili.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("bilibili_history")
public class BilibiliHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("long_title")
    private String longTitle;

    @TableField("cover")
    private String cover;

    @TableField("uri")
    private String uri;

    @TableField("oid")
    private Long oid;

    @TableField("epid")
    private Long epid;

    @TableField("bvid")
    private String bvid;

    @TableField("page")
    private Integer page;

    @TableField("cid")
    private Long cid;

    @TableField("part")
    private String part;

    @TableField("business")
    private String business;

    @TableField("dt")
    private Integer dt;

    @TableField("videos")
    private Integer videos;

    @TableField("author_name")
    private String authorName;

    @TableField("author_face")
    private String authorFace;

    @TableField("author_mid")
    private Long authorMid;

    @TableField("view_at")
    private Instant viewAt;

    @TableField("progress")
    private Integer progress;

    @TableField("badge")
    private String badge;

    @TableField("show_title")
    private String showTitle;

    @TableField("duration")
    private Integer duration;

    @TableField("current_val")
    @JsonProperty("current")
    private String currentVal;

    @TableField("total")
    private Integer total;

    @TableField("new_desc")
    private String newDesc;

    @TableField("is_finish")
    private Integer isFinish;

    @TableField("is_fav")
    private Integer isFav;

    @TableField("kid")
    private Long kid;

    @TableField("tag_name")
    private String tagName;

    @TableField("live_status")
    private Integer liveStatus;

    @TableField("original_json")
    private String originalJson;

    @TableField("create_time")
    private Instant createTime;

    @TableField("update_time")
    private Instant updateTime;
}
