package com.yuqiqi.superagent.app;

import com.yuqiqi.superagent.advisor.MyLoggerAdvisor;
import com.yuqiqi.superagent.advisor.PermissionAdvisor;
import com.yuqiqi.superagent.advisor.ReReadingAdvisor;
import com.yuqiqi.superagent.chatMemorty.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AnimeMaster {

    //要调用ai，首先初始化一个ai客户端 手动创建
    private final ChatClient chatClient;

    //提示词prompt
    private final String SYSTEM_PROMPT = "你是一个热爱动漫的普通观众，说话直接、接地气，善于用生活化的比喻和网络流行语来评论动漫。\n" +
            "【语言特色】\n" +
            "- 使用\"这波操作\"、\"离谱\"、\"蚌埠住了\"等网络用语\n" +
            "- 用生活中的例子做类比，通俗易懂\n" +
            "- 情绪表达直接，喜怒形于色\n" +
            "- 善于发现作品的沙雕和鬼畜潜力\n" +
            "【评论角度】\n" +
            "\"这主角光环亮得能当手电筒用了\"\n" +
            "\"反派的话痨程度，让我想起了我唠叨的母上大人\"\n" +
            "\"这番的经费是不是都花在ED上了？\"\n" +
            "【核心原则】\n" +
            "- 说人话，不说专业术语\n" +
            "- 站在普通观众的角度思考\n" +
            "- 幽默吐槽但不恶意攻击\n" +
            "- 真心热爱动漫这个媒介\n" +
            "用最真实的声音，说出广大动漫迷的心里话！";

    /**
     * 初始化
     * @param dashScopeChatModel 注入大模型
     */
    //构造器注入  注入大模型  手动构造客户端
    public AnimeMaster(ChatModel dashScopeChatModel) {
        //初始化 窗口存储
//        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().build();

        //初始化 基于内存的会话记忆 有问题⭐
//        ChatMemory chatMemory = new ChatMemory();

        //初始化 基于文件的会话记忆
        String fileDir = System.getProperty("user.dir")+"/tmp/chat-memory";
        FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        //⭐创建对话客户端
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT) //系统提示词
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
//                        new SimpleLoggerAdvisor(),
                        new PermissionAdvisor(), //⭐自定义敏感词拦截器
                        new MyLoggerAdvisor(),  //⭐自定义默认的日志拦截级别
                        new ReReadingAdvisor()  //⭐自定义的重读拦截器
                ) //默认拦截器，对所有请求生效
                .build();
//        //单次调用
//        chatClient.prompt()
//                .system()
//                .advisors()
    }

    //创建方法调用

    /**
     * ai对话  基础对话  支持多轮多人ai记忆
     * @param message 用户提示词
     * @param id 用户id
     * @return 返回结果
     */
    public String doChat(String message, String id) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id) //官方文档中的对话隔离写法
                        .param(ChatMemory.DEFAULT_CONVERSATION_ID, 10))   //默认读取的记录数
                .call()   //调用
                .chatResponse();//获取回复
        String content = chatResponse.getResult().getOutput().getText(); //获取结果
        //⭐也可以直接在获取回复处.content获取结果，但是获取response这样写便于记录token
        log.info("content:{}",content);
        return content;
    }

    //⭐java21的特性record 快速定义变量 （可以理解为快速定义类）
    record AnimeReport(String title, List<String> suggestion){

    }

    /**
     * 返回动漫报告   （实践结构化输出）
     * @param message 消息
     * @param id 对话id
     * @return 结果
     */
    public AnimeReport doChatWithReport(String message, String id) {
        AnimeReport animeReport = chatClient
                .prompt()
                .user(message)  //用户提示词
                .system(SYSTEM_PROMPT + "每次对话完成之后都要生成一个补番推荐列表，标题为“补番建议”，内容为动漫全名列表")  //修改一下系统信息
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id) //官方文档中的对话隔离写法
                        .param(ChatMemory.DEFAULT_CONVERSATION_ID, 10))   //默认读取的记录数
                .call()   //调用
                .entity(AnimeReport.class);  //⭐指定结构化输出
        log.info("AnimeReport:{}",animeReport);

        return animeReport;
    }
}
