package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Callable;


class Message implements Serializable{
    public String data;

    public Message(String data) {
        this.data = data;
    }

    public String get_data(){
        return data;
    }
}


public class Client {
    static Logger log = LoggerFactory.getLogger(Client.class);

    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void loop() throws Exception {
        Socket socket = new Socket(host, port);

        Writer writer = new Writer(socket);
        writer.start();

        Reader reader = new Reader(socket);
        reader.start();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }


    public class Writer extends Thread{
//        Scanner scanner;
        BufferedReader br;
        ObjectOutputStream oos;
        OutputStream out;
        Socket socket;

        public Writer(Socket socket){
            this.br = new BufferedReader(new InputStreamReader(System.in));
            this.socket = socket;
            this.setName("writer");
            try {
                this.out = this.socket.getOutputStream();
                this.oos = new ObjectOutputStream(this.out);
            } catch (IOException e1) {
                System.out.println("Smth wrong with Input/Output e0");
            }
//            this.scanner = new Scanner(System.in);
            this.oos = oos;
        }

        @Override
        public void run(){
            try {
                while (!isInterrupted()) {
                    while (!br.ready()) {
                        Thread.sleep(200);
                    }
                    String line = br.readLine();
                    System.out.println("1");
                    Message msg = new Message(line);
                    System.out.println("2");
                    this.oos.writeObject(msg);
//                    this.oos.flush();
                    if ("q".equals(line)) {
                        this.interrupt();
                    }
                }
            } catch (InterruptedException e0) {
                System.out.println("Writer was interrupted");
            }
            catch(Exception e){
                System.out.println(e);
            }

            try {
                this.oos.close();
                System.out.println("closed output stream");
                this.socket.close();
                System.out.println("closed socket");
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                currentGroup.enumerate(lstThreads);
                for (int i = 0; i < noThreads; i++) {
                    Thread tmpthread = lstThreads[i];
                    if (tmpthread instanceof Reader){
                        tmpthread.interrupt();
                        ((Reader) tmpthread).ois.close();
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public class Reader extends Thread{

        ObjectInputStream ois;
        InputStream inputStream;
        Socket socket;

        public Reader(Socket socket){
            this.setName("reader");
            try{
                this.socket = socket;
                this.inputStream = this.socket.getInputStream();
                this.ois = new ObjectInputStream(this.inputStream);
            } catch (Exception e){

            }
        }

        @Override
        public void run(){
            while (!isInterrupted()) {
                try {
                    Message resp = (Message) ois.readObject();
                    String responseLine = resp.get_data();
                    if ("q".equals(responseLine)) {
                        System.out.println("got q from server");
                        this.interrupt();
                    } else {
                        System.out.println(responseLine);
                    }
                } catch (IOException e1) {

                } catch (ClassNotFoundException e2) {

                }
            }
            try{
                this.ois.close();
                System.out.println("closed input stream");
                this.socket.close();
                System.out.println("closed socket");
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                currentGroup.enumerate(lstThreads);
                for (int i = 0; i < noThreads; i++) {
                    Thread tmpthread = lstThreads[i];
                    if (tmpthread instanceof Writer){
                        System.out.println("interrupting writer");
                        tmpthread.interrupt();
                        //doing this because scanner locks
//                        String s = "q";
//                        InputStream testInput = new ByteArrayInputStream( s.getBytes("UTF-8") );
//                        System.setIn(testInput);
//                        ((Writer) tmpthread).oos.close();
//                        ((Writer) tmpthread).socket.close();
                    }
                }
            }catch (Exception e) {
                System.out.println(e);
            }
        }

    }
}