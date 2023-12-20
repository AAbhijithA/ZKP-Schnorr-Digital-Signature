import java.io.*;
import java.net.*;
import java.lang.Math;

class ServerFunctions{
    public long m = 1007;
    public int p = 31; 

    //Double to String vice-versa conversions
    public String convert(double value){
        return String.valueOf(value);
    }

    public double numConvert(String s){
        return Double.parseDouble(s);
    }

    //String hashing h = s[0]*(p^0) + ... + s[i]*(p^i) + .... + s[n-1]*(p^(n-1)) % m
    public double StringHash(String r,String message){
        double mul = 1;
        int val;
        long hashVal = 0;
        char c;
        for(int i=0;i<r.length();i++){
            c = r.charAt(i);
            if(c == '.') break;
            val = (int) c;
            hashVal = (hashVal + (long)(val*mul)) % m;
            mul = mul * p;
        }
        for(int i=0;i<message.length();i++){
            c = message.charAt(i);
            val = (int) c;
            hashVal = (hashVal + (long)(val*mul)) % m;
            mul = mul * p;
        }
        return hashVal;
    }

    //Handling huge negative computations
    public double FinalCompute(double comp,int g,double power){
        if(power > 0){
            while(power > 0){
                comp = comp * g;
                power = power - 1;
            }
        }
        else{
            power = -power;
            while(power > 0){
                comp = comp / g;
                power = power - 1;
            }
        }
        return comp;
    }
}

public class Server{
    static int g = 7;
    static ServerSocket Serversoc = null;
    static Socket soc = null;
    static DataInputStream input = null;
    static DataOutputStream output = null;
    static BufferedReader bufRead = null;
    public static void main(String[] args) throws Exception{
        try{
            //Initializing the input output stream objects along with server functions 
            Serversoc = new ServerSocket(3333);
            soc = Serversoc.accept();
            input = new DataInputStream(soc.getInputStream());
            output = new DataOutputStream(soc.getOutputStream());
            bufRead = new BufferedReader(new InputStreamReader(System.in));
            ServerFunctions f = new ServerFunctions();

            //Running verifications
            while(true){
                String message = input.readUTF();
                System.out.println("Client sends message: " + message);
                if(message.equals("end")) break;
                String y = input.readUTF();
                System.out.println("Client sends y: " + y);

                String r = input.readUTF();
                System.out.println("Client sends r: " + r);

                //Compute hashr = H(r || M)
                double hashr = f.StringHash(r,message);
                System.out.println("Computed hash: " + hashr);
                output.writeUTF(f.convert(hashr));
                output.flush();

                //Compute rv = g^s * y^h1 and then hashVerification = H(rv || M)
                String signature = input.readUTF();
                System.out.println("Recieved Signature <s,hash>: " + signature);
                double s = f.numConvert(signature);
                double comp = Math.pow(f.numConvert(y),hashr);
                double rv = f.FinalCompute(comp,g,s);
                System.out.println("Computed Received Verification: " + rv);
                double hashVerification = f.StringHash(f.convert(rv),message);
                System.out.println("Verification hash: " + hashVerification);

                //If both hashes are equal then we have a verified message
                String verified;
                if(hashr == hashVerification){
                    System.out.println("Message verified");
                    verified = "1";
                }
                else{
                    System.out.println("Message not verified");
                    verified = "0";
                }
                output.writeUTF(verified);
                output.flush();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }

        //Closing all the objects
        Serversoc.close();
        soc.close();
        input.close();
        output.close();
        bufRead.close();
    }
}