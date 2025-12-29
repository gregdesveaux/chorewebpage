package com.chorewebpage.controller.dto;

import com.chorewebpage.model.Kid;
import jakarta.validation.constraints.NotNull;

public class MarkDoneRequest {

    @NotNull
    private Kid completedBy;

    public Kid getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Kid completedBy) {
        this.completedBy = completedBy;
    }
}
