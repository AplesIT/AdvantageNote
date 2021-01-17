package com.production.advangenote.helpers.notifications;

/**
 * @author vietnh
 * @name NotificationChannel
 * @date 10/1/20
 **/
public class NotificationChannel {

    int importance;
    String name;
    String description;
    String id;

    NotificationChannel(int importance, String name, String description, String id) {
        this.importance = importance;
        this.name = name;
        this.description = description;
        this.id = id;
    }
}
