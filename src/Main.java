import java.io.*;

public class Main {


    public static void main(String[] args) {
        String file = "/home/cj/ClionProjects/SPTReader/main.cpp";
        try {
            CPPSource cppSource = CPPSource.parse(file);
            cppSource.getClass("Packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
