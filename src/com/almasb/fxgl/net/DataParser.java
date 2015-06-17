package com.almasb.fxgl.net;

import java.io.Serializable;

public interface DataParser<T extends Serializable> {
    public void parse(T data);
}
