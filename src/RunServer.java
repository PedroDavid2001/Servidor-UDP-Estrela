package src;

public class RunServer {
    /*
     * Use esse valor de porta para a máquina central e 
     * altere-o na(s) outra(s) máquina(s) que simulará(m) 
     * os clientes mediados pela porta 10001. 
     */
    int port = 10001;
    
    public RunServer() {
        this.rodar();
    }

    private void rodar() {
        try {

            // Nos argumentos, você altera as String de da sua máquina e ip da máquina destino
            Server servidor = new Server( port, "IP-da-maquina", "IP-destino" ); 
            Thread serverThread = new Thread(servidor);

            serverThread.start();

        } catch (Exception e) {
            System.err.println("Exceção disparada: " + e.getMessage() );
        }
    }

    public static void main(String[] args) throws Exception {
        new RunServer();
    }
}