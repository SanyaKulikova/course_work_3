package ru.kulikova.course_work_3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerApplication {
    public static void main(String[] args) {

        new Server(8090, new CopyOnWriteArrayList<>(), new ArrayBlockingQueue<>(15)).run();
    }
}
