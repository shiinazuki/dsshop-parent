package com.iori.service;

import java.util.Map;

public interface PayService {
    Map<String, String> create(String money, String orderId);

    Map<String, String> query(String orderId);
}
