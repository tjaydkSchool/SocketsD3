package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EchoServer implements Runnable {

    LocalDateTime date = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E LLL dd HH:mm:ss yyyy");

    
    BufferedReader in;
    PrintWriter out;
    String echo, dateFormatted;
    Socket s;
    Scanner scan;

    public EchoServer(Socket soc) {
        s = soc;
    }

    public static void main(String[] args) throws IOException {
        String ip = "localhost";
        int port = 4321;
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(ip, port));

        while (true) {
            EchoServer e = new EchoServer(ss.accept());
            Thread t1 = new Thread(e);
            t1.start();
        }
    }

    @Override
    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            dateFormatted = date.format(formatter);
            while (true) {
                String toDo = "NULL", word = "NULL";
                echo = in.readLine();

                scan = new Scanner(echo);
                scan.useDelimiter("#");
                
                while (scan.hasNext()) {
                    toDo = scan.next();
                    word = scan.next();
                }
                

                switch (toDo) {
                    case "UPPER" : 
                        out.println(word.toUpperCase());
                        break;
                    case "LOWER" : 
                        out.println(word.toLowerCase());
                        break;
                    case "REVERSE" : 
                        String toRev = new StringBuilder(word).reverse().toString();
                        char toUpper = toRev.charAt(0);
                        String charToUpper = toUpper + "";
                        String newWord = charToUpper.toUpperCase() + toRev.substring(1, toRev.length());
                        out.println(newWord);
                        break;
                    case "TRANSLATE" : 
                        Document doc = Jsoup.connect("http://translate.reference.com/danish/english/" + word).get();
                        Elements translated = doc.select(".translate-module .target-area-container textarea");
                        out.println(translated.text());
                        break;
                        
                    default: 
                        out.println("PLEASE CHOOSE ONE OF THE FOLLOWING: UPPER/LOWER/REVERSE/TRANSLATE FOLLOW BY #YOURWORD");
                        break;
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
