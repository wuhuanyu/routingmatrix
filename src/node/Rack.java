package node;

import java.util.ArrayList;
import java.util.List;

public class Rack {
    private int density;
    private List<Host> hosts;
    private RackSwitch rackSwitch;

    private int id;
    public Rack(int id,int density){
        this.density=density;
        this.id=id;
        hosts=new ArrayList<>();
        rackSwitch=new RackSwitch(id);
    }

    public void addHosts(List<Host> hosts){
        this.hosts=hosts;
        for (Host host:
                hosts
             ) {
            host.connectToRackSwitch(rackSwitch);
        }
    }

    public RackSwitch getRackSwitch(){
        return this.rackSwitch;
    }

    public void connectToAggre(AggreSwitch aggreSwitch){
        rackSwitch.connectToAggreSwitch(aggreSwitch);
        aggreSwitch.connectToRack(rackSwitch);
    }
}
