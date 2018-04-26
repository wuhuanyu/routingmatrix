import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.collections.transformation.FilteredList;
import jdk.internal.cmm.SystemResourcePressureImpl;
import node.AggreSwitch;
import node.CoreSwitch;
import node.Host;
import node.Rack;
import node.RackSwitch;
import node.iNode;

public class FatTree {
    private List<CoreSwitch> coreSwitches;
    private List<AggreSwitch> aggreSwitches;
    private List<Rack> racks;
    private List<List<Host>> hosts;
    private Random rnd=new Random(13);



    private int K;
    private int iCores;
    private int iAggres;
    private int iHosts;
    private int iPod;
    private int iRacks;
    private int density;

    private int iLinks=0;
    private int numWhoWantToTalk;

    private List<Flow> flows=new ArrayList<>();
    private List<DirectedLink> links=new ArrayList<>();

    private int[][] routingMatrix;

    public FatTree(int kary,int density,int numWhoWantToTalk){
        this.K=kary;
        this.iPod=kary;
        this.density=density;
        this.iCores=(kary/2)*(kary/2);
        this.iAggres=kary*kary/2;
        this.iRacks=iAggres;
        this.iHosts=iAggres*density;

        // there is numWhoWantToTalk servers who want to communicate to numWhoWantToTalk servers in another rack

        this.numWhoWantToTalk = numWhoWantToTalk;
        build();
        createTopo();
        determineRouting();
        determineRoutingMatrix();
    }
    public void printRoutingMatrix(String log) throws IOException {
        PrintWriter out=new PrintWriter(log);
        out.print("            ");
        for(int flowIdx=0;flowIdx<flows.size();flowIdx++){
            Flow flow=flows.get(flowIdx);
            //r1h1-r4h2
            out.print(flow.getSrc()+"-"+flow.getDest()+" ");
        }
       out.println();
        for(int linkIdx=0;linkIdx< links.size();linkIdx++){

            out.print(links.get(linkIdx)+"         ");
            for(int flowIdx=0;flowIdx<flows.size();flowIdx++){
                out.print(""+routingMatrix[linkIdx][flowIdx]+"          ");
            }
            out.println();
        }
        out.flush();
        out.close();
    }

    private void determineRoutingMatrix(){
        for(int linkIdx=0;linkIdx<links.size();linkIdx++){
            for(int flowIdx=0;flowIdx<flows.size();flowIdx++){
                int isIn=isIn(
                        links.get(linkIdx),
                        flows.get(flowIdx)
                );
                routingMatrix[linkIdx][flowIdx]=isIn;
            }
        }
    }

    private void determineRouting(){
        for(int currentIdx=0;currentIdx<iRacks;currentIdx++){
            //choose who want to talk;
            List<Host> hostsInRack=hosts.get(currentIdx);
            for(int who=0;who<hostsInRack.size();who++){
                Host host=hostsInRack.get(who);
                if(host.isWantToTalk()){
                    //select another rack
                    for(int otherRackIdx=0;otherRackIdx<iRacks;otherRackIdx++){
                        if(otherRackIdx!=currentIdx){
                            List<Host> hostsInOtherRack=hosts.get(otherRackIdx);
                            for(int otherWho=0;otherWho<hostsInOtherRack.size();otherWho++){
                                Host otherHost=hostsInOtherRack.get(otherWho);
                                if(otherHost.isWantToTalk()){
                                    Flow flow=new Flow(host,otherHost);
                                    determinePath(currentIdx,otherRackIdx,flow);
                                    flows.add(flow);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void determinePath(int srcRack,int destRack,Flow flow){
        Host src=flow.getSrc();
        Host dest=flow.getDest();
        int rackPerPod=K/2;
        RackSwitch srcRackSwitch=src.getRackSwitch();

        flow.addToPath(0,srcRackSwitch);

        RackSwitch destRackSwitch=dest.getRackSwitch();
        if(srcRack/rackPerPod!=destRack/rackPerPod){
            //must go to core switch;
            //first select a aggre switch;
            int aggreIdx=getRandom((srcRack/rackPerPod)*rackPerPod,(srcRack/rackPerPod+1)*rackPerPod-1);
            flow.addToPath(1,aggreSwitches.get(aggreIdx));

            /**
             * then select a core switch
             * for example if K=8; select 2rd (start from 0) aggregation switch, the 2rd of 0 pod,then We can only select a core switch between 8 and 11
             */
            int start=aggreIdx%rackPerPod*rackPerPod; //start=2%4*4
            int end=(aggreIdx%rackPerPod+1)*rackPerPod-1; //end=(2%4+1)*4-1
            int coreIdx=getRandom(start,end);
            flow.addToPath(2,coreSwitches.get(coreIdx));

            /**
             * aggre switch again
             */
            flow.addToPath(3,aggreSwitches.get(destRack));
            flow.addToPath(4,destRackSwitch);

//            int offet=coreIdx/rackPerPod;

//            int aggre=offet+(rackPerPod*(destRack/rackPerPod)); //offset + start

        }else{
            //they are in the same pod;
            int aggreIdx=getRandom((srcRack/rackPerPod)*rackPerPod,(srcRack/rackPerPod+1)*rackPerPod-1);

            flow.addToPath(1,aggreSwitches.get(aggreIdx));
            flow.addToPath(2,destRackSwitch);
        }
    }


    private void build(){
        coreSwitches=new ArrayList<>();
        for(int i=0;i<iCores;i++){
            coreSwitches.add(new CoreSwitch(i));
        }
        aggreSwitches=new ArrayList<>();
        for(int i=0;i<iAggres;i++){
            aggreSwitches.add(new AggreSwitch(i));
        }
        racks=new ArrayList<>();
        hosts=new ArrayList<>();



        for(int i=0;i<iRacks;i++){
            Rack rack=new Rack(i,density);
            List<Host> hostsInRack=new ArrayList<>();
            for(int j=0;j<density;j++){
                Host host=new Host("r"+i+"h"+j);
                hostsInRack.add(host);
            }
            //determine who want to talk
            {
                Collections.shuffle(hostsInRack,rnd);
                for(int who=0;who< numWhoWantToTalk;who++){
                    hostsInRack.get(who).setWantToTalk(true);
                }
            }
            hosts.add(hostsInRack);
            rack.addHosts(hostsInRack);
            racks.add(rack);
        }
    }

    public FatTree(){
        this(4,4,4);
    }
    private void createTopo(){
        int end=iPod/2;
        //connect core to aggre;
        for(int x=0;x<iAggres;x+=end){
            for(int i=0;i<end;i++){
                for(int j=0;j<end;j++){
                    CoreSwitch coreSwitch=coreSwitches.get(i*end+j);
                    AggreSwitch aggreSwitch=aggreSwitches.get(x+i);
                    coreSwitch.connectToAggr(aggreSwitch);
                    links.add(new DirectedLink(coreSwitch,aggreSwitch));
                    links.add(new DirectedLink(aggreSwitch,coreSwitch));
                    iLinks++;
                }
            }
        }

        //connect rack to aggres

        for(int x=0;x<iAggres;x+=end){
            for(int i=0;i<end;i++){
                for(int j=0;j<end;j++){
                    AggreSwitch aggreSwitch=aggreSwitches.get(x+i);
                    Rack rack=racks.get(x+j);
                    rack.connectToAggre(aggreSwitch);
                    links.add(new DirectedLink(aggreSwitch,rack.getRackSwitch()));
                    links.add(new DirectedLink(rack.getRackSwitch(),aggreSwitch));
                    iLinks=iLinks+1;
                }
            }
        }

        int flows=iRacks* numWhoWantToTalk *(iRacks-1)* numWhoWantToTalk;
        routingMatrix=new int[links.size()][flows];
    }

    @Override
    public String toString() {
        return "FatTree{" +
                "K=" + K +
                ", iCores=" + iCores +
                ", iAggres=" + iAggres +
                ", iHosts=" + iHosts +
                ", iPod=" + iPod +
                ", iRacks=" + iRacks +
                ", density=" + density +
//                ", iLinks=" + iLinks +
//                ", Hosts="+hosts+
                ", numWhoWantToTalk=" + numWhoWantToTalk +
                '}';
    }


    /**
     * Get a random number between a and b,inclusively
     * @param a
     * @param b
     * @return
     */
    private int  getRandom(int a,int b){
        return rnd.nextInt(Math.abs(b-a))+Math.min(a,b)+1;
    }


    private int isIn(DirectedLink link,Flow flow){
        iNode[] path=flow.getPath();
        for(int i=0;i<path.length-1;i++){
            if(path[i]==null || path[i+1]==null) return 0;
            if(path[i].toString().equals( link.getFrom().toString())&&path[i+1].toString().equals(link.getTo().toString()))
                return 1;
        }
        return 0;
    }
}
