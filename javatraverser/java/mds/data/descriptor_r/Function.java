package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import debug.DEBUG;
import mds.Mds;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor.OPC;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Complex32;
import mds.data.descriptor_s.Float32;
import mds.data.descriptor_s.Missing;
import mds.data.descriptor_s.Uint8;

public final class Function extends Descriptor_R<Short>{
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
    private static final op_rec[] binary    = new op_rec[]{                    //
            new op_rec(", ", OPC.OpcComma, (byte)92, (byte)-1),                // 0
            new op_rec(" = ", OPC.OpcEquals, (byte)84, (byte)1),               // 1
            new op_rec(null, OPC.OpcEqualsFirst, (byte)84, (byte)1),           // 2
            new op_rec(" : ", OPC.OpcDtypeRange, (byte)80, (byte)0),           // 3
            new op_rec(" ? ", OPC.OpcConditional, (byte)72, (byte)1),          // 4
            new op_rec(" @ ", OPC.OpcPromote, (byte)68, (byte)1),              // 5
            new op_rec(" Eqv ", OPC.OpcEqv, (byte)64, (byte)-1),               // 6
            new op_rec(" Neqv ", OPC.OpcNeqv, (byte)64, (byte)-1),             // 7
            new op_rec(" || ", OPC.OpcOr, (byte)60, (byte)-1),                 // 8
            new op_rec(" Or_Not ", OPC.OpcOrNot, (byte)60, (byte)-1),          // 9
            new op_rec(" Nor ", OPC.OpcNor, (byte)60, (byte)-1),               // 10
            new op_rec(" Nor_Not ", OPC.OpcNorNot, (byte)60, (byte)-1),        // 11
            new op_rec(" && ", OPC.OpcAnd, (byte)52, (byte)-1),                // 12
            new op_rec(" And_Not ", OPC.OpcAndNot, (byte)52, (byte)-1),        // 13
            new op_rec(" Nand ", OPC.OpcNand, (byte)52, (byte)-1),             // 14
            new op_rec(" Nand_Not ", OPC.OpcNandNot, (byte)52, (byte)-1),      // 15
            new op_rec(" == ", OPC.OpcEq, (byte)48, (byte)-1),                 // 16
            new op_rec(" <> ", OPC.OpcNe, (byte)48, (byte)-1),                 // 17
            new op_rec(" >= ", OPC.OpcGe, (byte)44, (byte)-1),                 // 18
            new op_rec(" > ", OPC.OpcGt, (byte)44, (byte)-1),                  // 19
            new op_rec(" <= ", OPC.OpcLe, (byte)44, (byte)-1),                 // 20
            new op_rec(" < ", OPC.OpcLt, (byte)44, (byte)-1),                  // 21
            new op_rec(" Is_In ", OPC.OpcIsIn, (byte)40, (byte)-1),            // 22
            new op_rec(" // ", OPC.OpcConcat, (byte)32, (byte)-1),             // 23
            new op_rec(" << ", OPC.OpcShiftLeft, (byte)28, (byte)-1),          // 24
            new op_rec(" >> ", OPC.OpcShiftRight, (byte)28, (byte)-1),         // 25
            new op_rec(" + ", OPC.OpcAdd, (byte)24, (byte)-1),                 // 26
            new op_rec(" - ", OPC.OpcSubtract, (byte)24, (byte)-1),            // 27
            new op_rec(" * ", OPC.OpcMultiply, (byte)20, (byte)-1),            // 28
            new op_rec(" / ", OPC.OpcDivide, (byte)20, (byte)-1),              // 29
            new op_rec(" ^ ", OPC.OpcPower, (byte)16, (byte)1),                // 30
    };
    private static final String   newline   = "\r\n\t\t\t\t\t\t\t";
    private static int            TdiIndent = 1;
    private static final op_rec[] unary     = new op_rec[]{                    //
            new op_rec("~", OPC.OpcInot, (byte)8, (byte)1),                    // 0
            new op_rec("!", OPC.OpcNot, (byte)8, (byte)1),                     // 1
            new op_rec("--", OPC.OpcPreDec, (byte)8, (byte)1),                 // 2
            new op_rec("++", OPC.OpcPreInc, (byte)8, (byte)1),                 // 3
            new op_rec("-", OPC.OpcUnaryMinus, (byte)8, (byte)1),              // 4
            new op_rec("+", OPC.OpcUnaryPlus, (byte)8, (byte)1),               // 5
            new op_rec("--", OPC.OpcPostDec, (byte)4, (byte)-1),               // 6
            new op_rec("++", OPC.OpcPostInc, (byte)4, (byte)-1),               // 7
    };

    public static final Function $2PI() {
        return new Function(OPC.Opc2Pi);
    }

    public static final Function $A0() {
        return new Function(OPC.OpcA0);
    }

    public static final Function $ALPHA() {
        return new Function(OPC.OpcAlpha);
    }

    public static final Function $AMU() {
        return new Function(OPC.OpcAmu);
    }

    public static final Function $C() {
        return new Function(OPC.OpcC);
    }

    public static final Function $CAL() {
        return new Function(OPC.OpcCal);
    }

    public static final Function $DEGREE() {
        return new Function(OPC.OpcDegree);
    }

    public static final Function $FALSE() {
        return new Function(OPC.OpcFalse);
    }

    public static final Function $FARADAY() {
        return new Function(OPC.OpcFaraday);
    }

    public static final Function $G() {
        return new Function(OPC.OpcG);
    }

    public static final Function $GAS() {
        return new Function(OPC.OpcGas);
    }

    public static final Function $H() {
        return new Function(OPC.OpcH);
    }

    public static final Function $HBAR() {
        return new Function(OPC.OpcHbar);
    }

    public static final Function $I() {
        return new Function(OPC.OpcI);
    }

    public static final Function $K() {
        return new Function(OPC.OpcK);
    }

    public static final Function $MISSING() {
        return new Function(OPC.OpcMissing);
    }

    public static final Function $MP() {
        return new Function(OPC.OpcMp);
    }

    public static final Function $N0() {
        return new Function(OPC.OpcN0);
    }

    public static final Function $NA() {
        return new Function(OPC.OpcNa);
    }

    public static final Function $P0() {
        return new Function(OPC.OpcP0);
    }

    public static final Function $PI() {
        return new Function(OPC.OpcPi);
    }

    public static final Function $QE() {
        return new Function(OPC.OpcQe);
    }

    public static final Function $RE() {
        return new Function(OPC.OpcRe);
    }

    public static final Function $ROPRAND() {
        return new Function(OPC.OpcRoprand);
    }

    public static final Function $RYDBERG() {
        return new Function(OPC.OpcRydberg);
    }

    public static final Function $TORR() {
        return new Function(OPC.OpcTorr);
    }

    public static final Function $TRUE() {
        return new Function(OPC.OpcTrue);
    }

    public static final Function $VALUE() {
        return new Function(OPC.OpcValue);
    }

    public static final Function abs(final Descriptor... dscptrs) {
        return new Function(OPC.OpcAbs, dscptrs);
    }

    public static final Function add(final Descriptor... dscptrs) {
        return new Function(OPC.OpcAdd, dscptrs);
    }

    private static final void addCompoundStatement(final int nstmt, final Descriptor_R pin, final int offset, final StringBuilder pout, final int mode) {
        pout.append('{');
        if(nstmt > 0){
            Function.addIndent(1, pout);
            Function.addMultiStatement(nstmt, pin, offset, pout, mode);
            Function.deIndent(pout);
            Function.addIndent(-1, pout);
        }
        pout.append('}');
    }

    private final static void addIndent(final int step, final StringBuilder pout) {
        final int len = (((Function.TdiIndent += step) < 8 ? Function.TdiIndent : 8) + 1);
        pout.append(Function.newline.substring(0, len));
    }

    private static final void addMultiStatement(final int nstmt, final Descriptor_R pin, final int offset, final StringBuilder pout, final int mode) {
        if(nstmt == 0){
            pout.append(';');
            Function.addIndent(0, pout);
        }else for(int j = 0; j < nstmt; j++)
            Function.addOneStatement(pin.getDescriptor(j + offset), pout, mode);
    }

    private static final void addOneStatement(final Descriptor pin, final StringBuilder pout, final int mode) {
        if(pin != null) pin.decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
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

    public static final Function Byte(final Descriptor value) {
        return new Function(OPC.OpcByte, value);
    }

    public static final Function Byte_Unsigned(final Descriptor value) {
        return new Function(OPC.OpcByteUnsigned, value);
    }

    public static final Function D_Float(final Descriptor value) {
        return new Function(OPC.OpcDFloat, value);
    }

    public static final Function Dble(final Descriptor value) {
        return new Function(OPC.OpcDble, value);
    }

    private final static void deIndent(final StringBuilder pout) {
        int fin;
        for(fin = pout.length() - 1; fin >= 0; fin--){
            final char t = pout.substring(fin).charAt(0);
            if(!(t == '\t' || t == '\r' || t == '\n')) break;
        }
        pout.setLength(fin + 1);
    }

    public static final Function F_Float(final Descriptor value) {
        return new Function(OPC.OpcFFloat, value);
    }

    public static final Function Float(final Descriptor value) {
        return new Function(OPC.OpcFloat, value);
    }

    public static final Function FS_Float(final Descriptor value) {
        return new Function(OPC.OpcFS_float, value);
    }

    public static final Function FT_Float(final Descriptor value) {
        return new Function(OPC.OpcFT_float, value);
    }

    public static final Function G_Float(final Descriptor value) {
        return new Function(OPC.OpcGFloat, value);
    }

    public static final Function Long(final Descriptor value) {
        return new Function(OPC.OpcLong, value);
    }

    public static final Function Long_Unsigned(final Descriptor value) {
        return new Function(OPC.OpcLongUnsigned, value);
    }

    public static final Function Octaword(final Descriptor value) {
        return new Function(OPC.OpcOctaword, value);
    }

    public static final Function Octaword_Unsigned(final Descriptor value) {
        return new Function(OPC.OpcOctawordUnsigned, value);
    }

    public static final Function Quadword(final Descriptor value) {
        return new Function(OPC.OpcQuadword, value);
    }

    public static final Function Quadword_Unsigned(final Descriptor value) {
        return new Function(OPC.OpcQuadwordUnsigned, value);
    }

    public static final Function Word(final Descriptor value) {
        return new Function(OPC.OpcWord, value);
    }

    public static final Function Word_Unsigned(final Descriptor value) {
        return new Function(OPC.OpcWordUnsigned, value);
    }

    public Function(final ByteBuffer b) throws MdsException{
        super(b);
    }

    public Function(final short mode, final Descriptor... args){
        super(DTYPE.FUNCTION, ByteBuffer.allocate(Short.BYTES).order(Descriptor.BYTEORDER).putShort(0, mode), args);
    }

    @Override
    public final StringBuilder decompile(final int prec, final StringBuilder pout, final int mode) {
        int narg = this.ndesc;
        op_rec pop = null;
        int lorr;
        int newone;
        Descriptor_R r_ptr = null;
        Descriptor ptr = null;
        final short opcode = this.getValue();
        if(DEBUG.D) System.out.println(OPC.Names[opcode]);
        try{
            switch(opcode){
                default:{ /*intrinsic(arg, ...) */
                    final String name = this.getName();
                    pout.append(name);
                    if(name.charAt(0) != '$') this.addArguments(0, "(", ")", pout, mode);
                    break;
                }
                case OPC.OpcFun:{ /*fun ident(arg, ...) stmt */
                    if(prec < Descriptor.P_STMT) pout.append('(');
                    pout.append("Fun ");
                    ptr = this.getDescriptor(0);
                    if(ptr.dtype == DTYPE.T) pout.append(ptr.toString());
                    else ptr.decompile(Descriptor.P_SUBS, pout, mode & ~Descriptor.DECO_X);
                    this.addArguments(2, " (", ") ", pout, mode);
                    Function.addCompoundStatement(1, this, 1, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(')');
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
                    ptr = this.getDescriptor(0);
                    if(ptr.dtype == DTYPE.T) pout.append(ptr.toString());
                    else ptr.decompile(Descriptor.P_SUBS, pout, mode & ~Descriptor.DECO_X);
                    break;
                }
                case OPC.OpcExtFunction:{ /*_label(arg, ...)*/
                    if(this.getDescriptor(0) != Missing.NEW || this.getDescriptor(1) == Missing.NEW || this.getDescriptor(1).dtype != DTYPE.T){
                        pout.append(OPC.Names[this.getValue()]);
                        this.addArguments(0, "(", ")", pout, mode);
                        break;
                    }
                    pout.append(this.getDescriptor(1).toString());
                    this.addArguments(2, "(", ")", pout, mode);
                    break;
                }
                case OPC.OpcSubscript:{ /*postfix[subscript, ...] */
                    this.getDescriptor(0).decompile(Descriptor.P_SUBS, pout, mode & ~Descriptor.DECO_X);
                    this.addArguments(1, "[", "]", pout, mode);
                    break;
                }
                case OPC.OpcVector:{ /*[elem, ...] */
                    this.addArguments(0, "[", "]", pout, mode);
                    break;
                }
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
                    this.getDescriptor(0).decompile(newone + lorr, pout, mode & ~Descriptor.DECO_X);
                    if(prec <= newone) pout.append(")");
                    if(lorr < 0) pout.append(pop.symbol);
                    break;
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
                        ptr = this.getDescriptor(0);
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
                        r_ptr.getDescriptor(2).decompile(newone - lorr, pout, mode & ~Descriptor.DECO_X);
                        pout.append(pop.symbol);
                        r_ptr.getDescriptor(0).decompile(newone, pout, mode & ~Descriptor.DECO_X);
                        pout.append(" : ");
                        r_ptr.getDescriptor(1).decompile(newone + lorr, pout, mode & ~Descriptor.DECO_X);
                    }else{
                        r_ptr.getDescriptor(0).decompile(newone - lorr, pout, mode & ~Descriptor.DECO_X);
                        for(int m = 1; m < narg; m++){
                            pout.append(pop.symbol);
                            if(this != r_ptr) pout.append("= ");
                            r_ptr.getDescriptor(m).decompile(newone + lorr, pout, mode & ~Descriptor.DECO_X);
                        }
                    }
                    if(prec <= newone) pout.append(")");
                    break;
                }
                case OPC.OpcBreak: /*break; */
                case OPC.OpcContinue:{ /*continue; */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append(this.getDescriptor(0).toString());
                    Function.addOneStatement(null, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcCase:{ /*case (xxx) stmt ... */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("Case (");
                    this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append(") ");
                    Function.addMultiStatement(narg - 1, this, 1, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcDefault:{ /*case default stmt ... */
                    pout.append("(");
                    pout.append("Case Default ");
                    Function.addMultiStatement(narg, this, 0, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcDo:{ /*do {stmt} while (exp); Note argument order is (exp,stmt,...) */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("DO {");
                    Function.addMultiStatement(narg - 1, this, 1, pout, mode);
                    pout.append("} While ");
                    this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    Function.addMultiStatement(0, null, 0, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcFor:{ /*for (init;test;step) stmt */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("For (");
                    this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append("; ");
                    this.getDscptrs(1).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append("; ");
                    this.getDscptrs(2).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append(") ");
                    Function.addCompoundStatement(narg - 3, this, 3, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcGoto:{ /*goto xxx; */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("GoTo ");
                    pout.append(this.getDescriptor(0).toString());
                    Function.addOneStatement(null, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcIf: /*if (exp) stmt else stmt */
                case OPC.OpcWhere:{ /*where (exp) stmt elsewhere stmt */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append((opcode == OPC.OpcIf) ? "If (" : "Where (");
                    this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append(") ");
                    Function.addCompoundStatement(1, this, 1, pout, mode);
                    if(narg >= 3){
                        pout.append((opcode == OPC.OpcIf) ? " Else " : " ElseWhere ");
                        Function.addCompoundStatement(1, this, 2, pout, mode);
                    }
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcLabel:{ /*xxx : stmt ... */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("Label ");
                    pout.append(this.getDescriptor(0).toString());
                    pout.append(" : ");
                    Function.addMultiStatement(narg - 1, this, 1, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcReturn:{ /*return (optional-exp); */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append("Return (");
                    if(this.ndesc > 0) this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    else pout.append("*");
                    pout.append(")");
                    Function.addOneStatement(null, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcStatement:{ /*{stmt ...} */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    Function.addMultiStatement(narg, this, 0, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
                case OPC.OpcSwitch: /*switch (exp) stmt */
                case OPC.OpcWhile:{ /*while (exp) stmt */
                    if(prec < Descriptor.P_STMT) pout.append("(");
                    pout.append((opcode == OPC.OpcSwitch) ? "Switch (" : "While (");
                    this.getDescriptor(0).decompile(Descriptor.P_STMT, pout, mode & ~Descriptor.DECO_X);
                    pout.append(") ");
                    Function.addCompoundStatement(narg - 1, this, 1, pout, mode);
                    if(prec < Descriptor.P_STMT) pout.append(")");
                    break;
                }
            }
        }catch(final MdsException e){
            pout.append("/***error<").append(e).append(">***/");
            e.printStackTrace();
        }
        return pout;
    }

    @Override
    public Descriptor evaluate() {
        switch(this.getOpCode()){
            default:
                Descriptor eval;
                final String deco = this.decompile();
                try{
                    eval = Mds.getActiveMds().getDescriptor(deco);
                }catch(final MdsException e){
                    eval = super.evaluate();
                }
                if(deco.equals(eval.decompile())) return Missing.NEW;
                return eval;
            case OPC.OpcValue:
                return this.VALUE.getVALUE();
            case OPC.Opc2Pi:
                return new Float32(6.2831853072f);
            case OPC.OpcA0:
                return new With_Units(new With_Error(new Float32(5.29177249e-11f), new Float32(0.00000024e-11f)), new CString("m"));
            case OPC.OpcAmu:
                return new With_Units(new With_Error(new Float32(1.6605402e-27f), new Float32(0.0000010e-27f)), new CString("kg"));
            case OPC.OpcC:
                return new With_Units(new Float32(299792458.f), new CString("m/s"));
            case OPC.OpcCal:
                return new With_Units(new Float32(4.1868f), new CString("J"));
            case OPC.OpcDegree:
                return new Float32(.01745329252f);
            case OPC.OpcEpsilon0:
                return new With_Units(new Float32(8.854187817e-12f), new CString("F/m"));
            case OPC.OpcEv:
                return new With_Units(new With_Error(new Float32(1.60217733e-19f), new Float32(0.00000049e-19f)), new CString("J/eV"));
            case OPC.OpcFalse:
                return new Uint8((byte)0);
            case OPC.OpcFaraday:
                return new With_Units(new With_Error(new Float32(9.6485309e4f), new Float32(0.0000029e4f)), new CString("C/mol"));
            case OPC.OpcG:
                return new With_Units(new With_Error(new Float32(6.67259e-11f), new Float32(0.00085f)), new CString("m^3/s^2/kg"));
            case OPC.OpcGas:
                return new With_Units(new With_Error(new Float32(8.314510f), new Float32(0.000070f)), new CString("J/K/mol"));
            case OPC.OpcGn:
                return new With_Units(new Float32(9.80665f), new CString("m/s^2"));
            case OPC.OpcH:
                return new With_Units(new With_Error(new Float32(6.6260755e-34f), new Float32(0.0000040f)), new CString("J*s"));
            case OPC.OpcHbar:
                return new With_Units(new With_Error(new Float32(1.05457266e-34f), new Float32(0.00000063f)), new CString("J*s"));
            case OPC.OpcI:
                return new Complex32(0.f, 1.f);
            case OPC.OpcK:
                return new With_Units(new With_Error(new Float32(1.380658e-23f), new Float32(0.000012e-23f)), new CString("J/K"));
            case OPC.OpcMe:
                return new With_Units(new With_Error(new Float32(9.1093897e-31f), new Float32(0.0000054e-31f)), new CString("kg"));
            case OPC.OpcMissing:
                return Missing.NEW;
            case OPC.OpcMp:
                return new With_Units(new With_Error(new Float32(1.6726231e-27f), new Float32(0.0000010e-27f)), new CString("kg"));
            case OPC.OpcMu0:
                return new With_Units(new Float32(12.566370614e-7f), new CString("N/A^2"));
            case OPC.OpcN0:
                return new With_Units(new With_Error(new Float32(2.686763e25f), new Float32(0.000023e25f)), new CString("/m^3"));
            case OPC.OpcNa:
                return new With_Units(new With_Error(new Float32(6.0221367e23f), new Float32(0.0000036e23f)), new CString("/mol"));
            case OPC.OpcP0:
                return new With_Units(new Float32(1.01325e5f), new CString("Pa"));
            case OPC.OpcPi:
                return new Float32(3.1415926536f);
            case OPC.OpcQe:
                return new With_Units(new With_Error(new Float32(1.60217733e-19f), new Float32(0.000000493e-19f)), new CString("C"));
            case OPC.OpcRe:
                return new With_Units(new With_Error(new Float32(2.81794092e-15f), new Float32(0.00000038e-15f)), new CString("m"));
            case OPC.OpcRoprand:
                return Missing.NEW;// TODO:set correct return value
            case OPC.OpcRydberg:
                return new With_Units(new With_Error(new Float32(1.0973731534e7f), new Float32(0.0000000013e7f)), new CString("/m"));
            case OPC.OpcT0:
                return new With_Units(new Float32(273.16f), new CString("K"));
            case OPC.OpcTorr:
                return new With_Units(new Float32(1.3332e2f), new CString("Pa"));
            case OPC.OpcTrue:
                return new Uint8((byte)1);
        }
    }

    public final Descriptor getArgument(final int idx) {
        return this.getDescriptor(idx);
    }

    public final Descriptor[] getArguments() {
        final Descriptor[] desc = new Descriptor[this.ndesc];
        for(int i = 0; i < this.ndesc; i++)
            desc[i] = this.getDescriptor(i);
        return desc;
    }

    @Override
    public Descriptor getData() {
        return this.evaluate().getData();
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
    public double toDouble() {
        return this.evaluate().toDouble();
    }

    @Override
    public double[] toDoubleArray() {
        return this.evaluate().toDoubleArray();
    }

    @Override
    public float toFloat() {
        return this.evaluate().toFloat();
    }

    @Override
    public float[] toFloatArray() {
        return this.evaluate().toFloatArray();
    }

    @Override
    public int toInt() {
        return this.evaluate().toInt();
    }

    @Override
    public int[] toIntArray() {
        return this.evaluate().toIntArray();
    }

    @Override
    public long toLong() {
        return this.evaluate().toLong();
    }

    @Override
    public long[] toLongArray() {
        return this.evaluate().toLongArray();
    }
}
