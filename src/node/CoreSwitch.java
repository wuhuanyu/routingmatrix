package node;

import java.util.ArrayList;
import java.util.List;

public class CoreSwitch extends Switch{

    private List<AggreSwitch> connectedAggres=new ArrayList<>();


    public void connectToAggr(AggreSwitch aggreSwitch){
        if(!connectedAggres.contains(aggreSwitch)){
            connectedAggres.add(aggreSwitch);
            aggreSwitch.connectToCore(this);
        }
    }

    public CoreSwitch(int id){
        this.id=id;
    }
    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return "c";
    }
}
