package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {

    // IMPORTANT: The string inside @SerializedName("...") must match the
    // exact key name returned by your API (e.g., "data", "notifications", "result").
    // Since you didn't provide the JSON, I'm assuming the key is "data" or "notifications".
    @SerializedName("data")
    private List<Notification> notifications;

    // This is the method your Fragment is trying to call
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
