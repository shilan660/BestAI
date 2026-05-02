package com.best.agent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileBasedChatMemory implements ChatMemory {
//    存储地址
    private final String BASE_DIR;

//    引入kryo
    private static final Kryo kryo = new Kryo();

//    初始化kryo
    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

//    构造对象时,指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    /**
     * 添加单条消息
     * @param conversationId
     * @param message
     */
    @Override
    public void add(String conversationId, Message message) {
        List<Message> messagesList = getOrCreateMessages(conversationId);
        messagesList.add(message);
        saveMessages(conversationId, messagesList);
    }

    /**
     * 添加多条消息
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messagesList = getOrCreateMessages(conversationId);
        messagesList.addAll(messages);
        saveMessages(conversationId, messagesList);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messageList = getOrCreateMessages(conversationId);
//        取最近3条消息
        return messageList.stream()
                .skip(Math.max(0 , messageList.size() - 3))
                .toList();
    }

    /**
     * 清空会话消息
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if  (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取或创建会话消息
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateMessages(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()){
            try(Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input , ArrayList.class);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话消息
     * @param conversationId
     * @param messages
     */
    private void saveMessages(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try(Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 每个会话单独保存
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
