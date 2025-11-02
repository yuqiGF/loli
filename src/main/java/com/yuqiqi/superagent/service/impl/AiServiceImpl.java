package com.yuqiqi.superagent.service.impl;

import com.yuqiqi.superagent.app.AnimeMaster;
import com.yuqiqi.superagent.service.AiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {
    @Resource
    private AnimeMaster animeMaster;
    @Override
    public String chat(String message) {
        //TODO 先指定为1 后面从浏览器获取
        String id = "1";
        return animeMaster.doChat(message, id);
    }
}
