package node;

import java.util.ArrayList;
import java.util.List;

public class RackSwitch extends Switch {
    List<Host> hosts;
    List<AggreSwitch> aggreSwitches=new ArrayList<>();


    public boolean isConnectedToAggreSwitch(AggreSwitch a){
        return aggreSwitches.contains(a);
    }

    public RackSwitch(int id){
        this.id=id;
        hosts=new ArrayList<>();
    }



    public void connectToAggreSwitch(AggreSwitch aggreSwitch){
        if(!aggreSwitches.contains(aggreSwitch)){
            aggreSwitches.add(aggreSwitch);
            aggreSwitch.connectToRack(this);
        }
    }

    public void connectToHost(Host host){
        if(!hosts.contains(host)){
            this.hosts.add(host);
            host.connectToRackSwitch(this);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getType() {
        return "r";
    }

}
