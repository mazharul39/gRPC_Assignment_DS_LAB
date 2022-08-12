package Sources;

import com.assignment.grpc.User;
import com.assignment.grpc.userGrpc;
import io.grpc.stub.StreamObserver;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

public class UserService extends userGrpc.userImplBase{
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    String url = "jdbc:mysql://localhost/grpc_user";
    String name= "root";
    String pass= "";
    @Override
    public void login(User.LoginRequest request, StreamObserver<User.Response> responseObserver) {
        String username = request.getUsername();
        String password = passwordHasher(request.getPassword());

        logger.info("Logging in as "+username);

        User.Response.Builder response = User.Response.newBuilder();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,name,pass);
            Statement statement = connection.createStatement();
            String loginQuery = "SELECT password from user WHERE username='"+username+"'";
            ResultSet resultSet = statement.executeQuery(loginQuery);
            resultSet.next();
            String userpass = resultSet.getString(1);
            if(password.equals(userpass)){
                response.setResponseCode(200).setMessage("OK! Login Successful");

                logger.info("Login Successful as  "+username);
            }
            else{
                response.setResponseCode(400).setMessage("Bad Request !!!");
                logger.info("Login failed as "+username);
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        }catch (Exception ex){
            System.out.println(ex);
        }


    }

    @Override
    public void register(User.RegistrationRequest request, StreamObserver<User.RegistrationResponse> responseObserver) {

        String registerPassword = passwordHasher(request.getPassword());
        String registerUsername = request.getUsername();

        User.RegistrationResponse.Builder registrationResponse = User.RegistrationResponse.newBuilder();


        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,name,pass);
            Statement statement = connection.createStatement();

            String loginQuery = "SELECT password from user WHERE username='"+registerUsername+"'";
            ResultSet resultSet = statement.executeQuery(loginQuery);

            if(!resultSet.next()){

                String registerQuery = "INSERT INTO user(username,password) VALUES(\""+registerUsername+"\",\""+registerPassword+"\")";

                int flag= statement.executeUpdate(registerQuery);
                if(flag==1){
                    registrationResponse.setResponseCode(201).setMessage("New user "+registerUsername+" was registered");
                }
                else {
                    registrationResponse.setResponseCode(400).setMessage("Registration Failed. ");
                }
            }

            else{
                registrationResponse.setResponseCode(400).setMessage("Use a different username");
            }

            responseObserver.onNext(registrationResponse.build());
            responseObserver.onCompleted();

        }catch (Exception ex){
            System.out.println(ex);
        }


    }

    @Override
    public void logout(User.LogoutRequest request, StreamObserver<User.Response> responseObserver) {
        super.logout(request, responseObserver);
    }

    static String passwordHasher(String password){
        char[] string = password.toCharArray();
        String hash="";
        for (int i=0;i< password.length();i+=2){
            hash += (char)(string[i]+(i%30));
        }

        for(int i=1;i<password.length();i+=2){
            hash+=(char)(string[i]+(i%35));
        }

        return hash;
    }
}