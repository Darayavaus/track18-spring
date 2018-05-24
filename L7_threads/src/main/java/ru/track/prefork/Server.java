package ru.track.prefork;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;

import javax.swing.tree.ExpandVetoException;


/**
 * - multithreaded +
 * - atomic counter +
 * - setName() +
 * - thread -> Worker +
 * - save threads
 * - broadcast (fail-safe)
 */



public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    public Server(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        AdminConsole admin = new AdminConsole();
        admin.start();
        int count=0;
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();
            count++;
            Thread t = new ServeClient(socket, count);
            System.out.println("Starting thread "+ t.getName());
            t.start();
        }
    }

    public class ServeClient extends Thread{
        private Socket socket;
        int ClientId;
        int port;
        String address;
        InputStream inputStream;
        OutputStream out;
        ObjectOutputStream oos;
        ObjectInputStream ois;

        public ServeClient(Socket socket, int count) {
            this.socket = socket;
            this.ClientId = count;
            this.port = this.socket.getPort();
            this.address = this.socket.getLocalAddress().toString();
            this.setName(String.format("Client[%d]@%s:%d", this.ClientId, this.address, this.port));
            try {
                inputStream = this.socket.getInputStream();
                out = this.socket.getOutputStream();
                oos = new ObjectOutputStream(out);
                ois = new ObjectInputStream(inputStream);
            } catch (IOException e1) {
                System.out.println("Smth wrong with Input/Output e0");
            }

        }

        @Override
        public void run(){
            try {
                while (!isInterrupted()) {
                    Message msg = (Message) ois.readObject();
                    String line = msg.get_data();
                    System.out.println(line);
                    if (line.equals("q")) {
                        throw new InterruptedException();
                    }
                    msg.data = String.format("Client@%s:%d>", this.address, this.port) + msg.get_data();
                    ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                    int noThreads = currentGroup.activeCount();
                    Thread[] lstThreads = new Thread[noThreads];
                    currentGroup.enumerate(lstThreads);
                    for (int i = 0; i < noThreads; i++) {
                        Thread tmpthread = lstThreads[i];
                        if (tmpthread instanceof ServeClient && ((ServeClient) tmpthread).ClientId != this.ClientId) {
                            ((ServeClient) tmpthread).oos.writeObject(msg);
                            ((ServeClient) tmpthread).oos.flush();
                        }
                    }

                }
            } catch (Exception E) {
                if (E instanceof InterruptedException){
                    System.out.println("Client initiated closing connection " + this.getName());
                }
                else {
                    System.out.println(E);
//                    System.out.println("Some other mistake");
                }
                try {
                    ois.close();
                    oos.close();
                    socket.close();
                } catch (IOException e2){
                    System.out.println("Smth wrong with Input/Output e2");
                }
                System.out.println("Connection closed " + this.getName());
            }
        }
    }

    public class AdminConsole extends Thread{
        Scanner scanner;
        public AdminConsole(){
            this.scanner = new Scanner(System.in);
        }
        public void run(){
            while(true){
                String line = scanner.nextLine();
                if ("list".equals(line.toLowerCase())){
                    ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                    int noThreads = currentGroup.activeCount();
                    Thread[] lstThreads = new Thread[noThreads];
                    currentGroup.enumerate(lstThreads);
                    for (int i = 0; i < noThreads; i++) {
                        Thread tmpthread = lstThreads[i];
                        if (tmpthread instanceof ServeClient) {
                            System.out.println(tmpthread.getName());
                        }
                    }
                }
                if (Pattern.matches("^drop ++[0-9]++$", line.toLowerCase())){
                    String delims = " ";
                    String[] tokens = line.split(delims);
                    int killId = Integer.parseInt(tokens[tokens.length-1]);;
                    ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                    int noThreads = currentGroup.activeCount();
                    Thread[] lstThreads = new Thread[noThreads];
                    currentGroup.enumerate(lstThreads);
                    for (int i = 0; i < noThreads; i++) {
                        Thread tmpthread = lstThreads[i];
                        if (tmpthread instanceof ServeClient && ((ServeClient) tmpthread).ClientId == killId){
                            System.out.println("Closing connection "+ tmpthread.getName());
                            tmpthread.interrupt();
                            try{
                                //doing this because readobject() method doesn't throw InterruptedException
                                //trying to make it throw Exception with streams instead
                                ((ServeClient) tmpthread).oos.writeObject(new Message("q"));
                                ((ServeClient) tmpthread).ois.close();
                                ((ServeClient) tmpthread).oos.close();
                            } catch (IOException e){

                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}
