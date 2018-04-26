import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("?-ary fat tree?");
        Scanner input=new Scanner(System.in);
        int k=input.nextInt();
        System.out.println("How many servers do you want per rack?");
        int density=input.nextInt();
        System.out.println("And how many servers which will talk to servers in other rack?");
        int whatToTalk=input.nextInt();

        if(density<whatToTalk){
            System.out.println("Wrong input !");
            System.exit(1);
        }


        FatTree fatTree=new FatTree(k,density,whatToTalk);
        System.out.println("Output routing matrix....");
        fatTree.printRoutingMatrix(""+k+"-"+density+"-"+whatToTalk+".txt");
        System.out.println("Output over");

    }
}
