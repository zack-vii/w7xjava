package jScope;

/* $Id$ */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * RowColumnLayout is a layout manager that dispose added component in
 * a grid defined by number of column and number of components in column.
 * Column width and component height can be modified by pointer
 * interaction. An Example explain how to use it:
 *
 * <pre>
 * <code>
 * class RowColumnExample extends Panel {
 *
 *    protected RowColumnLayout    row_col_layout;
 *    protected int rows[] = {2, 2};
 *
 *    //Resize button class
 *    class ResizeButton extends Canvas {
 *
 *             ResizeButton()
 *             {
 *                 setBackground(Color.lightGray);
 *                 setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
 *             }
 *
 *             public void paint(Graphics g)
 *             {
 *                 Rectangle d = getBounds();
 *                 g.draw3DRect(0, 0, d.width-1, d.height-1, true);
 *                 g.dispose();
 *             }
 *             public void print(Graphics g){}
 *             public void printAll(Graphics g){}
 *     }
 *
 *
 *     public RowColumnExample()
 *     {
 *           row_col_layout = new RowColumnLayout(rows);
 *           setLayout(row_col_layout);
 *
 *         //The number of component to add to panel are 4 (2 column each colum 2 component)
 *         //Before add component we must add num_component - 1 canvas objet that are used
 *         //to interact with layout manager for interactive resize of width and height of
 *         //the grid
 *           ResizeButton b;
 *           for(int i = 0; i < 4 - 1; i++) {
 *                 add(b = new ResizeButton());
 *                 b.addMouseListener(new MouseAdapter()
 *                   {
 *   //Action used to resize column width or component height
 *                      public  void mouseReleased(MouseEvent e)
 *                      {
 *                           Component ob = e.getComponent();
 *                           if(ob instanceof ResizeButton)
 *                                 row_col_layout.ResizeRowColumn(ob, e.getPoint().x, e.getPoint().y);
 *                      }
 *                   });
 *                 b.addMouseMotionListener(new MouseMotionAdapter()
 *                   {
 *   //Action used to draw resiaze line on the component, only if component impement double buffering
 *                      public  void mouseDragged(MouseEvent e)
 *                      {
 *                          Component ob = e.getComponent();
 *                          row_col_layout.DrawResize(ob, e.getPoint().x, e.getPoint().y);
 *                      }
 *                   });
 *           }
 *
 *           for(int i = 0, k = 0; i < columns; i++)
 *           {
 *                 for(int j = 0; j < rows[i]; j++)
 *                 {
 *                        add(<component>);
 *                        k++;
 *                 }
 *           }
 *
 *           validate();
 *    }
 *  }
 *
 * </code>
 * </pre>
 *
 * @see RowColumnContainer
 */
final public class RowColumnLayout implements LayoutManager{
    /**
     * Horizontal mode item
     */
    static final int HORIZONTAL = 1;
    /**
     * None mode item
     */
    static final int NONE       = 3;
    /**
     * Vertical mode item
     */
    static final int VERTICAL   = 2;

    /**
     * Draw vertical line at x position on component
     *
     * @param x
     *            vertical line position
     * @param c
     *            Component on which draw line/s
     */
    private static void DrawXResize(final Component c, final int x) {
        // if(System.getProperty("java.version").indexOf("1.2") != -1 && !c.isDoubleBuffered())
        // return;
        // else
        if(!(c instanceof Waveform || c instanceof MultiWaveform)) return;
        final Dimension d = c.getSize();
        final Graphics g = c.getGraphics();
        c.paint(g);
        g.drawLine(x, 0, x, d.height);
        g.dispose();
    }

    /**
     * Draw horizontal n_lines lines firt at y position next at y+ith*space on component
     * where ith came form 1 to n_lines. Multi horizontal line must be draw when more
     * than 1 component is resized.
     *
     * @param y
     *            start y horizontal lines
     * @param n_line
     *            number of line to draw
     * @param space
     *            number of pixel between line
     * @param c
     *            component on which draw line/s
     */
    private static void DrawYResize(final Component c, final int y, final int n_line, final int space) {
        // if(System.getProperty("java.version").indexOf("1.2") != -1 && !c.isDoubleBuffered())
        // return;
        // else
        if(!(c instanceof Waveform || c instanceof MultiWaveform)) return;
        final Dimension d = c.getSize();
        final Graphics g = c.getGraphics();
        c.paint(g);
        for(int i = 0; i < n_line; i++){
            g.drawLine(0, y + i * space, d.width, y + i * space);
        }
        g.dispose();
    }
    /**
     * number of button resize component
     */
    private int       b_comp          = 0;
    /**
     * Canvas resize object x position
     */
    private final int bx_pos          = 25;
    /**
     * Canvas resize objet y position
     */
    private final int by_pos          = 20;
    /**
     * current culomn idx to be resized
     */
    private int       col_idx;
    /**
     * number of column
     */
    private int       column;
    /**
     * Current componet to be resized
     */
    private int       comp_idx;
    /**
     * Horizontal componet space in pixel
     */
    private final int hgap            = 0;
    /**
     * Boolean resize flag true if must be computed size vomponent
     */
    private boolean   init_resize     = true;
    /**
     * Parent component containr
     */
    private Container main_p;
    /**
     * maximum height dimension
     */
    private int       maxHeight       = 0;
    /**
     * maximum width dimension
     */
    private int       maxWidth        = 0;
    /**
     * Minimum component height size
     */
    private int       MIN_SIZE_H      = 10;
    /**
     * minimum column width
     */
    private int       MIN_SIZE_W      = 10;
    /**
     * minimum height dimension
     */
    private int       minHeight       = 0;
    /**
     * Minimum width dimension
     */
    private int       minWidth        = 0;
    /**
     * Vector that defined height components
     */
    private float     percent_height[];
    /**
     * Vector that defined width components
     */
    private float     percent_width[];
    /**
     * Current column y position
     */
    // private int pos_x;
    private int       pos_y;
    /**
     * preferred height size
     */
    private int       preferredHeight = 0;
    /**
     * preferred width size
     */
    private int       preferredWidth  = 0;
    /**
     * Internal variable
     */
    private int       prev_col_idx    = -1;
    /**
     * Current resize mode
     */
    private int       resize_mode;
    /**
     * Vector to define the number of component in column
     */
    private int       row[];
    /**
     * Unknow size boolean flag
     */
    private boolean   sizeUnknown     = true;
    /**
     * Vertical component space in pixel
     */
    private final int vgap            = 0;

    /**
     * Costruct a RowColumnLayout with a column number defined by _column
     * argument and number of object per culomn defined by row[] vector
     *
     * @param column
     *            Number of column
     * @param row
     *            row[i] define number of component in column i
     */
    public RowColumnLayout(final int[] row){
        this.SetRowColumn(row, null, null);
    }

    /**
     * Costruct a RowColumnLayout with a column number defined by _column
     * argument and number of object per column defined by row[] vector.
     * The width size of ith column is defined by pw[i] value and height size
     * of jth component is defuned by pw[j]. The pw[i] and ph[j] value are normalize
     * value in rangeform 0 to 1.
     *
     * @param row
     *            row[i] define number of component in column i
     * @param ph
     *            Vector of normalize height of component. The sum of ph[x] of the objects in a
     *            column must be 1.
     * @param pw
     *            Vector of normalize width of the culomn. The sum of pw[x] must be 1
     */
    public RowColumnLayout(final int[] row, final float ph[], final float pw[]){
        this.SetRowColumn(row, ph, pw);
    }

    /**
     * Do nothing
     * Required by LayoutManager.
     *
     * @param name
     *            Component name
     * @param comp
     *            Component
     */
    @Override
    public void addLayoutComponent(final String name, final Component comp) {
        // System.out.println("Add cmp");
    }

    /**
     * Method to draw orizontal or vertical line during resize componet or
     * column. Line/s are drow on the component only if double buffering
     * is supported on component. RowColumnLayout manager check double buffering
     * component capability by isDoubleBuffered() method of component.
     *
     * @param _b
     *            Canvas object used to interact with mouse drag
     * @param x
     *            Canvas x position
     * @param y
     *            Canvas y position
     * @see RowColumnComponet
     */
    public void DrawResize(final Component _b, final int x, final int y) {
        Component c = null, b;
        int idx = 0, k;
        boolean found;
        int pos;
        Rectangle r;
        int i, j, jj, num_line = 0, pos_y, n_draw, curr_height, start_pos;
        if(this.init_resize){
            this.init_resize = false;
            found = false;
            for(j = 0; j < this.row.length && !found; j++){// column && !found; j++) {
                for(i = 0; i < this.row[j]; i++){
                    b = this.main_p.getComponent(idx);
                    if(_b == b){
                        this.col_idx = j;
                        this.comp_idx = idx;
                        if(i == this.row[j] - 1) this.resize_mode = RowColumnLayout.HORIZONTAL;
                        else this.resize_mode = RowColumnLayout.VERTICAL;
                        found = true;
                        break;
                    }
                    idx++;
                }
            }
        }
        if(this.resize_mode == RowColumnLayout.VERTICAL){
            k = 0;
            for(i = 0; i < this.col_idx + 1; i++)
                k += this.row[i];
            if(y < 0){
                pos_y = pos = y;
                curr_height = 0;
                start_pos = _b.getBounds().y;
                k -= this.row[this.col_idx];
                c = this.main_p.getComponent(this.b_comp + this.comp_idx + 1);
                RowColumnLayout.DrawYResize(c, 0, 0, 0);
                for(j = 0, jj = 0, i = this.b_comp + this.comp_idx; i > this.b_comp + k - 1; i--, j++){
                    c = this.main_p.getComponent(i);
                    r = c.getBounds();
                    curr_height += r.height;
                    if(curr_height + pos < this.MIN_SIZE_H * (num_line + 1)) num_line++;
                    if(start_pos + pos + (k - this.comp_idx - 1) * this.MIN_SIZE_H <= 0) return;
                    for(n_draw = 0; jj < num_line + 1; jj++)
                        if(Math.abs(pos) + jj * this.MIN_SIZE_H < curr_height) n_draw++;
                        else break;
                    if(n_draw > 0){
                        pos_y = (r.height + pos) - (-curr_height + r.height + (jj - n_draw) * this.MIN_SIZE_H);
                        // ((RowColumnComponent)c).DrawYResize(c, pos_y - 2, n_draw, -MIN_SIZE_H);
                        RowColumnLayout.DrawYResize(c, pos_y - 2, n_draw, -this.MIN_SIZE_H);
                    }else RowColumnLayout.DrawYResize(c, 0, 0, 0);
                }
            }else{
                pos_y = pos = y;
                curr_height = 0;
                start_pos = _b.getBounds().y;
                c = this.main_p.getComponent(this.b_comp + this.comp_idx);
                // ((RowColumnComponent)c).DrawYResize(0, 0, 0);
                RowColumnLayout.DrawYResize(c, 0, 0, 0);
                for(j = 0, jj = 0, i = this.b_comp + this.comp_idx + 1; i < this.b_comp + k; i++, j++){
                    c = this.main_p.getComponent(i);
                    r = c.getBounds();
                    curr_height += r.height;
                    if(curr_height - pos < this.MIN_SIZE_H * (num_line + 1)) num_line++;
                    if(start_pos + pos + (k - this.comp_idx - 1) * this.MIN_SIZE_H > this.maxHeight) return;
                    for(n_draw = 0; jj < num_line + 1; jj++)
                        if(pos + jj * this.MIN_SIZE_H < curr_height) n_draw++;
                        else break;
                    if(n_draw > 0){
                        pos_y = pos - curr_height + r.height + (jj - n_draw) * this.MIN_SIZE_H;
                        // ((RowColumnComponent)c).DrawYResize(pos_y - 2, n_draw, MIN_SIZE_H);
                        RowColumnLayout.DrawYResize(c, pos_y - 2, n_draw, this.MIN_SIZE_H);
                    }else RowColumnLayout.DrawYResize(c, 0, 0, 0);
                }
            }
        }
        if(this.resize_mode == RowColumnLayout.HORIZONTAL){
            if(x < 0) idx = this.col_idx;
            else idx = this.col_idx + 1;
            if(this.prev_col_idx != -1 && this.prev_col_idx != idx){
                k = 0;
                for(i = 0; i < this.prev_col_idx; i++)
                    k += this.row[i];
                for(i = k; i < k + this.row[this.prev_col_idx]; i++){
                    c = this.main_p.getComponent(this.b_comp + i);
                    // ((RowColumnComponent)c).DrawXResize(0);
                    RowColumnLayout.DrawXResize(c, 0);
                }
            }
            this.prev_col_idx = idx;
            k = 0;
            for(i = 0; i < idx; i++)
                k += this.row[i];
            c = this.main_p.getComponent(this.b_comp + k);
            r = c.getBounds();
            if(x < 0){
                pos = r.width + x;
                if(pos < this.MIN_SIZE_W) pos = this.MIN_SIZE_W;
            }else{
                pos = x;
                if(r.width - pos < this.MIN_SIZE_W) pos = r.width - this.MIN_SIZE_W;
            }
            for(i = k; i < k + this.row[idx]; i++){
                c = this.main_p.getComponent(this.b_comp + i);
                // ((RowColumnComponent)c).DrawXResize(pos - 2);
                RowColumnLayout.DrawXResize(c, pos - 2);
            }
        }
    }

    /**
     * Return number of column
     *
     * @return Integer number of column
     */
    public int GetColumns() {
        return this.column;
    }

    public float[] getPercentHeight() {
        return this.percent_height;
    }

    /**
     * Return normalize height of i-th component
     *
     * @param i
     *            index of component
     * @return Normalize Height
     */
    public float getPercentHeight(final int i) {
        if(i < this.percent_height.length) return this.percent_height[i];
        return 0;
    }

    public float[] getPercentWidth() {
        return this.percent_width;
    }

    /**
     * Return width of i-th column
     *
     * @param i
     *            index of column
     * @return Normalize column width
     */
    public float getPercentWidth(final int i) {
        if(i < this.row.length) return this.percent_width[i];
        return 0;
    }

    /**
     * Return integer vector of number of component in each column
     *
     * @return Integer vector where i-th value is number of component in i-th column
     */
    public int[] GetRows() {
        return this.row;
    }

    /**
     * Required by LayoutManager.
     * This is called when the panel is first displayed,
     * and every time its size changes.
     * Note: You CAN'T assume preferredLayoutSize() or minimumLayoutSize()
     * will be called -- in the case of applets, at least, they probably
     * won't be.
     *
     * @param parent
     *            component container
     */
    @Override
    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        this.maxWidth = parent.getSize().width - (insets.left + insets.right);
        this.maxHeight = parent.getSize().height - (insets.top + insets.bottom);
        this.main_p = parent;
        if(this.maxWidth <= 0 || this.maxHeight <= 0) return;
        // Go through the components' sizes, if neither preferredLayoutSize()
        // nor minimumLayoutSize() has been called.
        if(this.sizeUnknown){
            this.setSizes(parent);
            this.sizeUnknown = false;
        }
        this.ResizeColumns(parent);
        for(int i = 0; i < this.row.length; i++)
            // column; i++)
            this.ResizeColumn(parent, i);
    }

    /**
     * Required by LayoutManager
     *
     * @param parent
     *            component container
     * @return component container dimension
     */
    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        final Dimension dim = new Dimension(0, 0);
        // Always add the container's insets!
        final Insets insets = parent.getInsets();
        dim.width = this.minWidth + insets.left + insets.right;
        dim.height = this.minHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    private int nextColumn(final int idx) {
        for(int i = idx + 1; i < this.row.length; i++)
            if(this.row[i] != 0) return i;
        return -1;
    }

    /**
     * Required by LayoutManager
     *
     * @param parent
     *            component container
     * @return component container dimension
     */
    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        final Dimension dim = new Dimension(0, 0);
        this.setSizes(parent);
        // Always add the container's insets!
        final Insets insets = parent.getInsets();
        dim.width = this.preferredWidth + insets.left + insets.right;
        dim.height = this.preferredHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }

    /**
     * Do nothing
     * Required by LayoutManager.
     *
     * @param comp
     *            component
     */
    @Override
    public void removeLayoutComponent(final Component comp) {
        // System.out.println("remove cmp");
    }

    /**
     * Set componet to a columns all with the same height, componet portion
     * of the parent width
     *
     * @param col
     *            Integer index of column
     */
    private void ResetHeight(final int col) {
        if(this.row != null && col < this.row.length){
            int k = 0;
            for(int i = 0; i < col; i++)
                k += this.row[i];
            if(this.percent_height != null && k + this.row[col] <= this.percent_height.length){
                for(int j = 0; j < this.row[col]; j++){
                    this.percent_height[k] = (float)1. / this.row[col];
                    k++;
                }
            }
        }
    }

    /**
     * Set to all column to the same width, column portion of the parent width
     */
    private void ResetWidth() {
        if(this.row != null && this.percent_width != null && this.percent_width.length >= this.row.length){
            for(int i = 0; i < this.row.length; i++){// column; i++)
                if(this.row[i] == 0) continue;
                this.percent_width[i] = (float)1. / this.column;
            }
        }
    }

    /**
     * Resize column height component
     *
     * @param parent
     *            component container
     * @param col_idx
     *            column index
     */
    private void ResizeColumn(final Container parent, final int col_idx) {
        int k = 0, y = 0;
        int previousHeight = 0, currMaxHeight;
        for(int i = 0; i < col_idx; i++)
            k += this.row[i];
        currMaxHeight = this.maxHeight - (this.row[col_idx] - 1) * this.vgap;
        for(int j = 0; j < this.row[col_idx]; j++){
            final Component c = parent.getComponent(this.b_comp + k);
            if(c.isVisible()){
                final Dimension d = c.getSize();// c.getPreferredSize();
                y += previousHeight;
                d.height = (int)(currMaxHeight * this.percent_height[k]);
                if(j != this.row[col_idx] - 1) d.height = (int)(currMaxHeight * this.percent_height[k]);
                else d.height = this.maxHeight - y;
                c.setBounds(c.getBounds().x, y, c.getBounds().width, d.height);
                previousHeight = d.height + this.vgap;
                if(k < this.b_comp){
                    final Component b = parent.getComponent(k);
                    if(j == this.row[col_idx] - 1) b.setBounds(c.getBounds().x + c.getBounds().width - this.hgap - 2, this.maxHeight - this.bx_pos, 4, 8);
                    else{
                        int p = this.by_pos;
                        if(d.width < this.by_pos + 8) p = d.width - 1;
                        b.setBounds(c.getBounds().x + c.getBounds().width - p, y + d.height - 2, 8, 4);
                    }
                }
            }
            k++;
        }
    }

    /**
     * Resize component width in columns
     *
     * @param parent
     *            component container
     */
    private void ResizeColumns(final Container parent) {
        int k = 0, x = 0;
        int previousWidth = 0, currMaxWidth, currWidth;
        currMaxWidth = this.maxWidth - (this.column - 1) * this.hgap;
        int curr_col = 0;
        for(int i = 0; i < this.row.length; i++)// column; i++)
        {
            if(this.row[i] == 0) continue;
            currWidth = (int)(currMaxWidth * this.percent_width[i]);
            for(int j = 0; j < this.row[i]; j++){
                final Component c = parent.getComponent(this.b_comp + k);
                if(c.isVisible()){
                    final Dimension d = c.getSize();// c.getPreferredSize();
                    x = previousWidth;
                    d.width = currWidth;
                    // if(i != column - 1)
                    if(curr_col != this.column - 1) d.width = (int)(currMaxWidth * this.percent_width[i]);
                    else d.width = this.maxWidth - x;
                    c.setBounds(x, c.getBounds().y, d.width, c.getBounds().height);
                    if(k < this.b_comp){
                        final Component b = parent.getComponent(k);
                        if(j == this.row[i] - 1) b.setBounds(x + d.width - this.hgap - 2, this.maxHeight - this.bx_pos, 4, 8);
                        else{
                            int p = this.by_pos;
                            if(d.width < this.by_pos + 8) p = d.width - 1;
                            b.setBounds(x + d.width - p, c.getBounds().y + c.getBounds().height - 2, 8, 4);
                        }
                        b.invalidate();
                    }
                }
                k++;
            }
            previousWidth += currWidth + this.hgap;
            curr_col++;
        }
    }

    /**
     * Calculate equal space of column and equal width of component in
     * column
     *
     * @param _b
     *            Canvas object used to interact with mouse if _b is column resize
     *            canvas equal width culumn are performed if _b is component column resize
     *            all component in column are resized to equal height.
     */
    public void ResizeRowColumn(final Component _b) {
        Component b;
        int idx = 0;
        this.init_resize = true;
        for(int j = 0; j < this.row.length; j++){ // column; j++) {
            for(int i = 0; i < this.row[j]; i++){
                b = this.main_p.getComponent(idx);
                if(_b == b){
                    this.col_idx = j;
                    this.comp_idx = idx;
                    if(i == this.row[j] - 1) this.resize_mode = RowColumnLayout.HORIZONTAL;
                    else this.resize_mode = RowColumnLayout.VERTICAL;
                }
                idx++;
            }
        }
        if(this.resize_mode == RowColumnLayout.VERTICAL){
            this.ResetHeight(this.col_idx);
            this.ResizeColumn(this.main_p, this.col_idx);
        }
        if(this.resize_mode == RowColumnLayout.HORIZONTAL){
            this.ResetWidth();
            this.ResizeColumns(this.main_p);
        }
    }

    /**
     * Culumn width and component height resize on x or y value.
     *
     * @param _b
     *            Canvas object used to interact with mouse if _b is column resize
     *            canvas columns width are resize on x value if _b is componet column resize
     *            component height are resize on y value.
     * @param x
     *            x position
     * @param y
     *            y position
     */
    public void ResizeRowColumn(final Component _b, int x, int y) {
        int k = 0, currHeight = 0, curr_y = 0, curr_x = 0;
        int currMaxHeight, currMaxWidth;
        int max_y, max_x, min_x;
        int start_comp, end_comp, inc_comp;
        int new_y, resize_y, idx = 0;
        Component b, c;
        Rectangle d, d1;
        this.init_resize = true;
        this.prev_col_idx = -1;
        for(int j = 0; j < this.row.length; j++){ // column; j++) {
            for(int i = 0; i < this.row[j]; i++){
                b = this.main_p.getComponent(idx);
                if(_b == b){
                    c = this.main_p.getComponent(this.b_comp + idx);
                    d = c.getBounds();
                    d1 = b.getBounds();
                    this.col_idx = j;
                    this.comp_idx = idx;
                    // pos_x = d.x + d.width;
                    this.pos_y = d.y + d.height;
                    x += d1.x;
                    y += d1.y;
                    if(i == this.row[j] - 1) this.resize_mode = RowColumnLayout.HORIZONTAL;
                    else this.resize_mode = RowColumnLayout.VERTICAL;
                }
                idx++;
            }
        }
        for(int i = 0; i < this.col_idx; i++)
            k += this.row[i];
        if(this.resize_mode == RowColumnLayout.VERTICAL){
            if(this.maxHeight < this.row[this.col_idx] * (this.MIN_SIZE_H + this.vgap) - this.vgap) return;
            if(this.pos_y < y){
                start_comp = k;
                end_comp = k + this.row[this.col_idx];
                inc_comp = 1;
                max_y = this.maxHeight - (this.row[this.col_idx] - (this.comp_idx - k) - 1) * (this.MIN_SIZE_H + this.vgap) + this.vgap;
            }else{
                start_comp = k + this.row[this.col_idx] - 1;
                end_comp = k - 1;
                inc_comp = -1;
                y = this.maxHeight - y;
                this.comp_idx++;
                max_y = this.maxHeight - (this.comp_idx - k) * (this.MIN_SIZE_H + this.vgap);
            }
            if(y > max_y) resize_y = max_y;
            else resize_y = y;
            currMaxHeight = this.maxHeight - (this.row[this.col_idx] - 1) * this.vgap;
            for(k = start_comp, new_y = 0; k != end_comp; k += inc_comp){
                currHeight = (int)(currMaxHeight * this.percent_height[k]);
                if(k == this.comp_idx) this.percent_height[k] = (float)(resize_y - curr_y) / currMaxHeight;
                else if(curr_y + currHeight - new_y < this.MIN_SIZE_H) this.percent_height[k] = (float)(this.MIN_SIZE_H) / currMaxHeight;
                else this.percent_height[k] = (float)(curr_y + currHeight - new_y) / currMaxHeight;
                curr_y += currHeight + this.vgap;
                new_y += (int)(currMaxHeight * this.percent_height[k]) + this.vgap;
            }
            this.ResizeColumn(this.main_p, this.col_idx);
        }
        if(this.resize_mode == RowColumnLayout.HORIZONTAL){
            if(this.maxWidth < this.column * (this.MIN_SIZE_W + this.hgap) - this.hgap) return;
            currMaxWidth = (this.maxWidth - (this.column - 1) * this.hgap);
            for(int i = 0; i < this.col_idx; i++)
                curr_x += currMaxWidth * this.percent_width[i] + this.hgap;
            min_x = curr_x + this.MIN_SIZE_W;
            max_x = (int)(curr_x + currMaxWidth * (this.percent_width[this.col_idx] + this.percent_width[this.nextColumn(this.col_idx)])) + this.hgap;
            // percent_width[col_idx + 1])) + hgap;
            if(x < min_x) x = min_x;
            if(x > max_x - this.MIN_SIZE_W) x = max_x - this.MIN_SIZE_W;
            this.percent_width[this.col_idx] = (float)(x - curr_x) / currMaxWidth;
            // percent_width[col_idx + 1] = (float)(max_x - x - hgap)/currMaxWidth;
            this.percent_width[this.nextColumn(this.col_idx)] = (float)(max_x - x - this.hgap) / currMaxWidth;
            this.ResizeColumns(this.main_p);
        }
    }

    /**
     * Set normalize size of column width and component height
     *
     * @param ph
     *            Vector of normalize height of component. The sum of ph[x] for objet in a
     *            column must be 1.
     * @param pw
     *            Vector of normalize width of the column. Sum of pw[x] must be 1
     */
    public void SetPanelSize(final float ph[], final float pw[]) {
        if(ph != null && pw != null){
            if(ph.length < this.b_comp + 1 || pw.length < this.row.length) // column)
                return; // define exception
            this.percent_height = new float[this.b_comp + 1];
            this.percent_width = new float[this.row.length];
            // int MaxWidth = maxWidth - (column - 1) * hgap;
            // int currMaxWidth = MaxWidth;
            for(int i = 0; i < this.b_comp + 1; i++)
                this.percent_height[i] = ph[i];
            for(int i = 0; i < this.row.length; i++){
                /*
                if(MaxWidth * pw[i] > currMaxWidth - MIN_SIZE_W * (column - i))
                    pw[i] = ((float)currMaxWidth - MIN_SIZE_W * (column - i)) / MaxWidth;
                currMaxWidth -= MaxWidth * pw[i];
                 */
                this.percent_width[i] = pw[i];
            }
            this.sizeUnknown = false;
        }
    }

    /**
     * Set number of column an number of component for column
     *
     * @param row
     *            row[i] define number of component in column i
     */
    public void SetRowColumn(final int[] row) {
        this.b_comp = 0;
        this.column = 0;
        this.row = new int[row.length];
        for(int i = 0; i < row.length; i++){
            this.row[i] = row[i];
            this.b_comp += row[i];
            if(row[i] != 0) this.column++;
        }
        this.b_comp--;
        this.sizeUnknown = true;
    }

    /**
     * Method used to update number of column end number of component for column
     *
     * @param row
     *            row[i] define number of component in column i
     * @param ph
     *            Vector of normalize height of component. The sum of ph[x] for object in a
     *            column must be 1.
     * @param pw
     *            Vector of normalize width of the column. Sum of pw[x] must be 1
     */
    public void SetRowColumn(final int[] row, final float ph[], final float pw[]) {
        this.SetRowColumn(row);
        this.SetPanelSize(ph, pw);
    }

    /**
     * Initialize component size
     *
     * @param parent
     *            parent component
     */
    private void setSizes(final Container parent) {
        final int nComps = parent.getComponentCount();
        Dimension d = null;
        int totW, totH, k, maxW[];
        // Reset preferred/minimum width and height.
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.minWidth = 0;
        this.minHeight = 0;
        if(2 * this.b_comp + 1 != nComps){ throw new IllegalArgumentException("Invalid number of component in RowColumnLayout"); }
        this.percent_height = new float[this.b_comp + 1];
        this.percent_width = new float[this.row.length];// column];
        final Dimension min_d = parent.getComponent(this.b_comp + 0).getMinimumSize();
        this.MIN_SIZE_W = min_d.width;
        this.MIN_SIZE_H = min_d.height;
        k = 0;
        totW = 0;
        maxW = new int[this.row.length];// column];
        for(int i = 0; i < this.row.length; i++){// column; i++) {
            if(this.row[i] == 0) continue;
            maxW[i] = 0;
            totH = 0;
            for(int j = 0; j < this.row[i]; j++){
                final Component c = parent.getComponent(this.b_comp + k++);
                // if(!(c instanceof RowColumnComponent))
                // return; //define exception
                if(c.isVisible()){
                    d = c.getPreferredSize();
                    if(d.height < this.MIN_SIZE_H) d.height = this.MIN_SIZE_H;
                    if(d.width < this.MIN_SIZE_W) d.width = this.MIN_SIZE_W;
                    if(maxW[i] < d.width) maxW[i] = d.width;
                    totH += d.height;
                }
            }
            if(totH > this.preferredHeight) this.preferredHeight = totH;
            totW += maxW[i];
        }
        this.preferredWidth = totW;
        this.minWidth = this.preferredWidth;
        this.minHeight = this.preferredHeight;
        k = 0;
        for(int i = 0; i < this.row.length; i++){// column; i++) {
            if(this.row[i] == 0) continue;
            for(int j = 0; j < this.row[i]; j++){
                final Component c = parent.getComponent(this.b_comp + k);
                if(c.isVisible()){
                    this.percent_height[k] = (float)1. / this.row[i];
                }
                k++;
            }
            this.percent_width[i] = (float)1. / this.column;
        }
        // resetPercentHW(parent);
    }

    /**
     * Returns a string representation of this RowColumnLayout and its values.
     *
     * @return a string representation of this layout.
     */
    @Override
    public String toString() {
        final String str = "";
        return this.getClass().getName() + "[vgap=" + this.vgap + str + "]";
    }
}
