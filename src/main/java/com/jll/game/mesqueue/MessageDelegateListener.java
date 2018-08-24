package com.jll.game.mesqueue;

import java.io.Serializable;

public interface MessageDelegateListener {

    void handleMessage(Serializable message);
}