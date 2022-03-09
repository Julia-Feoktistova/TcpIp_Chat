package clientpack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Chatter1 {
    public static void main(String[] args) {
        new Chatter1().start();
    }

    final String ADR = "localhost";
    final int PORT = 3030;
    private String name;

    private String setName(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя");
        name = scanner.nextLine();
        return name;
    }

    public String getName() {
        return name;
    }

    final void start() {
        System.out.println(setName() + " вошел в чат");
        InetSocketAddress address = new InetSocketAddress(ADR, PORT);
        try (SocketChannel client = SocketChannel.open(address)) {
            Scanner sc = new Scanner(System.in);
            ByteBuffer buf = ByteBuffer.allocate(256);
            String line;
            while (true) {
                System.out.print(name + ": ");
                line = sc.nextLine();
                buf.clear();
                buf.put(line.getBytes());
                buf.flip();
                client.write(buf);
                if (line.equalsIgnoreCase("/q")) {
                    break;
                }
                buf.clear();
                client.read(buf);
                buf.flip();
                System.out.println("New message from " + name + ": \"" +
                        new String(buf.array(), buf.position(), buf.limit()) + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
