package ru.kulikova.course_work_3;

import ru.kulikova.course_work_3.common.Connection;
import ru.kulikova.course_work_3.common.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int port;

    private CopyOnWriteArrayList<Connection> connections;

    private ArrayBlockingQueue<Message> messages;

    private ServerSocket serverSocket;

    public Server(int port, CopyOnWriteArrayList<Connection> connections, ArrayBlockingQueue<Message> messages){
        this.port = port;
        this.connections = connections;
        this.messages = messages;
    }
    // TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально


    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run(){ // чтобы сервер начал слушать клиентов - нужен ServerSocket
        ThreadSenderSer threadSender = new ThreadSenderSer();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            setServerSocket(serverSocket);
            System.out.println("Сервер запущен...");
            while (true){
                ThreadRecipientSer threadRecipient = new ThreadRecipientSer();
                threadRecipient.start();

                threadSender.start();

                // todo по идее должен быть только один поток
                // значит - или мы его создаем вне цикла или
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public class ThreadSenderSer extends Thread{

        @Override
        public void run() {
            for (Connection<Message> connection : Server.this.connections) {
                // проверка на вшивость?
                try {
                    connection.sendMessage(Server.this.messages.take());
                } catch (IOException e) {
                    System.out.println("Обработка Exception 10");
                } catch (InterruptedException e) {
                    System.out.println("Обработка Exception 11");
                }
            }

            // TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально
        }
    }

    public class ThreadRecipientSer extends Thread {
        @Override
        public void run() {
            try {
                Socket socket = Server.this.serverSocket.accept();

                Connection<Message> connection = new Connection<>(socket);
                Server.this.connections.add(connection);
                System.out.println(Server.this.connections);
                Message fromClient = connection.readMessage();
                Server.this.messages.put(fromClient);
                System.out.println(Server.this.messages);
            } catch (IOException e) {
                System.out.println("Обработка Exception 7");
            } catch (ClassNotFoundException e) {
                System.out.println("Обработка Exception 8");
                System.out.println("это вроде связан с рид и райт у коннектион");
            } catch (InterruptedException e) {
                System.out.println("Обработка Exception 9");
            }


        }
    }
// сначала запускаем сервер
// видим- Сервер запущен...
// дальше сервер уходит в бесконечный цикл и ждет клиента
// идем в клиентское приложение

// сервер дождался сообщения, вывел его, сформировал свое и отправил его клиенту
// опять ждет в бесконечном цикле

}
