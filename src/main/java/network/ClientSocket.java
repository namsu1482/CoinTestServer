package network;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ClientSocket {
    java.util.logging.Logger Logger = java.util.logging.Logger.getLogger(ClientSocket.class.getSimpleName());

    DataInputStream in = null; // stream 타입의 문자를 읽어서 저장할 수 있는 함수.

    Socket clientSocket = null; // 서버소켓이 지정한 포트를 타고온 상대 ip를 저장할 수 있다.

    public ClientSocket(Socket acceptedSocket) {
        clientSocket = acceptedSocket;
        InetAddress clientAddress = clientSocket.getInetAddress();
        System.out.println("client IP :" + clientAddress);
        try {
            clientSocket.setSoTimeout(3000);
            System.out.println("client access");
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void transData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    in = new DataInputStream(clientSocket.getInputStream());
                    byte[] inputData = new byte[1024];
                    in.read(inputData);
                    String receivedDataFromApp = new String(inputData, StandardCharsets.UTF_8).trim();

                    JSONObject appReqObject = new JSONObject(receivedDataFromApp);
                    JSONObject responseObject = ResponseData.responseParser(appReqObject);

                    DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    Logger.info(responseObject.toString());

                    dataOutputStream.write(responseObject.toString().getBytes());
                    dataOutputStream.flush();

                    in.close();
                    dataOutputStream.close();


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
//        serverSocket.close();
    }


}
