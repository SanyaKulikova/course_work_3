package ru.kulikova.course_work_3;

import ru.kulikova.course_work_3.common.Connection;
import ru.kulikova.course_work_3.common.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final String ip;
    private final int port;

    private Message messageToSend; // пытаюсь так передать сообщения в потоки и из

    private Connection<Message> connection;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Message getMessageToSend() {
        return messageToSend;
    }

    public void setMessageToSend(Message messageToSend) {
        this.messageToSend = messageToSend;
    }

    public Connection<Message> getConnection() {
        return connection;
    }

    public void setConnection(Connection<Message> connection) {
        this.connection = connection;
    }

    public void run() {
        // при запуске клиентского приложения - создается соединение
        // не в () try, чтобы не обрывалось!
        try {
            Connection<Message> connection = new Connection<>(new Socket(ip, port));
            setConnection(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ThreadSender threadSender = new ThreadSender();
        threadSender.start();

        ThreadRecipient threadRecipient = new ThreadRecipient();
        threadRecipient.start();
    }



    public class ThreadSender extends Thread {

        @Override
        public void run() {

            Scanner scanner = new Scanner(System.in);

            System.out.println("Введите имя");
            String name = scanner.nextLine();

            while (true) {
                System.out.println("Введите сообщение");
                String text = scanner.nextLine();

//                if ("/exit".equalsIgnoreCase(text)) {
//                    System.out.println("Закрытие приложения");
//                    return;
//                }

                Message message = new Message(name, text);

                try {
                    Client.this.connection.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("Обработка Exception 1");
                }
            }

        }// TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально}
    }

    public class ThreadRecipient extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    Message fromServer = Client.this.connection.readMessage();
                    System.out.println("Новое сообщение: " + fromServer);
                } catch (IOException e) {
//                    System.out.println("Обработка Exception 2");
                    System.out.println("Сервер не доступен. Попробуйте позже.");
                    break;
                } catch (ClassNotFoundException e) {
                    System.out.println("Обработка Exception 3");
                }
            }
        }
    }
}
