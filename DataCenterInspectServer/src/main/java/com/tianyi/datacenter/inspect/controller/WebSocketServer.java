package com.tianyi.datacenter.inspect.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {

    //在相对路径中发布端点websocket，注解中的路径要与前端代码对应“ websocket = new WebSocket("ws://localhost:8080/项目名/websocket");”
    MyThread thread1 = new MyThread();
    //用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    private Session session = null;

    //开启连接
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        webSocketSet.add(this);
        System.out.println(webSocketSet);
        thread1.run();

    }

    //关闭连接
    @OnClose
    public void onClose() {
        thread1.stopMe();
        webSocketSet.remove(this);
    }

    //给服务器发送消息告知数据库发生变化
    @OnMessage
    public void onMessage(String time) {
        System.out.println("有一个新车下线,请及时检查");
        try {
            sendMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //出错的操作
    @OnError
    public void onError(Throwable error) {
        System.out.println(error);
        error.printStackTrace();
    }




    public void sendMessage() throws IOException {
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            item.session.getBasicRemote().sendText("有一个新车下线,请及时检查" + "\n" + "      ");
        }
    }
}


