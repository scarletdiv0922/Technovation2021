package com.example.technovation2021;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    public String eventId;
    // Event start date, start time.
    public Date startDate;
    // Event end date, end time
    public Date endDate;
    public String eventDesc;
}
