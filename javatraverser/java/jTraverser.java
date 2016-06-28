import jtraverser.jTraverserFacade;

public class jTraverser{
    public static void main(final String args[]) {
        if(args.length >= 4) new jTraverserFacade(args[0], args[1], args[2], args[3]);
        else if(args.length == 3) new jTraverserFacade(args[0], args[1], args[2], null);
        else if(args.length == 2) new jTraverserFacade(args[0], args[1], null, null);
        else if(args.length == 1) new jTraverserFacade(args[0], null, null, null);
        else new jTraverserFacade(null, null, null, null);
    }
}
