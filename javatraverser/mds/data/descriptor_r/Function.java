package mds.data.descriptor_r;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import mds.MdsException;
import mds.data.descriptor.DTYPE;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor.Descriptor_R;
import mds.data.descriptor_s.CString;

public final class Function extends Descriptor_R<Short>{
    private final class Arguments extends ArrayList<Descriptor>{
        private static final long serialVersionUID = 1L;
        private final boolean     deco;

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
    public static final String[] OPC = new String[]{"$", "$A0", "$ALPHA", "$AMU", "$C", //
    "$CAL", "$DEGREE", "$EV", "$FALSE", "$FARADAY", //
    "$G", "$GAS", "$H", "$HBAR", "$I", "$K", "$ME", //
    "$MISSING", "$MP", "$N0", "$NA", "$P0", "$PI", //
    "$QE", "$RE", "$ROPRAND", "$RYDBERG", "$T0", //
    "$TORR", "$TRUE", "$VALUE", "ABORT", "ABS", "ABS1", //
    "ABSSQ", "ACHAR", "ACOS", "ACOSD", "ADD", "ADJUSTL", //
    "ADJUSTR", "AIMAG", "AINT", "ALL", "ALLOCATED", //
    "AND", "AND_NOT", "ANINT", "ANY", "ARG", "ARGD", //
    "ARG_OF", "ARRAY", "ASIN", "ASIND", "AS_IS", "ATAN", //
    "ATAN2", "ATAN2D", "ATAND", "ATANH", "AXIS_OF", //
    "BACKSPACE", "IBCLR", "BEGIN_OF", "IBITS", "BREAK", //
    "BSEARCH", "IBSET", "BTEST", "BUILD_ACTION", //
    "BUILD_CONDITION", "BUILD_CONGLOM", "BUILD_DEPENDENC", //
    "BUILD_DIM", "BUILD_DISPATCH", "BUILD_EVENT", //
    "BUILD_FUNCTION", "BUILD_METHOD", "BUILD_PARAM", //
    "BUILD_PATH", "BUILD_PROCEDURE", "BUILD_PROGRAM", //
    "BUILD_RANGE", "BUILD_ROUTINE", "BUILD_SIGNAL", //
    "BUILD_SLOPE", "BUILD_WINDOW", "BUILD_WITH_UNIT", //
    "BUILTIN_OPCODE", "BYTE", "BYTE_UNSIGNED", "CASE", //
    "CEILING", "CHAR", "CLASS", "FCLOSE", "CMPLX", //
    "COMMA", "COMPILE", "COMPLETION_OF", "CONCAT", //
    "CONDITIONAL", "CONJG", "CONTINUE", "CONVOLVE", //
    "COS", "COSD", "COSH", "COUNT", "CSHIFT", "CVT", //
    "DATA", "DATE_AND_TIME", "DATE_TIME", "DBLE", //
    "DEALLOCATE", "DEBUG", "DECODE", "DECOMPILE", //
    "DECOMPRESS", "DEFAULT", "DERIVATIVE", "DESCR", //
    "DIAGONAL", "DIGITS", "DIM", "DIM_OF", "DISPATCH_OF", //
    "DIVIDE", "LBOUND", "DO", "DOT_PRODUCT", "DPROD", //
    "DSCPTR", "SHAPE", "SIZE", "KIND", "UBOUND", "D_COMPLEX", //
    "D_FLOAT", "RANGE", "PRECISION", "ELBOUND", "ELSE", //
    "ELSEWHERE", "ENCODE", "ENDFILE", "END_OF", "EOSHIFT", //
    "EPSILON", "EQ", "EQUALS", "EQUALS_FIRST", "EQV", "ESHAPE", //
    "ESIZE", "EUBOUND", "EVALUATE", "EXECUTE", "EXP", "EXPONENT", //
    "EXT_FUNCTION", "FFT", "FIRSTLOC", "FIT", "FIX_ROPRAND", //
    "FLOAT", "FLOOR", "FOR", "FRACTION", "FUN", "F_COMPLEX", //
    "F_FLOAT", "GE", "GETNCI", "GOTO", "GT", "G_COMPLEX", //
    "G_FLOAT", "HELP_OF", "HUGE", "H_COMPLEX", "H_FLOAT", //
    "IACHAR", "IAND", "IAND_NOT", "ICHAR", "IDENT_OF", "IF", //
    "IF_ERROR", "IMAGE_OF", "IN", "INAND", "INAND_NOT", //
    "INDEX", "INOR", "INOR_NOT", "INOT", "INOUT", "INQUIRE", //
    "INT", "INTEGRAL", "INTERPOL", "INTERSECT", "INT_UNSIGNED", //
    "INVERSE", "IOR", "IOR_NOT", "IS_IN", "IEOR", "IEOR_NOT", //
    "LABEL", "LAMINATE", "LANGUAGE_OF", "LASTLOC", "LE", "LEN", //
    "LEN_TRIM", "LGE", "LGT", "LLE", "LLT", "LOG", "LOG10", "LOG2", //
    "LOGICAL", "LONG", "LONG_UNSIGNED", "LT", "MATMUL", "MAT_ROT", //
    "MAT_ROT_INT", "MAX", "MAXEXPONENT", "MAXLOC", "MAXVAL", "MEAN", //
    "MEDIAN", "MERGE", "METHOD_OF", "MIN", "MINEXPONENT", "MINLOC", //
    "MINVAL", "MOD", "MODEL_OF", "MULTIPLY", "NAME_OF", "NAND", //
    "NAND_NOT", "NDESC", "NE", "NEAREST", "NEQV", "NINT", "NOR", //
    "NOR_NOT", "NOT", "OBJECT_OF", "OCTAWORD", "OCTAWORD_UNSIGNED", //
    "ON_ERROR", "OPCODE_BUILTIN", "OPCODE_STRING", "FOPEN", "OPTIONAL", //
    "OR", "OR_NOT", "OUT", "PACK", "PHASE_OF", "POST_DEC", "POST_INC", //
    "POWER", "PRESENT", "PRE_DEC", "PRE_INC", "PRIVATE", "PROCEDURE_OF", //
    "PRODUCT", "PROGRAM_OF", "PROJECT", "PROMOTE", "PUBLIC", "QUADWORD", //
    "QUADWORD_UNSIGNED", "QUALIFIERS_OF", "RADIX", "RAMP", "RANDOM", //
    "RANDOM_SEED", "DTYPE_RANGE", "RANK", "RAW_OF", "READ", "REAL", //
    "REBIN", "REF", "REPEAT", "REPLICATE", "RESHAPE", "RETURN", "REWIND", //
    "RMS", "ROUTINE_OF", "RRSPACING", "SCALE", "SCAN", "FSEEK", //
    "SET_EXPONENT", "SET_RANGE", "ISHFT", "ISHFTC", "SHIFT_LEFT", //
    "SHIFT_RIGHT", "SIGN", "SIGNED", "SIN", "SIND", "SINH", "SIZEOF", //
    "SLOPE_OF", "SMOOTH", "SOLVE", "SORTVAL", "SPACING", "SPAWN", "SPREAD", //
    "SQRT", "SQUARE", "STATEMENT", "STD_DEV", "STRING", "STRING_OPCODE", //
    "SUBSCRIPT", "SUBTRACT", "SUM", "SWITCH", "SYSTEM_CLOCK", "TAN", //
    "TAND", "TANH", "TASK_OF", "TEXT", "TIME_OUT_OF", "TINY", "TRANSFER", //
    "TRANSPOSE_", "TRIM", "UNARY_MINUS", "UNARY_PLUS", "UNION", "UNITS", //
    "UNITS_OF", "UNPACK", "UNSIGNED", "VAL", "VALIDATION_OF", "VALUE_OF", //
    "VAR", "VECTOR", "VERIFY", "WAIT", "WHEN_OF", "WHERE", "WHILE", //
    "WINDOW_OF", "WORD", "WORD_UNSIGNED", "WRITE", "ZERO", "$2PI", "$NARG", //
    "ELEMENT", "RC_DROOP", "RESET_PRIVATE", "RESET_PUBLIC", "SHOW_PRIVATE", //
    "SHOW_PUBLIC", "SHOW_VM", "TRANSLATE", "TRANSPOSE_MUL", "UPCASE", //
    "USING", "VALIDATION", "$DEFAULT", "$EXPT", "$SHOT", "GETDBI", "CULL", //
    "EXTEND", "I_TO_X", "X_TO_I", "MAP", "COMPILE_DEPENDENCY", //
    "DECOMPILE_DEPENDENCY", "BUILD_CALL", "ERRORLOGS_OF", "PERFORMANCE_OF", //
    "XD", "CONDITION_OF", "SORT", "$THIS", "DATA_WITH_UNITS", "$ATM", //
    "$EPSILON0", "$GN", "$MU0", "EXTRACT", "FINITE", "BIT_SIZE", "MODULO", //
    "SELECTED_INT_KIND", "SELECTED_REAL_KIND", "DSQL", "ISQL", "FTELL", //
    "MAKE_ACTION", "MAKE_CONDITION", "MAKE_CONGLOM", "MAKE_DEPENDENCY", //
    "MAKE_DIM", "MAKE_DISPATCH", "MAKE_FUNCTION", "MAKE_METHOD", "MAKE_PARAM", //
    "MAKE_PROCEDURE", "MAKE_PROGRAM", "MAKE_RANGE", "MAKE_ROUTINE", //
    "MAKE_SIGNAL", "MAKE_WINDOW", "MAKE_WITH_UNITS", "MAKE_CALL", "CLASS_OF", //
    "DSCPTR_OF", "KIND_OF", "NDESC_OF", "ACCUMULATE", "MAKE_SLOPE", "REM", //
    "COMPLETION_MESSAGE_OF", "INTERRUPT_OF", "$SHOTNAME", "BUILD_WITH_ERROR", //
    "ERROR_OF", "MAKE_WITH_ERROR", "DO_TASK", "ISQL_SET", "FS_FLOAT", //
    "FS_COMPLEX", "FT_FLOAT", "FT_COMPLEX", "BUILD_OPAQUE", "MAKE_OPAQUE"};

    private static final String _default(final short opcode, final Arguments args) {
        final String fun = Function.OPC[opcode];
        if(args.isEmpty() && fun.startsWith("$")) return fun;
        return String.format("%s(%s)", fun, args.join(", ", 0));
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

    private static final String add(final short opcode, final Arguments args) {
        return args.join(" + ", 0);
    }

    private static final String concat(final short opcode, final Arguments args) {
        return args.join(" // ", 0);
    }

    private static final String devide(final short opcode, final Arguments args) {
        return args.join(" / ", 0);
    }

    private static String eq(final short opcode, final Arguments args) {
        return args.join(" == ", 0);
    }

    private static String equals(final short opcode, final Arguments args) {
        return args.join(" = ", 0);
    }

    private static String equals_first(final short opcode, final Arguments args) {
        return Function._default(opcode, args);
    }

    private static final String ext_function(final short opcode, final Arguments args) {
        if(args.size() < 2 || (args.get(0) != null) || !(args.get(1) instanceof CString)) return Function._default(opcode, args);
        return String.format("%s(%s)", args.get(1).getValue(), args.join(", ", 2));
    }

    private static String ge(final short opcode, final Arguments args) {
        return args.join(" >= ", 0);
    }

    private static String gt(final short opcode, final Arguments args) {
        return args.join(" > ", 0);
    }

    private static final String if_errorX(final short opcode, final Arguments args) {
        return String.format("%s(\r\n\t%s\n)", Function.OPC[opcode], args.join(",\r\n\t", 0));
    }

    private static String le(final short opcode, final Arguments args) {
        return args.join(" <= ", 0);
    }

    private static String lt(final short opcode, final Arguments args) {
        return args.join(" < ", 0);
    }

    private static final String multiply(final short opcode, final Arguments args) {
        return args.join(" * ", 0);
    }

    private static final String ne(final short opcode, final Arguments args) {
        return args.join(" != ", 0);
    }

    private static final String not(final short opcode, final Arguments args) {
        if(args.size() == 1) return "!" + Descriptor.decompile(args.get(0));
        return Function._default(opcode, args);
    }

    private static final String power(final short opcode, final Arguments args) {
        return args.join(" ^ ", 0);
    }

    private static String shift_left(final short opcode, final Arguments args) {
        return args.join(" << ", 0);
    }

    private static String shift_right(final short opcode, final Arguments args) {
        return args.join(" >> ", 0);
    }

    private static final String statementX(final short opcode, final Arguments args) {
        return args.join(";\r\n", 0) + ";\r\n";
    }

    private static final String subtract(final short opcode, final Arguments args) {
        return args.join(" - ", 0);
    }

    private static final String vector(final short opcode, final Arguments args) {
        return String.format("[%s]", args.join(",", 0));
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
        return this.toString(true);
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
                return this.toString();
        }
    }

    public final Descriptor getArgument(final int idx) {
        return this.dscptrs[idx];
    }

    public final Descriptor[] getArguments() {
        return this.dscptrs;
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
    public final String toString() {
        return this.toString(false);
    }

    private final String toString(final boolean preview) {
        final short opcode = this.getOpCode();
        if(opcode == 0) return super.toString();
        final Arguments args = new Arguments(this.dscptrs, 0, preview);
        switch(opcode){
            case (38):
                return Function.add(opcode, args);
            case (101):
                return Function.concat(opcode, args);
            case (129):
                return Function.devide(opcode, args);
            case (151):
                return Function.eq(opcode, args);
            case (152):
                return Function.equals(opcode, args);
            case (153):
                return Function.equals_first(opcode, args);
            case (162):
                return Function.ext_function(opcode, args);
            case (174):
                return Function.ge(opcode, args);
            case (177):
                return Function.gt(opcode, args);
            case (216):
                return Function.le(opcode, args);
            case (229):
                return Function.lt(opcode, args);
            case (247):
                return Function.multiply(opcode, args);
            case (252):
                return Function.ne(opcode, args);
            case (258):
                return Function.not(opcode, args);
            case (274):
                return Function.power(opcode, args);
            case (314):
                return Function.shift_left(opcode, args);
            case (315):
                return Function.shift_right(opcode, args);
            case (336):
                return Function.subtract(opcode, args);
            case (361):
                return Function.vector(opcode, args);
            default:
                return Function._default(opcode, args);
        }
    }
}
