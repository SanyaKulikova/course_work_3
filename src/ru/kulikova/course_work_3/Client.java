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
        ThreadSender threadSender = new ThreadSender();

        ThreadRecipient threadRecipient = new ThreadRecipient(); // а если клиент подключился только почитать?
        // будет работать?

        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите имя");
        String name = scanner.nextLine();

        while (true) {
            System.out.println("Введите сообщение");
            String text = scanner.nextLine();

            if ("/exit".equalsIgnoreCase(text)) {
                System.out.println("Закрытие приложения");
                return;
            }

            setMessageToSend(new Message(name, text));

            try (Connection<Message> connection = new Connection<>(new Socket(ip, port))) {

                setConnection(connection);
                threadSender.start();
                threadRecipient.start();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Обработка IOException и ClassNotFoundException 4");
            } catch (Exception e) {
                System.out.println("Обработка Exception 5");
            }
            // TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально

        }
    }
    public class ThreadSender extends Thread{

        @Override
        public void run() {
            try {
                Client.this.connection.sendMessage(Client.this.messageToSend);
            } catch (IOException e) {
                System.out.println("Обработка Exception 1");
            }

            // TODO: нужно изучить что будет когла вырубается сервер или клиент и обрабоать исключения нормально
        }
    }

    public class ThreadRecipient extends Thread{
        @Override
        public void run() {
            try {
                Message fromServer = Client.this.connection.readMessage();
                System.out.println("Сообщение от сервера: " + fromServer);
            } catch (IOException e) {
                System.out.println("Обработка Exception 2");
            } catch (ClassNotFoundException e) {
                System.out.println("Обработка Exception 3");
            }
        }
    }
}
