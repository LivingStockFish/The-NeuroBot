package com.genuinecoder.aiassistant.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class FeedbackItem {

    private final StringProperty user;
    private final StringProperty message;
    private final StringProperty date;

    private final IntegerProperty ratingUi;
    private final IntegerProperty ratingAccuracy;
    private final IntegerProperty ratingHelpfulness;

    public FeedbackItem(String user, String message, String date,
                        int ratingUi, int ratingAccuracy, int ratingHelpfulness) {

        this.user = new SimpleStringProperty(user);
        this.message = new SimpleStringProperty(message);
        this.date = new SimpleStringProperty(date);

        this.ratingUi = new SimpleIntegerProperty(ratingUi);
        this.ratingAccuracy = new SimpleIntegerProperty(ratingAccuracy);
        this.ratingHelpfulness = new SimpleIntegerProperty(ratingHelpfulness);
    }

    // --- Getters ---
    public String getUser() { return user.get(); }
    public String getMessage() { return message.get(); }
    public String getDate() { return date.get(); }

    public int getRatingUi() { return ratingUi.get(); }
    public int getRatingAccuracy() { return ratingAccuracy.get(); }
    public int getRatingHelpfulness() { return ratingHelpfulness.get(); }

    // --- Properties ---
    public StringProperty userProperty() { return user; }
    public StringProperty messageProperty() { return message; }
    public StringProperty dateProperty() { return date; }

    public IntegerProperty ratingUiProperty() { return ratingUi; }
    public IntegerProperty ratingAccuracyProperty() { return ratingAccuracy; }
    public IntegerProperty ratingHelpfulnessProperty() { return ratingHelpfulness; }
}
