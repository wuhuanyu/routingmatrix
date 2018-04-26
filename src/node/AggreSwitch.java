package node;

import java.util.ArrayList;
import java.util.List;

public class AggreSwitch extends Switch {

    private List<CoreSwitch> connectedCores=new ArrayList<>();
    private List<RackSwitch> rackSwitches=new ArrayList<>();



    public void connectToCore(CoreSwitch coreSwitch){
        if(!this.connectedCores.contains(coreSwitch)){
            connectedCores.add(coreSwitch);
            coreSwitch.connectToAggr(this);
        }
    }

    public void connectToRack(RackSwitch rackSwitch){
        if(!rackSwitches.contains(rackSwitch)){
            this.rackSwitches.add(rackSwitch);
            rackSwitch.connectToAggreSwitch(this);
        }
    }

    public AggreSwitch(int id){
        this.id=id;
    }



    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return "a";
    }
}
