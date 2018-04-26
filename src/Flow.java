import node.Host;
import node.iNode;

public class Flow {
    private Host  src;
    private Host dest;

    private iNode[] path=new iNode[5];

    public iNode[] getPath() {
        return path;
    }

    public Flow(Host src, Host dest){
       this.src=src;
       this.dest=dest;
    }

    public Host getDest() {
        return dest;
    }

    public Flow setDest(Host dest) {
        this.dest = dest;
        return this;
    }

    public Host getSrc() {
        return src;
    }

    public Flow setSrc(Host src) {
        this.src = src;
        return this;
    }

    public void addToPath(int idx, iNode iNode){
        this.path[idx]=iNode;
    }

}
