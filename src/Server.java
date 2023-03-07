package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server implements Runnable{

    //========================================================

    private int id;
    private boolean conexao;
    private boolean repassa;
    private DatagramSocket server_socket;
    private String dest_ip;                 //endereço de destino
    private String ip;                      //ip da máquina
    private int port;
    private InetAddress address;

    //========================================================

    //BOOLEAN DE CONTROLE
    private boolean cast_type = true;       //true = broadcast | false = unicast

    //========================================================
    
    public Server( int port, String ip, String dest_ip ){
        this.dest_ip = dest_ip;
        this.ip = ip;
        this.port = port;
        conexao = true;
    }

    @Override
    public void run() {

        try {

            address = InetAddress.getByName(ip);
            InetAddress dest_add = InetAddress.getByName(dest_ip);

            estabelecerPorta();

            if( conexao ){
                System.out.println( "P" + id + " rodando em " +  
                address.getHostAddress() + ":" + server_socket.getLocalPort());
            }
            
            while( conexao ){

                //arrays de bytes utilizados para armazenar os dados dos datagramas
                byte[] dados_recebidos = new byte [1024];
                byte[] dados_envio;

                DatagramPacket datagrama_recebido = new DatagramPacket( dados_recebidos, dados_recebidos.length);
                DatagramPacket pacote_envio = null;
                
                //recebe o datagrama e armazena os dados no array de bytes 
                server_socket.receive(datagrama_recebido);
                dados_recebidos = datagrama_recebido.getData();

                String msg = new String(dados_recebidos);
                String conteudo[] = msg.split(";"); 
                String resposta;

                //verifica se recebeu do cliente
                if(conteudo.length == 3){

                    System.out.println("\nP" + id + " enviou a mensagem > " + conteudo[0] );
                    resposta = id + ";" + conteudo[1] + ";" + conteudo[0] + ";";
                    repassa = true;
                
                }else{
                
                    int destino = (int)Double.parseDouble(conteudo[1]);
                    resposta = msg;

                    //o primeiro processo envia o datagrama, e, somente o P1 pode repassar
                    repassa = false;

                    //verifica se a mensagem é para ele ou dele
                    if((destino == id)){
                        System.out.println("\nP" + id + " recebeu a mensagem < " + conteudo[2] );
                        System.out.println("--- DESTINATÁRIO ALCANÇADO ---" );
                    }
                    else{

                        if(id == 1){
                            System.out.println("\nP" + id + " recebeu a mensagem < " + conteudo[2] );
                            
                            if(cast_type){
                                System.out.println("P" + id + " repassou a mensagem via Broadcast" );
                            }
                            else{
                                System.out.println("P" + id + " repassou a mensagem via Unicast" );
                            }
                            repassa = true;
                        }
                        else{
                            //Somente exibe que recebeu, mas não é o destinatário
                            System.out.println("\nP" + id + " recebeu a mensagem < " + conteudo[2] );
                            System.out.println("--- P" + id + " não é destinatário ---" );
                        }

                    }
                }

                dados_envio = resposta.getBytes();//envia todo o datagrama para outro processo

                //verifica se deve passar para outro processo ou entregar ao seu cliente
                if(repassa){

                    if(cast_type && (id == 1)){ //BroadCast (só envia dessa forma se for o P1)

                        DatagramPacket [] pacote_broadcast = new DatagramPacket[3];

                        for(int i = 2; i <= 4; i++){
                            //10002 --> 10004
                            pacote_broadcast[i - 2] = new DatagramPacket(  dados_envio, 
                                                                            dados_envio.length, 
                                                                            dest_add, 
                                                                            (10000 + i) );
                            server_socket.send(pacote_broadcast[i - 2]);
                        }

                    }
                    else{ //UniCast

                        int destino;
                        if(id != 1){
                            destino = 1;
                        }else{
                            destino = (int)Double.parseDouble( conteudo[1] );
                        }
                        
                        pacote_envio = new DatagramPacket(  dados_envio, 
                                                            dados_envio.length, 
                                                            dest_add, 
                                                            10000 + destino );

                        server_socket.send(pacote_envio);

                    }
  
                }

            }

            server_socket.close(); 
        } 
        catch (IOException e) {
            System.err.println("Exceção disparada: " + e.getMessage());
        } 
    }

    private void estabelecerPorta(){
        boolean porta_bloqueada = true;

        while(porta_bloqueada){
            try {
                server_socket = new DatagramSocket( port, address );
                porta_bloqueada = false;   
            } catch (SocketException e) {
                port++;
            }
        }

        id = port - 10000;
    }

}
