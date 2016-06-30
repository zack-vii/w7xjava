package mds.data.descriptor;

public final class DTYPE{
    private static final String[] _name          = new String[256];
    private static final String[] _suffix        = new String[256];
    public static final byte      MISSING        = 0;
    public static final byte      BU             = 2;
    public static final byte      WU             = 3;
    public static final byte      LU             = 4;
    public static final byte      QU             = 5;
    public static final byte      B              = 6;
    public static final byte      W              = 7;
    public static final byte      L              = 8;
    public static final byte      Q              = 9;
    public static final byte      F              = 10;             // VMS
    public static final byte      D              = 11;             // VMS
    public static final byte      FC             = 12;             // VMS
    public static final byte      DC             = 13;             // VMS
    public static final byte      T              = 14;
    public static final byte      DSC            = 24;
    public static final byte      OU             = 25;
    public static final byte      O              = 26;
    public static final byte      G              = 27;
    public static final byte      H              = 28;
    public static final byte      GC             = 29;             // IEEE
    public static final byte      HC             = 30;             // 128bit
    public static final byte      POINTER        = 51;
    public static final byte      FS             = 52;             // IEEE
    public static final byte      FT             = 53;             // IEEE
    public static final byte      FSC            = 54;             // IEEE
    public static final byte      FTC            = 55;             // IEEE
    public static final byte      IDENT          = -65;            // 191
    public static final byte      NID            = -64;            // 192
    public static final byte      PATH           = -63;            // 193
    public static final byte      PARAM          = -62;            // 194
    public static final byte      SIGNAL         = -61;            // 195
    public static final byte      DIMENSION      = -60;            // 196
    public static final byte      WINDOW         = -59;            // 197
    public static final byte      SLOPE          = -58;            // 198
    public static final byte      FUNCTION       = -57;            // 199
    public static final byte      CONGLOM        = -56;            // 200
    public static final byte      RANGE          = -55;            // 201
    public static final byte      ACTION         = -54;            // 202
    public static final byte      DISPATCH       = -53;            // 203
    public static final byte      PROGRAM        = -52;            // 204
    public static final byte      ROUTINE        = -51;            // 205
    public static final byte      PROCEDURE      = -50;            // 206
    public static final byte      METHOD         = -49;            // 207
    public static final byte      DEPENDENCY     = -48;            // 208
    public static final byte      CONDITION      = -47;            // 209
    public static final byte      EVENT          = -46;            // 210
    public static final byte      WITH_UNITS     = -45;            // 211
    public static final byte      CALL           = -44;            // 212
    public static final byte      WITH_ERROR     = -43;            // 213
    public static final byte      LIST           = -42;            // 214
    public static final byte      TUPLE          = -41;            // 215
    public static final byte      DICTIONARY     = -40;            // 216
    public static final byte      OPAQUE         = -39;            // 217
    public static final byte      FLOAT          = DTYPE.F;
    public static final byte      DOUBLE         = DTYPE.D;
    public static final byte      COMPLEX_FLOAT  = DTYPE.FC;
    public static final byte      COMPLEX_DOUBLE = DTYPE.DC;

    static{
        DTYPE._name[Byte.toUnsignedInt(DTYPE.B)] = "Byte";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.W)] = "Word";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.L)] = "Long";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.Q)] = "Quadword";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.O)] = "Octaword";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.BU)] = "Byte_Unsigned";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WU)] = "Word_Unsigned";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.LU)] = "Long_Unsigned";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.QU)] = "Quadword_Unsigned";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.OU)] = "Octaword_Unsigned";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.D)] = "D_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DC)] = "D_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.F)] = "F_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FC)] = "F_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FS)] = "FS_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FSC)] = "FSC_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FT)] = "FT_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FTC)] = "FT_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.G)] = "G_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.GC)] = "G_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.H)] = "H_Float";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.HC)] = "H_Complex";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.T)] = "Text";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.IDENT)] = "Ident";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.POINTER)] = "Pointer";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.EVENT)] = "Event";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.NID)] = "Nid";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PATH)] = "Path";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DSC)] = "Dsc";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DICTIONARY)] = "Dictionary";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.TUPLE)] = "Tuple";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.LIST)] = "List";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.ACTION)] = "Action";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CALL)] = "Call";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CONDITION)] = "Condition";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CONGLOM)] = "Conglom";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DEPENDENCY)] = "Dependency";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DIMENSION)] = "Dim";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DISPATCH)] = "Dispatch";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FUNCTION)] = "Function";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.METHOD)] = "Method";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.MISSING)] = "$Missing";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.OPAQUE)] = "Opaque";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PARAM)] = "Param";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PROCEDURE)] = "Procedure";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PROGRAM)] = "Program";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.RANGE)] = "Range";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.ROUTINE)] = "Routine";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.SIGNAL)] = "Signal";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.SLOPE)] = "Slope";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WINDOW)] = "Window";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WITH_ERROR)] = "With_Error";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WITH_UNITS)] = "With_Units";
    }

    static{
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.B)] = "B";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.W)] = "W";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.L)] = "L";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.Q)] = "Q";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.O)] = "O";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.BU)] = "BU";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.WU)] = "WU";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.LU)] = "LU";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.QU)] = "QU";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.OU)] = "OU";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.D)] = "V";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.DC)] = "V";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.F)] = "F";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.FC)] = "F";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.FS)] = "E";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.FSC)] = "E";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.FT)] = "D";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.FTC)] = "D";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.G)] = "G";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.GC)] = "G";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.H)] = "H";
        DTYPE._suffix[Byte.toUnsignedInt(DTYPE.HC)] = "H";
    }

    public static final String getName(final byte dtype) {
        return DTYPE._name[Byte.toUnsignedInt(dtype)];
    }

    public static final String getSuffix(final byte dtype) {
        return DTYPE._suffix[Byte.toUnsignedInt(dtype)];
    }
}
