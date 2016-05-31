package jTraverser.dialogs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.mdsip.Connection;

/**
 * @author Rodrigo
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel{
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    public static void main(final String[] args) throws MdsException {// TODO
        final Connection con = new Connection("mds-data-1");
        con.connectToMds(false);
        System.out.println(con.mdsValue("GetMsg(TreeOpen('QSS',160310007))"));
        final Descriptor sig = con.mdsValue("DATA.FILTERSCOPE:T16_C_II");
        final JFrame frame = new JFrame("DrawGraph");
        frame.getContentPane().add(GraphPanel.plotDescriptor(sig));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static final GraphPanel plotDescriptor(final Descriptor sig) {
        if(sig == null) return new GraphPanel();
        final float[] fl = sig.toFloat();
        if(fl == null) return new GraphPanel();
        final GraphPanel mainPanel = new GraphPanel();
        final int imax = (int)mainPanel.getPreferredSize().getWidth();
        final int len;
        final double p = (fl.length) / imax * 2.;
        double v = p;
        if(fl.length > imax){
            len = imax;
        }else len = fl.length;
        final List<Float> scores = new ArrayList<Float>(len);
        int i = 0;
        float max = Float.NEGATIVE_INFINITY, min = Float.POSITIVE_INFINITY;
        for(final float f : fl){
            if(min > f) min = f;
            if(max < f) max = f;
            if(i++ >= v){
                v += p;
                scores.add(min);
                scores.add(max);
                max = Float.NEGATIVE_INFINITY;
                min = Float.POSITIVE_INFINITY;
            }
        }
        mainPanel.setScores(scores);
        return mainPanel;
    }
    private final Color gridColor        = new Color(200, 200, 200, 200);
    private final int   heigth           = 400;
    private final int   labelPadding     = 25;
    private final Color lineColor        = new Color(44, 102, 230, 180);
    private final int   numberYDivisions = 10;
    private final int   padding          = 25;
    private final Color pointColor       = new Color(100, 100, 100, 180);
    private final int   pointWidth       = 4;
    private List<Float> scores;
    private final int   width            = 800;

    public GraphPanel(){
        this(null);
    }

    public GraphPanel(final List<Float> scores){
        this.scores = scores;
    }

    private float getMaxScore() {
        float maxScore = Float.MIN_VALUE;
        for(final Float score : this.scores){
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    private float getMinScore() {
        float minScore = Float.MAX_VALUE;
        for(final Float score : this.scores){
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.width, this.heigth);
    }

    public List<Float> getScores() {
        return this.scores;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if(this.scores == null) return;
        final Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final float xScale = ((float)this.getWidth() - (2 * this.padding) - this.labelPadding) / (this.scores.size() - 1);
        final float yScale = ((float)this.getHeight() - 2 * this.padding - this.labelPadding) / (this.getMaxScore() - this.getMinScore());
        final List<Point> graphPoints = new ArrayList<Point>();
        for(int i = 0; i < this.scores.size(); i++){
            final int x1 = (int)(i * xScale + this.padding + this.labelPadding);
            final int y1 = (int)((this.getMaxScore() - this.scores.get(i)) * yScale + this.padding);
            graphPoints.add(new Point(x1, y1));
        }
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(this.padding + this.labelPadding, this.padding, this.getWidth() - (2 * this.padding) - this.labelPadding, this.getHeight() - 2 * this.padding - this.labelPadding);
        g2.setColor(Color.BLACK);
        // create hatch marks and grid lines for y axis.
        for(int i = 0; i < this.numberYDivisions + 1; i++){
            final int x0 = this.padding + this.labelPadding;
            final int x1 = this.pointWidth + this.padding + this.labelPadding;
            final int y0 = this.getHeight() - ((i * (this.getHeight() - this.padding * 2 - this.labelPadding)) / this.numberYDivisions + this.padding + this.labelPadding);
            final int y1 = y0;
            if(this.scores.size() > 0){
                g2.setColor(this.gridColor);
                g2.drawLine(this.padding + this.labelPadding + 1 + this.pointWidth, y0, this.getWidth() - this.padding, y1);
                g2.setColor(Color.BLACK);
                final String yLabel = ((int)((this.getMinScore() + (this.getMaxScore() - this.getMinScore()) * ((i * 1.0f) / this.numberYDivisions)) * 100)) / 100.0f + "";
                final FontMetrics metrics = g2.getFontMetrics();
                final int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }
        // and for x axis
        for(int i = 0; i < this.scores.size(); i++){
            if(this.scores.size() > 1){
                final int x0 = i * (this.getWidth() - this.padding * 2 - this.labelPadding) / (this.scores.size() - 1) + this.padding + this.labelPadding;
                final int x1 = x0;
                final int y0 = this.getHeight() - this.padding - this.labelPadding;
                final int y1 = y0 - this.pointWidth;
                if((i % ((int)((this.scores.size() / 20.0f)) + 1)) == 0){
                    g2.setColor(this.gridColor);
                    g2.drawLine(x0, this.getHeight() - this.padding - this.labelPadding - 1 - this.pointWidth, x1, this.padding);
                    g2.setColor(Color.BLACK);
                    final String xLabel = i + "";
                    final FontMetrics metrics = g2.getFontMetrics();
                    final int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }
        // create x and y axes
        g2.drawLine(this.padding + this.labelPadding, this.getHeight() - this.padding - this.labelPadding, this.padding + this.labelPadding, this.padding);
        g2.drawLine(this.padding + this.labelPadding, this.getHeight() - this.padding - this.labelPadding, this.getWidth() - this.padding, this.getHeight() - this.padding - this.labelPadding);
        final Stroke oldStroke = g2.getStroke();
        g2.setColor(this.lineColor);
        g2.setStroke(GraphPanel.GRAPH_STROKE);
        for(int i = 0; i < graphPoints.size() - 1; i++){
            final int x1 = graphPoints.get(i).x;
            final int y1 = graphPoints.get(i).y;
            final int x2 = graphPoints.get(i + 1).x;
            final int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }
        g2.setStroke(oldStroke);
        g2.setColor(this.pointColor);
        for(int i = 0; i < graphPoints.size(); i++){
            final int x = graphPoints.get(i).x - this.pointWidth / 2;
            final int y = graphPoints.get(i).y - this.pointWidth / 2;
            final int ovalW = this.pointWidth;
            final int ovalH = this.pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    public void setScores(final List<Float> scores) {
        this.scores = scores;
        this.invalidate();
        this.repaint();
    }
}