package jtraverser.dialogs;

import jtraverser.TreeManager;

public final class Dialogs{
    public final AddNode addNode;
    public final ModifyFlags   modifyFlags;
    public final Rename  rename;

    public Dialogs(final TreeManager treeman){
        this.modifyFlags = new ModifyFlags(treeman);
        this.rename = new Rename(treeman);
        this.addNode = new AddNode(treeman);
    }

    public final void update() {
        this.modifyFlags.update();
        this.rename.update();
    }
}
