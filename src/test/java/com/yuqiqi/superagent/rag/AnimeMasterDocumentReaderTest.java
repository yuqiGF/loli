package com.yuqiqi.superagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnimeMasterDocumentReaderTest {

    @Resource
    private AnimeMasterDocumentReader animeMasterDocumentReader;

    @Test
    void loadMarkdowns() {
        animeMasterDocumentReader.loadMarkdowns();

    }
}