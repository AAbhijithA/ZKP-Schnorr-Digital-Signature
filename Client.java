import java.util.*;
import java.io.*;
import java.net.*;

class functions{

    //Double to String vice-versa conversions
    public String convert(double value){
        return String.valueOf(value);
    }

    public double numConvert(String s){
        return Double.parseDouble(s);
    }

    //Binary exponentiation for O(log(b)) computation of powers
    public double binEXP(int a,int b){
        double ans = 1;
        if(b >= 0){
            while(b > 0){
                if((b & 1) == 1) ans = (ans * a);
                a = a * a;
                b = b >> 1;
            }
        }
        else{
            b = -b;
            while(b > 0){
                if((b & 1) == 1) ans = ans / a;
                a = a * a;
                b = b >> 1;
            }
        }
        return ans;
    }
}

public class Client{
    static int g = 7;
    static Socket soc = null;
    static DataInputStream input = null;
    static DataOutputStream output = null;
    static BufferedReader bufRead = null;
    static Random rand = null;
    public static void main(String[] args) throws Exception{
        try{
            //Initializing the input output stream objects along with client functions 
            rand = new Random();
            soc = new Socket("localhost",3333);
            input = new DataInputStream(soc.getInputStream());
            output = new DataOutputStream(soc.getOutputStream());
            bufRead = new BufferedReader(new InputStreamReader(System.in));
            functions f = new functions();

            //Running message verification
            while(true){
                //Enter message M and compute key k to compute y = g^x
                System.out.print("Enter your message (Enter end to stop): ");
                String message = bufRead.readLine();
                output.writeUTF(message);
                output.flush();
                if(message.equals("end")) break;
                int x = rand.nextInt(5);
                System.out.println("Choosen private key by client: " + x);
                double y = f.binEXP(g,x);
                output.writeUTF(f.convert(y));
                output.flush();

                //generate k and send r = g^k
                int k = rand.nextInt(5);
                System.out.println("Random value chosen by client: " + k);
                double r = f.binEXP(g,k);
                output.writeUTF(f.convert(r));
                output.flush();

                //Compute s = k - hashr*x and send it to server
                String hashr = input.readUTF();
                System.out.println("Received verification hash: " + hashr);
                double h1 = f.numConvert(hashr);
                double s = k - (h1 * x);
                System.out.println("Computed signature with <s,hash>: " + s);
                output.writeUTF(f.convert(s));
                output.flush();

                //Verification
                String verified = input.readUTF();
                if(verified.equals("1")) System.out.println("Message verified");
                else System.out.println("Message not verified");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }

        //Closing all the objects
        soc.close();
        input.close();
        output.close();
        bufRead.close();
    }
}