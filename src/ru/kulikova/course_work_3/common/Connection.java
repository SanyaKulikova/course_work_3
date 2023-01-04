package ru.kulikova.course_work_3.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Connection <T extends Message> implements AutoCloseable{
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;




    public Connection(Socket socket) throws IOException { // передается только Socket, так как все остальное получаем из него
        this.socket = socket;
        output = new ObjectOutputStream(this.socket.getOutputStream()); // output будет представлять наш объект как массив байтов
        // и передавать аутпутстриму, который у него в конструкторе (вызвали его к сокета) - и там уже пойдут данные ИЗ программы
        // (от клиенту на сервер или от сервера к клиенту)
        input = new ObjectInputStream(this.socket.getInputStream()); // аналогично
        // если мы обработаем исключения здесь, то лешим клиента и сервер возможности обработать их своими (разными) способами
        // поэтому throws IOException
    }


    // TODO: пока точно не понимаю - навеное нужно преписать на многопоточность
    public void sendMessage(T message) throws IOException{ // T - чтобы вспомнить generic
        // используется и клиентом и сервером
        // можем вызывать методы : все что есть у message и его родителя
        message.setDateTime();
        output.writeObject(message);
        output.flush(); // нужно выталкивать файлы)
        // опять же либо обрабатываем здесь - либо подбрасываем
    }

    public T readMessage() throws IOException, ClassNotFoundException{
        return (T) input.readObject();
        //  можно обработать одно исключение, а дургое подбросить
    }


    @Override
    public void close() throws Exception {
        // нужно закрыть все, что было открыто
        input.close();
        output.close();
        socket.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection<?> that = (Connection<?>) o;

        if (!Objects.equals(socket, that.socket)) return false;
        if (!Objects.equals(input, that.input)) return false;
        return Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        int result = socket != null ? socket.hashCode() : 0;
        result = 31 * result + (input != null ? input.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        return result;
    }
}

