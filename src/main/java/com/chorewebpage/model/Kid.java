package com.chorewebpage.model;

public enum Kid {
    ONE,
    TWO;

    public Kid otherKid() {
        return this == ONE ? TWO : ONE;
    }
}
