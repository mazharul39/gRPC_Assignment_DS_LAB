import com.assignment.grpc.User;
import com.assignment.grpc.userGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.logging.Logger;

public class GRPC_Client {
    private static final Logger logger = Logger.getLogger(GRPC_Client.class.getName());

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",1111).usePlaintext().build();

        userGrpc.userBlockingStub userBlockingStub = userGrpc.newBlockingStub(managedChannel);

        while(true){
            Scanner scanner = new Scanner(System.in);

            System.out.println("Hey there! :\n1.Register\n2.Login\n3.Exit");

            int option = scanner.nextInt();
            if(option==1){
                boolean flag = true;
                while(flag){
                    Scanner scanner1 = new Scanner(System.in);
                    System.out.println("Register");
                    System.out.println("username: ");
                    String registerUserName = scanner1.nextLine();
                    System.out.println("passowrd: ");
                    String registerPassword = scanner1.nextLine();
                    User.RegistrationRequest registrationRequest = User.RegistrationRequest.newBuilder().setUsername(registerUserName).setPassword(registerPassword).build();
                    User.RegistrationResponse registrationResponse = userBlockingStub.register(registrationRequest);
                    logger.info(registrationResponse.getResponseCode()+"\n"+registrationResponse.getMessage());
                    if(registrationResponse.getResponseCode()==201){
                        flag=false;
                    }
                }
            }

            else if(option==2){
                boolean flag = true;
                while(flag){

                    Scanner scanner2 = new Scanner(System.in);
                    System.out.println("Username: ");
                    String username = scanner2.nextLine();
                    System.out.println("Password: ");
                    String password = scanner2.nextLine();

                    User.LoginRequest loginRequest = User.LoginRequest.newBuilder().setUsername(username).setPassword(password).build();

                    User.Response response = userBlockingStub.login(loginRequest);
                    logger.info(response.getResponseCode()+"\n"+response.getMessage());

                    if(response.getResponseCode()==200){
                        flag=false;
                    }
                }
            }

            else {
                break;
            }
        }
    }
}