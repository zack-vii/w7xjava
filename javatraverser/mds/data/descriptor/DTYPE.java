package mds.data.descriptor;

public final class DTYPE{
    private static final String[] _name      = new String[256];
    private static final String[] _suffix    = new String[256];
    public static final byte      ACTION     = -54;            // 202
    public static final byte      B          = 6;
    public static final byte      BU         = 2;
    public static final byte      CALL       = -44;            // 212
    public static final byte      CONDITION  = -47;            // 209
    public static final byte      CONGLOM    = -56;            // 200
    public static final byte      D          = 11;             // VMS
    public static final byte      DC         = 13;             // VMS
    public static final byte      DEPENDENC  = -48;            // 208
    public static final byte      DICTIONARY = -40;            // 216
    public static final byte      DIMENSION  = -60;            // 196
    public static final byte      DISPATCH   = -53;            // 203
    public static final byte      DSC        = 24;
    public static final byte      EVENT      = -46;            // 210
    public static final byte      F          = 10;             // VMS
    public static final byte      FC         = 12;             // VMS
    public static final byte      FS         = 52;             // IEEE
    public static final byte      FSC        = 54;             // IEEE
    public static final byte      FT         = 53;             // IEEE
    public static final byte      FTC        = 55;             // IEEE
    public static final byte      FUNCTION   = -57;            // 199
    public static final byte      G          = 27;
    public static final byte      GC         = 29;             // IEEE
    public static final byte      H          = 28;
    public static final byte      HC         = 30;             // 128bit
    public static final byte      IDENT      = -65;            // 191
    public static final byte      L          = 8;
    public static final byte      LIST       = -42;            // 214
    public static final byte      LU         = 4;
    public static final byte      METHOD     = -49;            // 207
    public static final byte      MISSING    = 0;
    public static final byte      NID        = -64;            // 192
    public static final byte      O          = 26;
    public static final byte      OPAQUE     = -43;            // 213
    public static final byte      OU         = 25;
    public static final byte      PARAM      = -62;            // 194
    public static final byte      PATH       = -63;            // 193
    public static final byte      POINTER    = 51;
    public static final byte      PROCEDURE  = -50;            // 206
    public static final byte      PROGRAM    = -52;            // 204
    public static final byte      Q          = 9;
    public static final byte      QU         = 5;
    public static final byte      RANGE      = -55;            // 201
    public static final byte      ROUTINE    = -51;            // 205
    public static final byte      SIGNAL     = -61;            // 195
    public static final byte      SLOPE      = -58;            // 198
    public static final byte      T          = 14;
    public static final byte      TUPLE      = -41;            // 215
    public static final byte      W          = 7;
    public static final byte      WINDOW     = -59;            // 197
    public static final byte      WITH_ERROR = -39;            // 217
    public static final byte      WITH_UNITS = -45;            // 211
    public static final byte      WU         = 3;

    static{
        DTYPE._name[Byte.toUnsignedInt(DTYPE.B)] = "BYTE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.W)] = "WORD";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.L)] = "LONG";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.Q)] = "QUADWORD";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.O)] = "OCTAWORD";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.BU)] = "BYTE_UNSIGNED";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WU)] = "WORD_UNSIGNED";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.LU)] = "LONG_UNSIGNED";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.QU)] = "QUADWORD_UNSIGNED";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.OU)] = "OCTAWORD_UNSIGNED";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.D)] = "D_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DC)] = "D_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.F)] = "F_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FC)] = "F_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FS)] = "FS_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FSC)] = "FSC_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FT)] = "FT_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FTC)] = "FT_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.G)] = "G_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.GC)] = "G_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.H)] = "H_FLOAT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.HC)] = "H_COMPLEX";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.T)] = "TEXT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.IDENT)] = "IDENT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.POINTER)] = "POINTER";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.EVENT)] = "EVENT";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.NID)] = "NID";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PATH)] = "PATH";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DSC)] = "DSC";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DICTIONARY)] = "DICTIONARY";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.TUPLE)] = "TUPLE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.LIST)] = "LIST";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.ACTION)] = "ACTION";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CALL)] = "CALL";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CONDITION)] = "CONDITION";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.CONGLOM)] = "CONGLOM";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DEPENDENC)] = "DEPENDENC";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DIMENSION)] = "DIM";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.DISPATCH)] = "DISPATCH";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.FUNCTION)] = "FUNCTION";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.METHOD)] = "METHOD";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.MISSING)] = "$MISSING";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.OPAQUE)] = "OPAQUE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PARAM)] = "PARAM";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PROCEDURE)] = "PROCEDURE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.PROGRAM)] = "PROGRAM";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.RANGE)] = "RANGE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.ROUTINE)] = "ROUTINE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.SIGNAL)] = "SIGNAL";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.SLOPE)] = "SLOPE";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WINDOW)] = "WINDOW";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WITH_ERROR)] = "WITH_ERROR";
        DTYPE._name[Byte.toUnsignedInt(DTYPE.WITH_UNITS)] = "WITH_UNITS";
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

    public static void main(final String[] a) throws Exception {// TODO main
        System.out.println(DTYPE.getName((byte)1));
    }
}
