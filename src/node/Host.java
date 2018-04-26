package node;

public class Host  {
    private Rack rack;
    private String id;
    private RackSwitch rackSwitch;

    public Host setId(String id) {
        this.id = id;
        return this;
    }

    public RackSwitch getRackSwitch() {
        return rackSwitch;
    }

    public Host setRackSwitch(RackSwitch rackSwitch) {
        this.rackSwitch = rackSwitch;
        return this;
    }

    public Rack getRack() {

        return rack;
    }

    private boolean isWantToTalk=false;
    public void setRack(Rack rack){
        this.rack=rack;
    }


    public void connectToRackSwitch(RackSwitch rackSwitch){
        this.rackSwitch=rackSwitch;
        rackSwitch.connectToHost(this);
    }

    public Host(String id){
        this.id=id;
    }

    public String toString(){
        return id;
    }

    public boolean isWantToTalk() {
        return isWantToTalk;
    }

    public Host setWantToTalk(boolean wantToTalk) {
        isWantToTalk = wantToTalk;
        return this;
    }

    public String getId(){
        return id;
    }

    public String getType() {
        return "host";
    }
}
