package jtraverser.dialogs;

import jtraverser.TreeManager;

public final class Dialogs{
    public final AddNode addNode;
    public final Flags   flags;
    public final Rename  rename;

    public Dialogs(final TreeManager treeman){
        this.flags = new Flags(treeman);
        this.rename = new Rename(treeman);
        this.addNode = new AddNode(treeman);
    }

    public final void update() {
        this.flags.update();
        this.rename.update();
    }
}
