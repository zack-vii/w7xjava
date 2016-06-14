package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import debug.DEBUG;
import mds.Database;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor.OPC;

@SuppressWarnings("serial")
public final class Function extends Descriptor_R<Short>{
    private final class Arguments extends ArrayList<Descriptor>{
        private final boolean deco;

        public Arguments(final Descriptor[] descs, final int idx, final boolean deco){
            super(descs.length - idx);
            this.deco = deco;
            for(int i = idx; i < descs.length; i++)
                this.add(descs[i]);
        }

        public final String join(final String delimiter, final int idx) {
            final String[] args = new String[this.size() - idx];
            if(this.deco) for(int i = 0; i < args.length; i++)
                args[i] = Descriptor.decompile(this.get(i + idx));
            else for(int i = 0; i < args.length; i++)
                args[i] = Descriptor.toString(this.get(i + idx));
            return String.join(delimiter, args);
        }
    }
    public static final class op_rec{
        public final short  opcode;
        public final byte   prec, lorr;
        public final String symbol;

        public op_rec(final String symbol, final short opcode, final byte prec, final byte lorr){
            this.symbol = symbol;
            this.opcode = opcode;
            this.prec = prec;
            this.lorr = lorr;
        }
    }
    private static final op_rec[] binary    = new op_rec[31], unary = new op_rec[8];
    private static final String   newline   = "\r\n\t\t\t\t\t\t\t";
    private static int            TdiIndent = 1;

    static{
        Function.binary[0] = new op_rec(", ", OPC.OpcComma, (byte)92, (byte)-1);
        Function.binary[1] = new op_rec(" = ", OPC.OpcEquals, (byte)84, (byte)1);
        Function.binary[2] = new op_rec(null, OPC.OpcEqualsFirst, (byte)84, (byte)1);
        Function.binary[3] = new op_rec(" : ", OPC.OpcDtypeRange, (byte)80, (byte)0);
        Function.binary[4] = new op_rec(" ? ", OPC.OpcConditional, (byte)72, (byte)1);
        Function.binary[5] = new op_rec(" @ ", OPC.OpcPromote, (byte)68, (byte)1);
        Function.binary[6] = new op_rec(" Eqv ", OPC.OpcEqv, (byte)64, (byte)-1);
        Function.binary[7] = new op_rec(" Neqv ", OPC.OpcNeqv, (byte)64, (byte)-1);
        Function.binary[8] = new op_rec(" || ", OPC.OpcOr, (byte)60, (byte)-1);
        Function.binary[9] = new op_rec(" Or_Not ", OPC.OpcOrNot, (byte)60, (byte)-1);
        Function.binary[10] = new op_rec(" Nor ", OPC.OpcNor, (byte)60, (byte)-1);
        Function.binary[11] = new op_rec(" Nor_Not ", OPC.OpcNorNot, (byte)60, (byte)-1);
        Function.binary[12] = new op_rec(" && ", OPC.OpcAnd, (byte)52, (byte)-1);
        Function.binary[13] = new op_rec(" And_Not ", OPC.OpcAndNot, (byte)52, (byte)-1);
        Function.binary[14] = new op_rec(" Nand ", OPC.OpcNand, (byte)52, (byte)-1);
        Function.binary[15] = new op_rec(" Nand_Not ", OPC.OpcNandNot, (byte)52, (byte)-1);
        Function.binary[16] = new op_rec(" == ", OPC.OpcEq, (byte)48, (byte)-1);
        Function.binary[17] = new op_rec(" <> ", OPC.OpcNe, (byte)48, (byte)-1);
        Function.binary[18] = new op_rec(" >= ", OPC.OpcGe, (byte)44, (byte)-1);
        Function.binary[19] = new op_rec(" > ", OPC.OpcGt, (byte)44, (byte)-1);
        Function.binary[20] = new op_rec(" <= ", OPC.OpcLe, (byte)44, (byte)-1);
        Function.binary[21] = new op_rec(" < ", OPC.OpcLt, (byte)44, (byte)-1);
        Function.binary[22] = new op_rec(" Is_In ", OPC.OpcIsIn, (byte)40, (byte)-1);
        Function.binary[23] = new op_rec(" // ", OPC.OpcConcat, (byte)32, (byte)-1);
        Function.binary[24] = new op_rec(" << ", OPC.OpcShiftLeft, (byte)28, (byte)-1);
        Function.binary[25] = new op_rec(" >> ", OPC.OpcShiftRight, (byte)28, (byte)-1);
        Function.binary[26] = new op_rec(" + ", OPC.OpcAdd, (byte)24, (byte)-1);
        Function.binary[27] = new op_rec(" - ", OPC.OpcSubtract, (byte)24, (byte)-1);
        Function.binary[28] = new op_rec(" * ", OPC.OpcMultiply, (byte)20, (byte)-1);
        Function.binary[29] = new op_rec(" / ", OPC.OpcDivide, (byte)20, (byte)-1);
        Function.binary[30] = new op_rec(" ^ ", OPC.OpcPower, (byte)16, (byte)1);
        Function.unary[0] = new op_rec("~", OPC.OpcInot, (byte)8, (byte)1);
        Function.unary[1] = new op_rec("!", OPC.OpcNot, (byte)8, (byte)1);
        Function.unary[2] = new op_rec("--", OPC.OpcPreDec, (byte)8, (byte)1);
        Function.unary[3] = new op_rec("++", OPC.OpcPreInc, (byte)8, (byte)1);
        Function.unary[4] = new op_rec("-", OPC.OpcUnaryMinus, (byte)8, (byte)1);
        Function.unary[5] = new op_rec("+", OPC.OpcUnaryPlus, (byte)8, (byte)1);
        Function.unary[6] = new op_rec("--", OPC.OpcPostDec, (byte)4, (byte)-1);
        Function.unary[7] = new op_rec("++", OPC.OpcPostInc, (byte)4, (byte)-1);
    }

    public static final Function $2PI() {
        return new Function((short)377, (byte)0);
    }

    public static final Function $A0() {
        return new Function((short)1, (byte)0);
    }

    public static final Function $ALPHA() {
        return new Function((short)2, (byte)0);
    }

    public static final Function $AMU() {
        return new Function((short)3, (byte)0);
    }

    public static final Function $C() {
        return new Function((short)4, (byte)0);
    }

    public static final Function $CAL() {
        return new Function((short)5, (byte)0);
    }

    public static final Function $DEGREE() {
        return new Function((short)6, (byte)0);
    }

    public static final Function $FALSE() {
        return new Function((short)7, (byte)0);
    }

    public static final Function $FARADAY() {
        return new Function((short)8, (byte)0);
    }

    public static final Function $G() {
        return new Function((short)9, (byte)0);
    }

    public static final Function $GAS() {
        return new Function((short)10, (byte)0);
    }

    public static final Function $H() {
        return new Function((short)11, (byte)0);
    }

    public static final Function $HBAR() {
        return new Function((short)12, (byte)0);
    }

    public static final Function $I() {
        return new Function((short)13, (byte)0);
    }

    public static final Function $K() {
        return new Function((short)14, (byte)0);
    }

    public static final Function $MISSING() {
        return new Function((short)15, (byte)0);
    }

    public static final Function $MP() {
        return new Function((short)16, (byte)0);
    }

    public static final Function $N0() {
        return new Function((short)17, (byte)0);
    }

    public static final Function $NA() {
        return new Function((short)18, (byte)0);
    }

    public static final Function $P0() {
        return new Function((short)19, (byte)0);
    }

    public static final Function $PI() {
        return new Function((short)20, (byte)0);
    }

    public static final Function $QE() {
        return new Function((short)21, (byte)0);
    }

    public static final Function $RE() {
        return new Function((short)22, (byte)0);
    }

    public static final Function $ROPRAND() {
        return new Function((short)23, (byte)0);
    }

    public static final Function $RYDBERG() {
        return new Function((short)24, (byte)0);
    }

    public static final Function $TORR() {
        return new Function((short)25, (byte)0);
    }

    public static final Function $TRUE() {
        return new Function((short)26, (byte)0);
    }

    public static final Function $VALUE() {
        return new Function((short)27, (byte)0);
    }

    private static final void addCompoundStatement(final int nstmt, final Descriptor[] pin, final int offset, final StringBuilder pout) throws MdsException {
        pout.append('{');
        if(nstmt > 0){
            Function.addIndent(1, pout);
            Function.addMultiStatement(nstmt, pin, offset, pout);
            Function.deIndent(pout);
            Function.addIndent(-1, pout);
        }
        pout.append('}');
    }

    private final static void addIndent(final int step, final StringBuilder pout) {
        final int len = (((Function.TdiIndent += step) < 8 ? Function.TdiIndent : 8) + 1);
        pout.append(Function.newline.substring(0, len));
    }

    private static final void addMultiStatement(final int nstmt, final Descriptor[] pin, final int offset, final StringBuilder pout) throws MdsException {
        if(nstmt == 0){
            pout.append(';');
            Function.addIndent(0, pout);
        }else for(int j = 0; j < nstmt; j++)
            Function.addOneStatement(pin[j + offset], pout);
    }

    private static final void addOneStatement(final Descriptor pin, final StringBuilder pout) throws MdsException {
        if(pin != null) pin.decompile(Function.P_STMT, pout);
        Function.deIndent(pout);
        switch(pout.substring(pout.length() - 1).charAt(0)){
            default:
                pout.append(';');
                break;
            case ';':
            case '}':
                break;
        }
        Function.addIndent(0, pout);
    }

    private final static void deIndent(final StringBuilder pout) {
        int fin;
        for(fin = pout.length() - 1; fin >= 0; fin--){
            final char t = pout.substring(fin).charAt(0);
            if(!(t == '\t' || t == '\r' || t == '\n')) break;
        }
        pout.setLength(fin + 1);
    }

    private static final String if_errorX(final short opcode, final Arguments args) {
        return String.format("%s(\r\n\t%s\n)", OPC.Names[opcode], args.join(",\r\n\t", 0));
    }

    public static final void main(final String[] args) throws MdsException {// TODO:main
        final Database db = new Database(null, "test", -1, Database.READONLY);
        System.out.println(db.compile("in _R").decompile());
        System.out.println(db.compile("out _R").decompile());
        System.out.println(db.compile("private _R").decompile());
        System.out.println(db.compile("public _R").decompile());
        System.out.println(db.compile("_r=sqrt((1+5)*6)/(3-1)").decompile());
        System.out.println(db.compile("for(_i=1;_i<5;_i++) write(*,text(_i))").decompile());
        System.out.println(db.compile("TreeShr->TreeCtx($SHOT,(5/2)^2)").decompile());
        System.out.println(db.compile("[cmplx(1.e2,3.e4)]").decompile());
        System.exit(0);
    }

    private static final String statementX(final short opcode, final Arguments args) {
        return args.join(";\r\n", 0) + ";\r\n";
    }

    public Function(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Function(final short mode, final byte nargs){
        super(DTYPE.FUNCTION, nargs, ByteBuffer.allocate(Short.BYTES).putShort(mode).array());
    }

    public Function(final short mode, final Descriptor[] args){
        this(mode, (byte)(args == null ? 0 : args.length));
        if(args == null) return;
        System.arraycopy(args, 0, this.dscptrs, 0, args.length);
    }

    @Override
    public final String decompile() {
        try{
            return this.decompile(Function.P_STMT, new StringBuilder(1024)).toString();
        }catch(final MdsException e){
            e.printStackTrace();
            return "1";
        }
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout) throws MdsException {
        int narg = this.ndesc;
        op_rec pop = null;
        int lorr;
        int newone;
        Descriptor_R r_ptr = null;
        Descriptor ptr = null;
        final short opcode = this.getValue();
        if(DEBUG.D) System.out.println(OPC.Names[opcode]);
        switch(opcode){
            default:{ /*intrinsic(arg, ...) */
                final String name = this.getName();
                pout.append(name);
                if(name.charAt(0) != '$') this.addArguments(0, "(", ")", pout);
                break;
            }
            case OPC.OpcFun:{ /*fun ident(arg, ...) stmt */
                if(prec < Function.P_STMT) pout.append('(');
                pout.append("Fun ");
                ptr = this.getDscptrs(0);
                if(ptr.dtype == DTYPE.T) pout.append(ptr.toString());
                else ptr.decompile(Function.P_SUBS, pout);
                this.addArguments(2, " (", ") ", pout);
                Function.addCompoundStatement(1, this.dscptrs, 1, pout);
                if(prec < Function.P_STMT) pout.append(')');
                break;
            }
            case OPC.OpcIn: /*input argument */
            case OPC.OpcInOut: /*input and output argument */
            case OPC.OpcOptional: /*optional argument */
            case OPC.OpcOut: /*output argument */
            case OPC.OpcPrivate: /*private ident */
            case OPC.OpcPublic:{ /*public ident */
                pout.append(this.getName());
                pout.append(" ");
                ptr = this.getDscptrs(0);
                if(ptr.dtype == DTYPE.T) pout.append(ptr.toString());
                else ptr.decompile(Function.P_SUBS, pout);
                break;
            }
            case OPC.OpcExtFunction:{ /*_label(arg, ...)*/
                if(this.dscptrs[0] != null || this.dscptrs[1] == null || this.dscptrs[1].dtype != DTYPE.T){
                    pout.append(this.getName());
                    this.addArguments(0, "(", ")", pout);
                    break;
                }
                pout.append(this.dscptrs[1].toString());
                this.addArguments(2, "(", ")", pout);
                break;
            }
            case OPC.OpcSubscript:{ /*postfix[subscript, ...] */
                this.getDscptrs(0).decompile(Function.P_SUBS, pout);
                this.addArguments(1, "[", "]", pout);
                break;
            }
            case OPC.OpcVector:{ /*[elem, ...] */
                this.addArguments(0, "[", "]", pout);
                break;
            }
                /****************
                 * Unary operations.
                 ****************/
            case OPC.OpcInot:
            case OPC.OpcNot:
            case OPC.OpcPreDec:
            case OPC.OpcPreInc:
            case OPC.OpcUnaryMinus:
            case OPC.OpcUnaryPlus:
            case OPC.OpcPostDec:
            case OPC.OpcPostInc:{
                for(final op_rec element : Function.unary)
                    if(element.opcode == opcode){
                        pop = element;
                        break;
                    }
                if(pop == null) throw new MdsException("unary opcode not found");
                newone = pop.prec;
                lorr = pop.lorr;
                if(lorr > 0) pout.append(pop.symbol);
                if(prec <= newone) pout.append("(");
                this.getDscptrs(0).decompile(newone + lorr, pout);
                if(prec <= newone) pout.append(")");
                if(lorr < 0) pout.append(pop.symbol);
                break;
                /***********************
                 * Binary/n-ary operations.
                 ***********************/
            }
            case OPC.OpcEqualsFirst:
            case OPC.OpcPower:
            case OPC.OpcDivide:
            case OPC.OpcMultiply:
            case OPC.OpcAdd:
            case OPC.OpcSubtract:
            case OPC.OpcShiftLeft:
            case OPC.OpcShiftRight:
            case OPC.OpcConcat:
            case OPC.OpcIsIn:
            case OPC.OpcGe:
            case OPC.OpcGt:
            case OPC.OpcLe:
            case OPC.OpcLt:
            case OPC.OpcEq:
            case OPC.OpcNe:
            case OPC.OpcAnd:
            case OPC.OpcNand:
            case OPC.OpcOr:
            case OPC.OpcNor:
            case OPC.OpcEqv:
            case OPC.OpcNeqv:
            case OPC.OpcPromote:
            case OPC.OpcEquals:
            case OPC.OpcDtypeRange:
            case OPC.OpcComma:
            case OPC.OpcConditional:{
                if(opcode == OPC.OpcEqualsFirst){
                    ptr = this.getDscptrs(0);
                    while(ptr != null && ptr.dtype == DTYPE.DSC)
                        ptr = ptr.getDescriptor();
                    r_ptr = (Descriptor_R)ptr;
                    if(r_ptr == null) throw new MdsException("OpcEqualsFirst:null");
                    newone = ((Function)r_ptr).getValue();
                    narg = ((Function)r_ptr).ndesc;
                }else{
                    r_ptr = this;
                    newone = opcode;
                }
                for(final op_rec element : Function.binary)
                    if(element.opcode == opcode){
                        pop = element;
                        break;
                    }
                if(pop == null) throw new MdsException("binary opcode not found");
                newone = pop.prec;
                lorr = pop.lorr;
                if(opcode == OPC.OpcEqualsFirst){
                    newone = Function.binary[2].prec;
                    lorr = Function.binary[2].lorr;
                }
                if(prec <= newone) pout.append('(');
                if(opcode == OPC.OpcConditional){
                    r_ptr.dscptrs[2].decompile(newone - lorr, pout);
                    pout.append(pop.symbol);
                    r_ptr.dscptrs[0].decompile(newone, pout);
                    pout.append(" : ");
                    r_ptr.dscptrs[1].decompile(newone + lorr, pout);
                }else{
                    r_ptr.dscptrs[0].decompile(newone - lorr, pout);
                    for(int m = 1; m < narg; m++){
                        pout.append(pop.symbol);
                        if(this != r_ptr) pout.append("= ");
                        r_ptr.dscptrs[m].decompile(newone + lorr, pout);
                    }
                }
                if(prec <= newone) pout.append(")");
                break;
                /*****************************************
                 * C-language constructs followed by newline.
                 * Plus WHERE.
                 *****************************************/
            }
            case OPC.OpcBreak: /*break; */
            case OPC.OpcContinue:{ /*continue; */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append(this.getDscptrs(0).toString());
                Function.addOneStatement(null, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcCase:{ /*case (xxx) stmt ... */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("Case (");
                this.getDscptrs(0).decompile(Function.P_STMT, pout);
                pout.append(") ");
                Function.addMultiStatement(narg - 1, this.dscptrs, 1, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcDefault:{ /*case default stmt ... */
                pout.append("(");
                pout.append("Case Default ");
                Function.addMultiStatement(narg, this.dscptrs, 0, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcDo:{ /*do {stmt} while (exp); Note argument order is (exp,stmt,...) */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("DO {");
                Function.addMultiStatement(narg - 1, this.dscptrs, 1, pout);
                pout.append("} While ");
                this.dscptrs[0].decompile(Function.P_STMT, pout);
                Function.addMultiStatement(0, null, 0, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcFor:{ /*for (init;test;step) stmt */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("For (");
                this.getDscptrs(0).decompile(Function.P_STMT, pout);
                pout.append("; ");
                this.getDscptrs(1).decompile(Function.P_STMT, pout);
                pout.append("; ");
                this.getDscptrs(2).decompile(Function.P_STMT, pout);
                pout.append(") ");
                Function.addCompoundStatement(narg - 3, this.dscptrs, 3, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcGoto:{ /*goto xxx; */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("GoTo ");
                pout.append(this.getDscptrs(0).toString());
                Function.addOneStatement(null, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcIf: /*if (exp) stmt else stmt */
            case OPC.OpcWhere:{ /*where (exp) stmt elsewhere stmt */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append((opcode == OPC.OpcIf) ? "If (" : "Where (");
                this.getDscptrs(0).decompile(Function.P_STMT, pout);
                pout.append(") ");
                Function.addCompoundStatement(1, this.dscptrs, 1, pout);
                if(narg >= 3){
                    pout.append((opcode == OPC.OpcIf) ? " Else " : " ElseWhere ");
                    Function.addCompoundStatement(1, this.dscptrs, 2, pout);
                }
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcLabel:{ /*xxx : stmt ... */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("Label ");
                pout.append(this.getDscptrs(0).toString());
                pout.append(" : ");
                Function.addMultiStatement(narg - 1, this.dscptrs, 1, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcReturn:{ /*return (optional-exp); */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append("Return (");
                if(this.ndesc > 0) this.getDscptrs(0).decompile(Function.P_STMT, pout);
                else pout.append("*");
                pout.append(")");
                Function.addOneStatement(null, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcStatement:{ /*{stmt ...} */
                if(prec < Function.P_STMT) pout.append("(");
                Function.addMultiStatement(narg, this.dscptrs, 0, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
            case OPC.OpcSwitch: /*switch (exp) stmt */
            case OPC.OpcWhile:{ /*while (exp) stmt */
                if(prec < Function.P_STMT) pout.append("(");
                pout.append((opcode == OPC.OpcSwitch) ? "Switch (" : "While (");
                this.dscptrs[0].decompile(Function.P_STMT, pout);
                pout.append(") ");
                Function.addCompoundStatement(narg - 1, this.dscptrs, 1, pout);
                if(prec < Function.P_STMT) pout.append(")");
                break;
            }
        }
        return pout;
    }

    @Override
    public final String decompileX() {
        final short opcode = this.getOpCode();
        if(opcode == 0) return super.toString();
        final Arguments args = new Arguments(this.dscptrs, 1, true);
        switch(opcode){
            case (190):
                return Function.if_errorX(opcode, args);
            case (331):
                return Function.statementX(opcode, args);
            default:
                return this.decompile();
        }
    }

    public final Descriptor getArgument(final int idx) {
        return this.dscptrs[idx];
    }

    public final Descriptor[] getArguments() {
        return this.dscptrs;
    }

    private final String getName() {
        return OPC.Names[this.getValue()];
    }

    public final short getOpCode() {
        return this.getValue();
    }

    @Override
    public final Short getValue(final ByteBuffer b) {
        switch(this.length){
            case 2:
                return b.getShort();
            case 1:
                return (short)Byte.toUnsignedInt(b.get());
            case 4:
                return (short)b.getInt();
            default:
                return 0;
        }
    }

    @Override
    public double[] toDouble() {
        return this.evaluate().toDouble();
    }

    @Override
    public float[] toFloat() {
        return this.evaluate().toFloat();
    }

    @Override
    public int[] toInt() {
        return this.evaluate().toInt();
    }

    @Override
    public long[] toLong() {
        return this.evaluate().toLong();
    }

    @Override
    public final String toString() {
        return this.toString(false);
    }

    private final String toString(final boolean preview) {
        final short opcode = this.getOpCode();
        if(opcode == 0) return super.toString();
        return this.decompile();
    }
}
