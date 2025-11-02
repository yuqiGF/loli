package com.yuqiqi.superagent.service;

import com.yuqiqi.superagent.app.AnimeMaster;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


public interface AiService {
    String chat(String message);
}
