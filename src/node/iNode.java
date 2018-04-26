package node;

import java.util.Objects;

public abstract class iNode {
    protected int id;
    protected String type;

    public abstract int getId();
    public abstract String getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof iNode)) return false;
        iNode iNode = (iNode) o;
        return getId() == iNode.getId() &&
                Objects.equals(getType(), iNode.getType());
    }

    public String toString(){
        return getType()+getId();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getType());
    }
}
