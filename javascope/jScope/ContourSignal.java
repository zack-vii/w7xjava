package jscope;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Vector;
import debug.DEBUG;

public class ContourSignal{
    static final int   CASE_A          = 0;
    static final int   CASE_B          = 1;
    static final int   CASE_C          = 2;
    static final int   CASE_D          = 3;
    private static int rPoint_A[]      = {0, 1, 3};
    private static int rPoint_B[]      = {0, 2, 2};
    private static int rPoint_C[]      = {0, 1, 2};
    private static int rPoint_D[]      = {1, 1, 2};
    private static int succCase_A[]    = {ContourSignal.CASE_C, ContourSignal.CASE_A, ContourSignal.CASE_B};
    private static int succCase_B[]    = {ContourSignal.CASE_A, ContourSignal.CASE_B, ContourSignal.CASE_D};
    private static int succCase_C[]    = {ContourSignal.CASE_D, ContourSignal.CASE_C, ContourSignal.CASE_A};
    private static int succCase_D[]    = {ContourSignal.CASE_B, ContourSignal.CASE_D, ContourSignal.CASE_C};
    private static int xNear_A[]       = {0, 1, 1, 0};
    private static int xNear_B[]       = {1, 1, 0, 0};
    private static int xNear_C[]       = {0, 0, 1, 1};
    private static int xNear_D[]       = {0, -1, -1, 0};
    private static int yNear_A[]       = {0, 0, 1, 1};
    private static int yNear_B[]       = {0, 1, 1, 0};
    private static int yNear_C[]       = {0, -1, -1, 0};
    private static int yNear_D[]       = {0, 0, -1, -1};
    boolean            automaticLimits = true;
    // private boolean equalZ1;
    private boolean    equalZ2;
    double             x[];
    private boolean    xflag[][];
    double             xmin            = -1, xmax = 1;
    float              y[];
    double             ymin            = -1, ymax = 1;
    float              z[][];
    double             zmin            = -1, zmax = 1;

    ContourSignal(final double x[], final float y[], final float z[][]){
        if(x.length != z.length || y.length != z[0].length){ throw(new IllegalArgumentException("Z colum must be equals to x element end Z row to y elements")); }
        this.x = x;
        this.y = y;
        this.z = z;
        this.computeMinMax();
    }

    // private boolean edge = false;
    ContourSignal(final Signal s){
        if(s.getType() == Signal.TYPE_2D){
            this.setMinMaxX(s.getX2Dmin(), s.getX2Dmax());
            this.setMinMaxY(s.getY2Dmin(), s.getY2Dmax());
            this.setMinMaxZ(s.getZ2Dmin(), s.getZ2Dmax());
            this.x = s.getX2D();
            this.y = s.getY2D();
            this.z = s.getZ2D();
        }
    }

    private final boolean checkIntersection(final double level, final double z1, final double z2) {
        boolean out;
        out = (z1 < level && level < z2) || (z2 < level && level < z1) || (this.equalZ2 = (level == z2));
        return out;
    }

    private void computeMinMax() {
        this.xmin = this.xmax = this.x[0];
        this.ymin = this.ymax = this.y[0];
        this.zmin = this.zmax = this.z[0][0];
        for(int i = 0; i < this.x.length; i++){
            if(this.x[i] < this.xmin) this.xmin = this.x[i];
            if(this.x[i] > this.xmax) this.xmax = this.x[i];
            for(int j = 0; j < this.z[0].length; j++){
                if(this.z[i][j] < this.zmin) this.zmin = this.z[i][j];
                if(this.z[i][j] > this.zmax) this.zmax = this.z[i][j];
            }
        }
        for(final float element : this.y){
            if(element < this.ymin) this.ymin = element;
            if(element > this.ymax) this.ymax = element;
        }
        this.equalCases();
    }

    public Vector<Vector<Point2D.Double>> contour(final double level) {
        final Vector<Vector<Point2D.Double>> contours = new Vector<Vector<Point2D.Double>>();
        Vector<Point2D.Double> contour = new Vector<Point2D.Double>();
        double x1, y1, z1;
        double x2, y2, z2;
        double xc, yc, c1;
        int xNear[] = null;
        int yNear[] = null;
        int rPoint[] = null;
        int succCase[] = null;
        Point2D.Double firstCPoint = new Point2D.Double();
        Point2D.Double currCPoint = new Point2D.Double();
        this.xflag = new boolean[this.x.length][this.y.length];
        int edgeCase = ContourSignal.CASE_A;
        int ri = 0;
        int rj = 0;
        final int maxIteractions = this.x.length * this.y.length;
        for(int i = 0; i < this.x.length; i++){
            for(int j = 0; j < this.y.length - 1; j++){
                if(this.xflag[i][j]) continue;
                x1 = this.x[i];
                y1 = this.y[j];
                z1 = this.z[i][j];
                x2 = this.x[i];
                y2 = this.y[j + 1];
                z2 = this.z[i][j + 1];
                if(this.checkIntersection(level, z1, z2)){
                    c1 = (level - z1) / (z2 - z1);
                    xc = x1 + (x2 - x1) * c1;
                    yc = y1 + (y2 - y1) * c1;
                    contour.addElement((firstCPoint = new Point2D.Double(xc, yc)));
                    edgeCase = ContourSignal.CASE_A;
                    ri = i;
                    rj = j;
                    if(this.equalZ2){
                        try{
                            this.xflag[i][j - 1] = true;
                            this.xflag[i][j] = true;
                        }catch(final Exception exc){}
                    }
                }else continue;
                boolean contourCompleted = false;
                int l;
                int numIteractions = 0;
                while(!contourCompleted){
                    do{
                        try{
                            switch(edgeCase){
                                case CASE_A:
                                    if(DEBUG.D) System.out.println("CASE_A");
                                    xNear = ContourSignal.xNear_A;
                                    yNear = ContourSignal.yNear_A;
                                    rPoint = ContourSignal.rPoint_A;
                                    succCase = ContourSignal.succCase_A;
                                    this.xflag[ri][rj] = true;
                                    break;
                                case CASE_B:
                                    if(DEBUG.D) System.out.println("CASE_B");
                                    xNear = ContourSignal.xNear_B;
                                    yNear = ContourSignal.yNear_B;
                                    rPoint = ContourSignal.rPoint_B;
                                    succCase = ContourSignal.succCase_B;
                                    break;
                                case CASE_C:
                                    if(DEBUG.D) System.out.println("CASE_C");
                                    xNear = ContourSignal.xNear_C;
                                    yNear = ContourSignal.yNear_C;
                                    rPoint = ContourSignal.rPoint_C;
                                    succCase = ContourSignal.succCase_C;
                                    break;
                                case CASE_D:
                                    if(DEBUG.D) System.out.println("CASE_D");
                                    xNear = ContourSignal.xNear_D;
                                    yNear = ContourSignal.yNear_D;
                                    rPoint = ContourSignal.rPoint_D;
                                    succCase = ContourSignal.succCase_D;
                                    this.xflag[ri][rj - 1] = true;
                                    break;
                                default:
                                    throw new Exception("invalid edgeCase: " + edgeCase);
                            }
                            int rri = 0;
                            int rrj = 0;
                            for(l = 0; l < 3; l++){
                                rri = ri + xNear[l];
                                rrj = rj + yNear[l];
                                x1 = this.x[rri];
                                y1 = this.y[rrj];
                                z1 = this.z[rri][rrj];
                                final int rrii = ri + xNear[l + 1];
                                final int rrjj = rj + yNear[l + 1];
                                x2 = this.x[rrii];
                                y2 = this.y[rrjj];
                                z2 = this.z[rrii][rrjj];
                                if(DEBUG.D) System.out.print("[" + (ri + xNear[l]) + "," + (rj + yNear[l]) + "] " + " [" + (ri + xNear[l + 1]) + "," + (rj + yNear[l + 1]) + "] " + l);
                                if(this.checkIntersection(level, z1, z2)){
                                    if(this.equalZ2){
                                        try{
                                            this.xflag[rrii][rrjj - 1] = true;
                                            this.xflag[rrii][rrjj] = true;
                                        }catch(final Exception exc){}
                                    }
                                    c1 = (level - z1) / (z2 - z1);
                                    xc = x1 + (x2 - x1) * c1;
                                    yc = y1 + (y2 - y1) * c1;
                                    contour.addElement((currCPoint = new Point2D.Double(xc, yc)));
                                    ri += xNear[rPoint[l]];
                                    rj += yNear[rPoint[l]];
                                    edgeCase = succCase[l];
                                    break;
                                }
                            }
                            if(l == 3){
                                System.err.println("Error creating contour of level " + level);
                                currCPoint = firstCPoint;
                            }
                        }catch(final IOException exc){}catch(final Exception exc){
                            if(DEBUG.D) System.err.println("Exception");
                            /*
                             * When a contour line exits the grid is an exception occurs that I manage by seeking on the edge
                             * where it falls within the curve and then taking up the search for the contour points.
                             */
                            boolean found = false;
                            int xi, yj;
                            int border;
                            for(border = 0; border < 4 && !found; border++){
                                switch(edgeCase){
                                    case CASE_B:
                                        yj = this.y.length - 1;
                                        for(xi = ri; xi > 0; xi--){
                                            x2 = this.x[xi];
                                            y2 = this.y[yj];
                                            z2 = this.z[xi][yj];
                                            x1 = this.x[xi - 1];
                                            y1 = this.y[yj];
                                            z1 = this.z[xi - 1][yj];
                                            if(this.checkIntersection(level, z1, z2)){
                                                found = true;
                                                ri = xi - 1;
                                                rj = yj;
                                                edgeCase = ContourSignal.CASE_C;
                                                if(DEBUG.D) System.out.println("CASE B found. continue CASE C.");
                                                break;
                                            }
                                        }
                                        /*
                                         * I do not find any point on the upper side should I look for a point
                                         * on the side edge CASE_A I have from the first point as opposed to the generic
                                         * case where I have to start from the point next to the segment in which was
                                         * identified the exit point of the contour in examination, similar considerations apply to other cases.
                                         */
                                        if(!found){
                                            if(DEBUG.D) System.out.println("CASE B not found. continue CASE D.");
                                            edgeCase = ContourSignal.CASE_D;
                                            rj = this.y.length - 1;
                                        }
                                        break;
                                    case CASE_A:
                                        xi = this.x.length - 1;
                                        for(yj = rj + 1; yj < this.y.length - 1; yj++){
                                            x1 = this.x[xi];
                                            y1 = this.y[yj];
                                            z1 = this.z[xi][yj];
                                            x2 = this.x[xi];
                                            y2 = this.y[yj + 1];
                                            z2 = this.z[xi][yj + 1];
                                            this.xflag[xi][yj] = true;
                                            if(this.checkIntersection(level, z1, z2)){
                                                found = true;
                                                ri = xi;
                                                rj = yj + 1;
                                                edgeCase = ContourSignal.CASE_D;
                                                if(DEBUG.D) System.out.println("CASE A found. continue CASE D.");
                                                break;
                                            }
                                        }
                                        if(!found){
                                            if(DEBUG.D) System.out.println("CASE A not found. continue CASE B.");
                                            edgeCase = ContourSignal.CASE_B;
                                            ri = this.x.length - 1;
                                        }
                                        break;
                                    case CASE_C:
                                        yj = 0;
                                        // for (xi = ri - 1; xi >= 0; xi--)
                                        for(xi = ri + 1; xi < this.x.length - 1; xi++){
                                            x1 = this.x[xi];
                                            y1 = this.y[yj];
                                            z1 = this.z[xi][yj];
                                            x2 = this.x[xi + 1];
                                            y2 = this.y[yj];
                                            z2 = this.z[xi + 1][yj];
                                            if(this.checkIntersection(level, z1, z2)){
                                                found = true;
                                                ri = xi;
                                                rj = yj;
                                                edgeCase = ContourSignal.CASE_B;
                                                if(DEBUG.D) System.out.println("CASE C found. continue CASE B.");
                                                break;
                                            }
                                        }
                                        if(!found){
                                            if(DEBUG.D) System.out.println("CASE C not found. continue CASE D.");
                                            edgeCase = ContourSignal.CASE_A;
                                            rj = -1;
                                        }
                                        break;
                                    case CASE_D:
                                        xi = 0;
                                        for(yj = rj - 1; yj > 0; yj--){
                                            x1 = this.x[xi];
                                            y1 = this.y[yj];
                                            z1 = this.z[xi][yj];
                                            x2 = this.x[xi];
                                            y2 = this.y[yj - 1];
                                            z2 = this.z[xi][yj - 1];
                                            this.xflag[xi][yj] = true;
                                            if(this.checkIntersection(level, z1, z2)){
                                                found = true;
                                                ri = xi;
                                                rj = yj - 1;
                                                edgeCase = ContourSignal.CASE_A;
                                                if(DEBUG.D) System.out.println("CASE D not found. continue CASE A.");
                                                break;
                                            }
                                        }
                                        if(!found){
                                            if(DEBUG.D) System.out.println("CASE D not found. continue CASE B.");
                                            edgeCase = ContourSignal.CASE_C;
                                            ri = -1;
                                        }
                                        break;
                                }
                            }
                            /*
                             * To properly handle the contour lines as they exit the grid as broken curves
                             * I memorize every single broken separately to avoid being plot are joined
                             * with a segment of the exit points from the grid in the examined contour.
                             */
                            if(contour.size() >= 2){
                                contours.addElement(contour);
                                contour = new Vector<Point2D.Double>();
                            }else{
                                contour.clear();
                            }
                            c1 = (level - z1) / (z2 - z1);
                            xc = x1 + (x2 - x1) * c1;
                            yc = y1 + (y2 - y1) * c1;
                            contour.addElement((currCPoint = new Point2D.Double(xc, yc)));
                            if(!found && border == 4){
                                if(DEBUG.D) System.out.println("Edge completed.");
                                numIteractions = maxIteractions;
                            }
                        }
                        numIteractions++;
                        if(numIteractions > maxIteractions) break;
                    }
                    /*
                     * The contour line is deemed complete when you return to the starting point.
                     */
                    while(!(currCPoint.equals(firstCPoint)));
                    if(contour.size() >= 2){
                        contours.addElement(contour);
                        contour = new Vector<Point2D.Double>();
                    }else contour.clear();
                    contourCompleted = true;
                }
            }
        }
        this.xflag = null;
        return contours;
    }

    private void equalCases() {
        if(this.xmax == this.xmin){
            this.xmin -= this.xmax / 10.f;
            this.xmax += this.xmax / 10.f;
        }
        if(this.ymax == this.ymin){
            this.ymin -= this.ymax / 10.f;
            this.ymax += this.ymax / 10.f;
        }
        if(this.zmax == this.zmin){
            this.zmin -= this.zmax / 10.f;
            this.zmax += this.zmax / 10.f;
        }
    }

    public void setMinMax(final float xmin, final float xmax, final float ymin, final float ymax, final float zmin, final float zmax) {
        this.setMinMaxX(xmin, xmax);
        this.setMinMaxY(ymin, ymax);
        this.setMinMaxZ(zmin, zmax);
    }

    public void setMinMaxX(final double xmin, final double xmax) {
        this.xmin = xmin;
        this.xmax = xmax;
    }

    public void setMinMaxY(final double ymin, final double ymax) {
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public void setMinMaxZ(final double zmin, final double zmax) {
        this.zmin = zmin;
        this.zmax = zmax;
    }
}
