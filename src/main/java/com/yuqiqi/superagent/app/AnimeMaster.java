package com.yuqiqi.superagent.app;

import com.yuqiqi.superagent.advisor.MyLoggerAdvisor;
import com.yuqiqi.superagent.advisor.PermissionAdvisor;
import com.yuqiqi.superagent.advisor.ReReadingAdvisor;

import com.yuqiqi.superagent.chatMemorty.FileBasedChatMemory;
import com.yuqiqi.superagent.rag.AnimeMasterCloudAdvisorConfig;
import com.yuqiqi.superagent.rag.AnimeMasterRagCustomAdvisorFactory;
import com.yuqiqi.superagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class AnimeMaster {

    //要调用ai，首先初始化一个ai客户端 手动创建
    private final ChatClient chatClient;

    //提示词prompt
    private final String SYSTEM_PROMPT = "你是一个热爱动漫的普通观众，说话直接、接地气，善于用生活化的比喻和网络流行语来评论动漫。\n";

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

    @Resource  //注入自己写的向量数据库
    private VectorStore animeMasterVectorStore;

    //使用云端的知识库需要引入自定义的advisor
    @Resource
    private Advisor animeMasterAdvisor;

    /**
     * 和RAG知识库对话
     * @param message
     * @param id
     * @return
     */
    public String doChatWithRAG(String message, String id){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id) //官方文档中的对话隔离写法
                        .param(ChatMemory.DEFAULT_CONVERSATION_ID, 10))
                //开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                //应用RAG（TMD有坑，得导advisor包！！！⭐）
//                .advisors(new QuestionAnswerAdvisor(animeMasterVectorStore))
                //应用RAG检索增强服务（基于云知识库服务）
                .advisors(animeMasterAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    @Resource
    private QueryRewriter queryRewriter;
    public String doChatWithQueryRewriter(String message,String chatId){
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        String content = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return content;
    }

    /**
     * 测试自定义检索增强顾问
     */
    public String doChatWithMyRagAdvisor(String message,String chatId){
        String result = chatClient.prompt()
                .user(message)
                .advisors(AnimeMasterRagCustomAdvisorFactory.createAnimeMasterRagCustomAdvisor(animeMasterVectorStore, "吃饭"))
                .call()
                .content();
        return result;
    }

    /**
     * ⭐流式对话方法
     * @param message 用户消息
     * @param id 用户ID
     * @return Flux<String> 流式字符流
     */
    public Flux<String> doStreamChat(String message, String id) {
        return chatClient
                .prompt()
                .user(message)
                // 保持和 doChat 一样的 Advisor 配置（记忆、参数等）
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, id)
                        .param(ChatMemory.DEFAULT_CONVERSATION_ID, 10))
                .stream() // ⭐这里使用 stream() 而不是 call()
                .content(); // ⭐直接获取内容流 (Flux<String>)
    }
}
