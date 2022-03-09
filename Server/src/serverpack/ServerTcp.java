package serverpack;

import clientpack.Chatter1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.nio.channels.SelectionKey.*;
import static java.nio.charset.StandardCharsets.UTF_8;


public class ServerTcp {
    public static void main(String[] args) {
        new ServerTcp().start();
    }

    final String ADR="localhost";
    final int PORT = 3030;
    private byte[] ba;

    final void start() {
        System.out.println("Chat created");
        try (Selector selector = Selector.open();
             ServerSocketChannel socket = ServerSocketChannel.open();
        ) {
            InetSocketAddress socketAddress = new InetSocketAddress(ADR, PORT);
            socket.bind(socketAddress);
            socket.configureBlocking(false);
            socket.register(selector, OP_ACCEPT);
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();//создаются ключевые точки
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel client = socket.accept();
                        client.configureBlocking(false);
                        System.out.println("Chatter connection: "
                                + client.getRemoteAddress());
                        client.register(selector, OP_READ);
                    }
                    else if(key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        client.read(buffer);
                        String line = new String(buffer.array()).trim();
                       // String chatterName = new Chatter1().getName();//null
                        SocketChannel clientName = (SocketChannel) key.channel();//todo
                        ByteBuffer bufferName = ByteBuffer.allocate(256);
                        clientName.read(bufferName);
                        String name = new String(buffer.array()).trim();
                        System.out.println("message from "  + name + "\n" + "> " + line + " " +
                                new SimpleDateFormat("HH:mm:ss, dd.MM.yyyy")
                                .format(Calendar.getInstance().getTime()));
                        ba = line.getBytes(UTF_8);
                        //
                        if (line.equalsIgnoreCase("/q")) {
                            client.register(selector, OP_ACCEPT);
                        } else {
                            client.register(selector, OP_WRITE);
                        }
                    }
                    else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.wrap(ba);
                        client.write(buffer);
                        client.register(selector, OP_READ);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
