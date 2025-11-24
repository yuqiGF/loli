package com.yuqiqi.superagent.rag;


import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * ⭐创建上下文查询增强器的工厂  当检索到的文档信息为空的时候的解决方式    检索不到文档的时候用的
  */
public class AnimeMasterContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                请严格输出以下内容：
                抱歉，我暂时无法回答您的问题，请联系管理员宇崎崎解决
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)  //不允许空上下文，  为true的时候会按照原本的ai能力来查询
                .emptyContextPromptTemplate(emptyContextPromptTemplate)  //空上下文时的提示词
                .build();
    }
}
