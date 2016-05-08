import java.io.*;

public class Main {


    public static void main(String[] args) {
        String file = "/home/cj/ClionProjects/SPTReader/main.cpp";
        try {
            CPPSource cppSource = CPPSource.parse(file);
            System.out.println(cppSource.getClass("Packet").getFunction("getTitle"));
            System.out.println(cppSource.getClass("Packet").getFunction("getContent"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
