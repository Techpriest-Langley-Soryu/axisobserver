package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class TestAsServer {

    /**
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
//  стартуем сервер на порту 3345

        try (ServerSocket server= new ServerSocket(3345)){
// становимся в ожидание подключения к сокету под именем - "client" на серверной стороне
            Socket client = server.accept();
            conn.Conn();
// после хэндшейкинга сервер ассоциирует подключающегося клиента с этим сокетом-соединением
            System.out.print("Connection accepted.");

// инициируем каналы для  общения в сокете, для сервера

// канал записи в сокет
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            System.out.println("DataOutputStream  created");

            // канал чтения из сокета
            DataInputStream in = new DataInputStream(client.getInputStream());
            System.out.println("DataInputStream created");

// начинаем диалог с подключенным клиентом в цикле, пока сокет не закрыт
            while(!client.isClosed()){

                System.out.println("Server reading from channel");
                /*ожидание получения серийника(в тесте текст из txt), проверка его в базе, если есть -- отправить 1, если нет -- 0 и ожидание ввода ключа */
// сервер ждёт в канале чтения (inputstream) получения данных клиента
                String entry = in.readUTF();

// после получения данных считывает их
                System.out.println("READ from client message - "+entry);

// и выводит в консоль
                System.out.println("Server try writing to channel");

// инициализация проверки условия продолжения работы с клиентом по этому сокету по кодовому слову       - quit
                if(entry.equalsIgnoreCase("quit")){
                    System.out.println("Client initialize connections suicide ...");
                    out.writeUTF("Server reply - "+entry + " - OK");
                    out.flush();
                    Thread.sleep(3000);
                    break;
                }
                if(entry.startsWith("key=")){
                    String ent=entry.substring(4);
                    int x=conn.ReadDB(ent);
                    if(x==0){
                        System.out.println("Invalid Key");
                        out.writeUTF("0");
                        /*отправить клиенту 0*/
                        continue;
                    }
                    if(x==1){
                        System.out.println("Valid key");
                        out.writeUTF("1");
                        /*отправить клиенту 1*/
                        continue;
                    }
                }
                if(entry.startsWith("SN=")){
                    String ent=entry.substring(3);
                    conn.WriteDB(ent);
                    System.out.println("SN is writed");
                    continue;
                }
                /* проверка в базе ключа, если ключ валид, отправить 1 и запросить серийники(для теста текст из txt файла), после отправить их на сервер
                  , после чего сервер их записыывает в базу. **/

// если условие окончания работы не верно - продолжаем работу - отправляем эхо-ответ  обратно клиенту
                out.writeUTF("Server reply - "+entry + " - OK");
                System.out.println("Server Wrote message to client.");

// освобождаем буфер сетевых сообщений (по умолчанию сообщение не сразу отправляется в сеть, а сначала накапливается в специальном буфере сообщений, размер которого определяется конкретными настройками в системе, а метод  - flush() отправляет сообщение не дожидаясь наполнения буфера согласно настройкам системы
                out.flush();

            }

// если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            in.close();
            out.close();

            // потом закрываем сам сокет общения на стороне сервера!
            client.close();

            // потом закрываем сокет сервера который создаёт сокеты общения
            // хотя при многопоточном применении его закрывать не нужно
            // для возможности поставить этот серверный сокет обратно в ожидание нового подключения

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            System.err.println("Не найден класс, крч SQL"+e.getMessage());
            e.printStackTrace();
        }catch (SQLException e){
            System.err.println("Какой-то SQL ошибка"+e.getMessage());
            e.printStackTrace();
        }
    }
}
