package ru.kulikova.course_work_3;

import ru.kulikova.course_work_3.common.Connection;
import ru.kulikova.course_work_3.common.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int port;

    private CopyOnWriteArrayList<Connection> connections;

    private ArrayBlockingQueue<Message> messages;



    public Server(int port, CopyOnWriteArrayList<Connection> connections, ArrayBlockingQueue<Message> messages){
        this.port = port;
        this.connections = connections;
        this.messages = messages;
    }
    // TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально


//    public Connection<Message> getConnection() {
//        return connection;
//    }
//
//    public void setConnection(Connection<Message> connection) {
//        this.connection = connection;
//    }

    public void run(){

        ThreadSenderSer threadSender = new ThreadSenderSer();
        threadSender.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен...");

            while (true){
                Socket socket = serverSocket.accept();
                Connection<Message> connection = new Connection<>(socket);
                connections.add(connection);
                System.out.println(connections);

                ThreadRecipientSer threadRecipient = new ThreadRecipientSer(connection);
                threadRecipient.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class ThreadSenderSer extends Thread{

        @Override
        public void run() {
            while (true) {
                Message message = null;
                try {
                    message = Server.this.messages.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (Connection<Message> connection : Server.this.connections) {
                    if(!message.getConnection().equals(connection)){
                        try {
                            connection.sendMessage(message);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


                }
            }
        }
    }

    public class ThreadRecipientSer extends Thread {
        private Connection<Message> connection;

        public ThreadRecipientSer(Connection<Message> connection) {
            this.connection = connection;
        }



        @Override
        public void run() {

            while(true){
                try {
                    Message fromClient = this.connection.readMessage();
                    fromClient.setConnection(this.connection);
                    Server.this.messages.put(fromClient);
                    System.out.println(Server.this.messages);
                } catch (IOException e) {
//                    System.out.println("Обработка Exception 7");
                    Server.this.connections.remove(this.connection);
//                    System.out.println(Server.this.connections);
                    return; // добавить finally?? нужно завершить связь и удалить коннектион
                } catch (ClassNotFoundException e) {
                    System.out.println("Обработка Exception 8");
                } catch (InterruptedException e) {
                    System.out.println("Обработка Exception 9");
                }
            }


        }
    }

}
