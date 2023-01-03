package ru.kulikova.course_work_3;

public class ClientApplication {
    public static void main(String[] args) {
        new Client("127.0.0.1", 8090).run();
    }
}
