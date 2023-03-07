package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client implements Runnable {
    
    Scanner scan = new Scanner(System.in);
    DatagramSocket datagramSocket;
    boolean conectado;
    int port;
    private String ip; //ip da máquina

    public Client( int port, String ip ){
        this.ip = ip;
        conectado = true;
        this.port = port;
    }

    @Override
    public void run() {
        try {

            // inicialização do socket de datagrama para adquirir a porta 
            datagramSocket = new DatagramSocket();

            InetAddress address = InetAddress.getByName(ip);
            
            while( conectado ){

                System.out.print("Digite a mensagem > ");
                String msg = scan.nextLine();
                
                System.out.print("Digite o id de destino > ");  
                int destino = scan.nextInt();
                
                //verifica se o id digitado é mesmo da máquina
                while( destino == (port - 10000) ){
                    System.out.print("Digite um id diferente > ");
                    destino = scan.nextInt();
                    scan.nextLine();
                }
                msg += ";" + destino;
                msg += ";";

                byte [] bufferEnvio = msg.getBytes();

                DatagramPacket datagramEnvio = new DatagramPacket(
                                                bufferEnvio, 
                                                bufferEnvio.length, 
                                                address, 
                                                port );
                                
                datagramSocket.send(datagramEnvio); 
                
                System.out.println("\nServidor enviando a mensagem. . ." );
                
            }
            
            System.out.println("\nDesconectado do servidor.");
            datagramSocket.close();

        } catch (IOException e) {
            System.err.println("Exceção disparada: " + e.getMessage());
        }
    }
    
}