import node.iNode;

public class DirectedLink {
    private iNode from;
    private iNode to;


    public DirectedLink(iNode from, iNode to) {
        this.from = from;
        this.to = to;
    }

    public iNode getFrom() {
        return from;
    }
    public iNode getTo(){
        return to;
    }

    public DirectedLink setFrom(iNode from) {
        this.from = from;
        return this;
    }

    @Override
    public String toString() {
        return from.toString()+"-"+to.toString();
    }
}
