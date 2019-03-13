package android.curso.sdacelermetro;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    private String msg;
    private String hostname;
    private int portaServidor;
    public PrintWriter  sendServer;
    public Socket mySocket = null;

    Client(String hostname, int portaServidor, String msg) {
        this.hostname = hostname;
        this.portaServidor = portaServidor;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mySocket   = new Socket(hostname, portaServidor);
            sendServer = new PrintWriter(mySocket.getOutputStream());
            ObjectOutputStream saida = new ObjectOutputStream(mySocket.getOutputStream());

            while(true) {
                if (mySocket.isConnected()) {
                    ObjectInputStream entrada = new ObjectInputStream(mySocket.getInputStream());

                    saida.flush();
                    saida.writeUTF(msg);
                    saida.close();
                    portaServidor =0;
                    mySocket.close();

                    /*s = entrada.readInt();
                    System.out.println("Valor: "+s);*/
                    Thread.sleep(1000);
                }else{
                    mySocket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Cliente não conectado!");
            try {
                mySocket.close();
            } catch (IOException ignored){}
        } catch (InterruptedException ignored) { }
        return null;
    }

    protected void onPreExecute(){}
    protected void onPostExecute(Void voids){}
    protected void onProgressUpdate(Void... voids) {}

}

