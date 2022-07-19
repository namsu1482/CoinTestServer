package network;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class TcpConnection {
    Logger Logger = java.util.logging.Logger.getLogger(TcpConnection.class.getSimpleName());
    Socket acceptClientSocket = null; // 서버소켓이 지정한 포트를 타고온 상대 ip를 저장할 수 있다.
    Socket clientSocketReceive = null;
    PrintWriter writer = null;
    DataInputStream in = null; // stream 타입의 문자를 읽어서 저장할 수 있는 함수.
    ServerSocket serverSocket = null;
    ServerSocket serverSocketReceive = null;
    String clientAddress = null;
    boolean NetworkError = false;

    File receivedFile = null;
    String filePath = "";

    public TcpConnection(int port) {
        try {
            serverSocket = new ServerSocket(port); // 서버소켓 생성
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer() throws IOException {
        System.out.println("run tcp server");
        for (; ; ) {
            try {
                acceptClientSocket = serverSocket.accept();
//                clientSocket = serverSocket.accept(); // 클라이언트로부터 데이터가 오는것을 감지한다.
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("accept canceled");
                if (acceptClientSocket != null) {
                    acceptClientSocket.close();
                }
                break;
            }
            if (!acceptClientSocket.isClosed()) {
                ClientSocket clientSocket = new ClientSocket(acceptClientSocket);
                clientSocket.transData();
//                InetAddress clientAddress = clientSocket.getInetAddress();
//                System.out.println("client IP :" + clientAddress);
//                clientSocket.setSoTimeout(3000);
//                System.out.println("client access");
//                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                transData();
            }


        }

    }

    public void transData() throws IOException {
        in = new DataInputStream(acceptClientSocket.getInputStream());
        byte[] inputData = new byte[1024];
        in.read(inputData);
        String receivedDataFromApp = new String(inputData, StandardCharsets.UTF_8).trim();

        JSONObject appReqObject = new JSONObject(receivedDataFromApp);
        JSONObject responseObject = ResponseData.responseParser(appReqObject);

        DataOutputStream dataOutputStream = new DataOutputStream(acceptClientSocket.getOutputStream());

        Logger.info(responseObject.toString());
        dataOutputStream.write(responseObject.toString().getBytes());
        dataOutputStream.flush();

        in.close();
        dataOutputStream.close();

        acceptClientSocket.close();
//        serverSocket.close();
    }

    public void stopTcpServer() {

        try {
            if (acceptClientSocket != null) {
                if (acceptClientSocket.isBound()) {
                    acceptClientSocket.close();
                    System.out.println("server closed");
                }
            }


            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
