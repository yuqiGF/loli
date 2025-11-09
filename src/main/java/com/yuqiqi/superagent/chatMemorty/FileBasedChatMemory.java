package com.yuqiqi.superagent.chatMemorty;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件的会话记忆  实现ChatMemory接口
 * 实现其增删改查即可
 */
public class FileBasedChatMemory implements ChatMemory {

    public final String BASE_DIR;

    //定义一个kryo对象
    public static final Kryo kryo = new Kryo();

    //代码块，调用类的时候优先执行
    static {
        kryo.setRegistrationRequired(false); //将需要手动注册给关掉
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    //构造函数  让用户在使用的时候自己指定会话文件的存储目录
    public FileBasedChatMemory(String dir){
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if(!baseDir.exists()){
            baseDir.mkdirs();  //如果目录不存在的话就自己创建一个
        }
    }

//    /**
//     * 保存单条消息
//     * @param conversationId
//     * @param message
//     */
//    @Override
//    public void add(String conversationId, Message message) {
//        // 直接调用列表版本的add方法，避免覆盖历史消息
//        add(conversationId, List.of(message));
//    }

    /**
     * 保存多条消息
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        //获取以往的消息
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages); //新增
        saveConversation(conversationId,messageList); //保存
    }

    /**
     * 获取会话记忆
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> get(String conversationId) {
        return getOrCreateConversation(conversationId);
    }

    /**
     * 清除会话记忆
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 获取或创建会话消息的列表
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId){
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if(file.exists()){
            try(Input input = new Input(new FileInputStream(file))){
                messages = kryo.readObject(input,ArrayList.class); //调用readObject的方法将信息转化为特定格式的消息
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话记忆
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId,List<Message> messages){
        File file = getConversationFile(conversationId);
        try(Output output = new Output(new FileOutputStream(file))){
            kryo.writeObject(output,messages);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 每个会话记忆单独保存，要获取到当前会话的id
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId){
        return new File(BASE_DIR,conversationId+".kryo");
    }
}
