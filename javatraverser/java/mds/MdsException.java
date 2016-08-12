package mds;

import java.awt.Color;
import java.io.IOException;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public final class MdsException extends IOException{
    private static JLabel   statusLabel          = null;
    public static final int MdsERROR             = 0;
    public static final int MdsSUCCESS           = 1;
    /** TdiShr Exceptions **/
    public static final int TdiRECURSIVE         = 265519306;
    public static final int TdiNO_OUTPTR         = 265519274;
    public static final int TdiMISS_ARG          = 265519242;
    public static final int TdiBREAK             = 265519112;
    public static final int TdiNO_OPC            = 265519266;
    public static final int TdiINVCLADTY         = 265519202;
    public static final int TdiNDIM_OVER         = 265519250;
    public static final int TdiSIG_DIM           = 265519314;
    public static final int TdiTIMEOUT           = 265519364;
    public static final int TdiNO_CMPLX          = 265519258;
    public static final int TdiSTRTOOLON         = 265519356;
    public static final int TdiUNKNOWN_VAR       = 265519346;
    public static final int TdiBAD_INDEX         = 265519162;
    public static final int TdiABORT             = 265519154;
    public static final int TdiEXTRA_ARG         = 265519178;
    public static final int TdiCONTINUE          = 265519128;
    public static final int TdiNULL_PTR          = 265519298;
    public static final int TdiNO_SELF_PTR       = 265519282;
    public static final int TdiTOO_BIG           = 265519330;
    public static final int TdiGOTO              = 265519186;
    public static final int TdiRETURN            = 265519144;
    public static final int TdiINVCLADSC         = 265519194;
    public static final int TdiINVDTYDSC         = 265519210;
    public static final int TdiSYNTAX            = 265519322;
    public static final int TdiCASE              = 265519120;
    public static final int TdiEXTRANEOUS        = 265519136;
    public static final int TdiBOMB              = 265519170;
    public static final int TdiINV_SIZE          = 265519226;
    public static final int TdiNOT_NUMBER        = 265519290;
    public static final int TdiINV_OPC           = 265519218;
    public static final int TdiUNBALANCE         = 265519338;
    public static final int TdiMISMATCH          = 265519234;
    /** TreeShr Exceptions **/
    public static final int TreeNEW              = 265388059;
    public static final int TreeALREADY_OPEN     = 265388091;
    public static final int TreeOPEN_EDIT        = 265388123;
    public static final int TreeSUCCESS          = 265389633;
    public static final int TreeRESOLVED         = 265388049;
    public static final int TreeNO_CONTEXT       = 265388099;
    public static final int TreeNORMAL           = 265388041;
    public static final int TreeON               = 265388107;
    public static final int TreeNOTALLSUBS       = 265388067;
    public static final int TreeALREADY_ON       = 265388083;
    public static final int TreeALREADY_OFF      = 265388075;
    public static final int TreeALREADY_THERE    = 265388168;
    public static final int TreeBADRECORD        = 265388218;
    public static final int TreeBOTH_OFF         = 265388184;
    public static final int TreeBUFFEROVF        = 265388306;
    public static final int TreeCANCEL           = 265391232;
    public static final int TreeCLOSEERR         = 265392242;
    public static final int TreeCONGLOM_NOT_FULL = 265388330;
    public static final int TreeCONGLOMFULL      = 265388322;
    public static final int TreeCONNECTFAIL      = 265392170;
    public static final int TreeCONTINUING       = 265390435;
    public static final int TreeDELFAIL          = 265392186;
    public static final int TreeDFREAD           = 265392234;
    public static final int TreeDUPTAG           = 265388234;
    public static final int TreeEDITTING         = 265388434;
    public static final int TreeEMPTY            = 265392200;
    public static final int TreeFAILURE          = 265392034;
    public static final int TreeFCREATE          = 265392162;
    public static final int TreeFILE_NOT_FOUND   = 265392042;
    public static final int TreeFOPENR           = 265392154;
    public static final int TreeFOPENW           = 265392146;
    public static final int TreeILLEGAL_ITEM     = 265388298;
    public static final int TreeILLPAGCNT        = 265388242;
    public static final int TreeINVDFFCLASS      = 265388346;
    public static final int TreeINVDTPUSG        = 265388426;
    public static final int TreeINVDTYPE         = 265392066;
    public static final int TreeINVPATH          = 265388290;
    public static final int TreeINVRECTYP        = 265388354;
    public static final int TreeINVSHAPE         = 265392074;
    public static final int TreeINVSHOT          = 265392090;
    public static final int TreeINVTAG           = 265392106;
    public static final int TreeINVTREE          = 265388226;
    public static final int TreeLOCK_FAILURE     = 265392050;
    public static final int TreeMAXOPENEDIT      = 265388250;
    public static final int TreeMEMERR           = 265392130;
    public static final int TreeMOVEERROR        = 265392250;
    public static final int TreeNCIREAD          = 265392218;
    public static final int TreeNCIWRITE         = 265392178;
    public static final int TreeNMN              = 265388128;
    public static final int TreeNMT              = 265388136;
    public static final int TreeNNF              = 265388144;
    public static final int TreeNOCURRENT        = 265392138;
    public static final int TreeNODATA           = 265388258;
    public static final int TreeNODNAMLEN        = 265388362;
    public static final int TreeNOEDIT           = 265388274;
    public static final int TreeNOLOG            = 265388458;
    public static final int TreeNOMETHOD         = 265388208;
    public static final int TreeNOOVERWRITE      = 265388418;
    public static final int TreeNOPATH           = 265392114;
    public static final int TreeNOSEGMENTS       = 265392058;
    public static final int TreeNOT_CONGLOM      = 265388386;
    public static final int TreeNOT_IN_LIST      = 265388482;
    public static final int TreeNOT_OPEN         = 265388200;
    public static final int TreeNOTCHILDLESS     = 265388282;
    public static final int TreeNOTMEMBERLESS    = 265388402;
    public static final int TreeNOTOPEN          = 265388266;
    public static final int TreeNOTSON           = 265388410;
    public static final int TreeNOVERSION        = 265392226;
    public static final int TreeNOWRITEMODEL     = 265388442;
    public static final int TreeNOWRITESHOT      = 265388450;
    public static final int TreeOFF              = 265388192;
    public static final int TreeOPEN             = 265388115;
    public static final int TreeOPENEDITERR      = 265392258;
    public static final int TreePARENT_OFF       = 265388176;
    public static final int TreePARSEERR         = 265392210;
    public static final int TreeREADERR          = 265388474;
    public static final int TreeREADONLY         = 265388466;
    public static final int TreeRENFAIL          = 265392194;
    public static final int TreeTAGNAMLEN        = 265388370;
    public static final int TreeTNF              = 265388152;
    public static final int TreeTREEFILEREADERR  = 265392122;
    public static final int TreeTREENF           = 265388160;
    public static final int TreeUNRESOLVED       = 265388338;
    public static final int TreeUNSPRTCLASS      = 265388314;
    public static final int TreeUNSUPARRDTYPE    = 265388394;
    public static final int TreeWRITEFIRST       = 265388378;
    /** MdsShr Exceptions **/
    public static final int StrMATCH             = 2393113;
    public static final int LibSTRTRU            = 1409041;
    public static final int LibINVARG            = 1409588;
    public static final int LibINSVIRMEM         = 1409556;
    public static final int StrNOELEM            = 2392600;
    public static final int LibNOTFOU            = 1409652;
    public static final int SsINTOVF             = 1148;
    public static final int StrINVDELIM          = 2392592;
    public static final int LibINVSTRDES         = 1409572;
    public static final int StrNOMATCH           = 2392584;
    public static final int LibQUEWASEMP         = 1409772;
    public static final int LibKEYNOTFOU         = 1409788;
    public static final int StrSTRTOOLON         = 2392180;
    /** MdsDcl Exceptions **/
    public static final int MdsdclERROR          = 134348824;
    public static final int MdsdclTOO_MANY_VALS  = 134349682;
    public static final int MdsdclIVQUAL         = 134349658;
    public static final int MdsdclException      = -1;
    public static final int MdsdclPROMPT_MORE    = 134349666;
    public static final int MdsdclIVVERB         = 134349626;
    public static final int MdsdclABSENT         = 134349632;
    public static final int MdsdclEXIT           = 134348817;
    public static final int MdsdclPRESENT        = 134349617;
    public static final int MdsdclTOO_MANY_PRMS  = 134349674;
    public static final int MdsdclNOTNEGATABLE   = 134349650;
    public static final int MdsdclNEGATED        = 134349640;
    public static final int MdsdclMISSING_VALUE  = 134349690;
    public static final int MdsdclSUCCESS        = 134348809;
    public static final int MdsdclNORMAL         = 134349609;

    public static final String getMdsMessage(final int status) {
        switch(status){
            default:
                return "%MDSPLUS-?-UNKNOWN, Unknown exception " + status;
            /** TdiShr Exceptions **/
            case MdsSUCCESS:
                return "%SS-S-SUCCESS, Success";
            case TdiRECURSIVE:
                return "%TDI-E-RECURSIVE, Overly recursive function, calls itself maybe";
            case TdiNO_OUTPTR:
                return "%TDI-E-NO_OUTPTR, An output pointer is required";
            case TdiMISS_ARG:
                return "%TDI-E-MISS_ARG, Missing argument is required for function";
            case TdiBREAK:
                return "%TDI-W-BREAK, BREAK was not in DO FOR SWITCH or WHILE";
            case TdiNO_OPC:
                return "%TDI-E-NO_OPC, No support for this function, today";
            case TdiINVCLADTY:
                return "%TDI-E-INVCLADTY, Invalid mixture of storage class and data type";
            case TdiNDIM_OVER:
                return "%TDI-E-NDIM_OVER, Number of dimensions is over the allowed 8";
            case TdiSIG_DIM:
                return "%TDI-E-SIG_DIM, Signal dimension does not match data shape";
            case TdiTIMEOUT:
                return "%TDI-F-TIMEOUT, task did not complete in alotted time";
            case TdiNO_CMPLX:
                return "%TDI-E-NO_CMPLX, There are no complex forms of this function";
            case TdiSTRTOOLON:
                return "%TDI-F-STRTOOLON, string is too long (greater than 65535)";
            case TdiUNKNOWN_VAR:
                return "%TDI-E-UNKNOWN_VAR, Unknown/undefined variable name";
            case TdiBAD_INDEX:
                return "%TDI-E-BAD_INDEX, Index or subscript is too small or too big";
            case TdiABORT:
                return "%TDI-E-ABORT, Program requested abort";
            case TdiEXTRA_ARG:
                return "%TDI-E-EXTRA_ARG, Too many arguments for function, watch commas";
            case TdiCONTINUE:
                return "%TDI-W-CONTINUE, CONTINUE was not in DO FOR or WHILE";
            case TdiNULL_PTR:
                return "%TDI-E-NULL_PTR, Null pointer where value needed";
            case TdiNO_SELF_PTR:
                return "%TDI-E-NO_SELF_PTR, No $VALUE is defined for signal or validation";
            case TdiTOO_BIG:
                return "%TDI-E-TOO_BIG, Conversion of number lost significant digits";
            case TdiGOTO:
                return "%TDI-E-GOTO, GOTO target label not found";
            case TdiRETURN:
                return "%TDI-W-RETURN, Extraneous RETURN statement, not from a FUN";
            case TdiINVCLADSC:
                return "%TDI-E-INVCLADSC, Storage class not valid, must be scalar or array";
            case TdiINVDTYDSC:
                return "%TDI-E-INVDTYDSC, Storage data type is not valid";
            case TdiSYNTAX:
                return "%TDI-E-SYNTAX, Bad punctuation or misspelled word or number";
            case TdiCASE:
                return "%TDI-W-CASE, CASE was not in SWITCH statement";
            case TdiEXTRANEOUS:
                return "%TDI-W-EXTRANEOUS, Some characters were unused, bad number maybe";
            case TdiBOMB:
                return "%TDI-E-BOMB, Bad punctuation, could not compile the text";
            case TdiINV_SIZE:
                return "%TDI-E-INV_SIZE, Number of elements does not match declaration";
            case TdiNOT_NUMBER:
                return "%TDI-E-NOT_NUMBER, Value is not a scalar number and must be";
            case TdiINV_OPC:
                return "%TDI-E-INV_OPC, Invalid operator code in a function";
            case TdiUNBALANCE:
                return "%TDI-E-UNBALANCE, Unbalanced () [] {} ''  or /**/";
            case TdiMISMATCH:
                return "%TDI-E-MISMATCH, Shape of arguments does not match";
            /** TreeShr Exceptions **/
            case TreeNEW:
                return "%TREE-I-NEW, New tree created";
            case TreeNOVERSION:
                return "%TREE-E-NOVERSION, No version available.";
            case TreeNOTCHILDLESS:
                return "%TREE-E-NOTCHILDLESS, Node must be childless to become subtree reference";
            case TreeALREADY_THERE:
                return "%TREE-W-ALREADY_THERE, Node is already in the tree";
            case TreeNMN:
                return "%TREE-W-NMN, No More Nodes";
            case TreeUNRESOLVED:
                return "%TREE-E-UNRESOLVED, Not an indirect node reference: No action taken";
            case TreeSUCCESS:
                return "%TREE-S-SUCCESS, Operation successful";
            case TreeNCIWRITE:
                return "%TREE-E-NCIWRITE, Error writing node characterisitics to file.";
            case TreeLOCK_FAILURE:
                return "%TREE-E-LOCK_FAILURE, Error locking file, perhaps NFSLOCKING not enabled on this system";
            case TreeOPEN_EDIT:
                return "%TREE-I-OPEN_EDIT, Tree is OPEN for edit";
            case TreeNOWRITEMODEL:
                return "%TREE-E-NOWRITEMODEL, Data for this node can not be written into the MODEL file";
            case TreeINVPATH:
                return "%TREE-E-INVPATH, Invalid tree pathname specified";
            case TreeINVTAG:
                return "%TREE-E-INVTAG, Invalid tagname - must begin with alpha followed by 0-22 alphanumeric or underscores";
            case TreeNOLOG:
                return "%TREE-E-NOLOG, Experiment pathname (xxx_path) not defined";
            case TreeILLPAGCNT:
                return "%TREE-E-ILLPAGCNT, Illegal page count, error mapping tree file";
            case TreeNOMETHOD:
                return "%TREE-W-NOMETHOD, Method not available for this object";
            case TreeNOPATH:
                return "%TREE-E-NOPATH, No 'treename'_path environment variable defined. Cannot locate tree files.";
            case TreeRENFAIL:
                return "%TREE-E-RENFAIL, Error renaming file.";
            case TreeREADONLY:
                return "%TREE-E-READONLY, Tree was opened with readonly access";
            case TreeNOTSON:
                return "%TREE-E-NOTSON, Subtree reference cannot be a member";
            case TreeNOTMEMBERLESS:
                return "%TREE-E-NOTMEMBERLESS, Subtree reference can not have members";
            case TreeRESOLVED:
                return "%TREE-S-RESOLVED, Indirect reference successfully resolved";
            case TreeFAILURE:
                return "%TREE-E-FAILURE, Operation NOT successful";
            case TreeNO_CONTEXT:
                return "%TREE-I-NO_CONTEXT, There is no active search to end";
            case TreeTAGNAMLEN:
                return "%TREE-E-TAGNAMLEN, Tagname too long (max 24 chars)";
            case TreeFILE_NOT_FOUND:
                return "%TREE-E-FILE_NOT_FOUND, File or Directory Not Found";
            case TreeDELFAIL:
                return "%TREE-E-DELFAIL, Error deleting file.";
            case TreeINVTREE:
                return "%TREE-E-INVTREE, Invalid tree identification structure";
            case TreeNODATA:
                return "%TREE-E-NODATA, No data available for this node";
            case TreeNOEDIT:
                return "%TREE-E-NOEDIT, Tree file is not open for edit";
            case TreeUNSPRTCLASS:
                return "%TREE-E-UNSPRTCLASS, Unsupported descriptor class";
            case TreePARENT_OFF:
                return "%TREE-W-PARENT_OFF, Parent of this node is OFF";
            case TreeINVSHAPE:
                return "%TREE-E-INVSHAPE, Invalid shape for this data segment";
            case TreeNODNAMLEN:
                return "%TREE-E-NODNAMLEN, Node name too long (12 chars max)";
            case TreeBUFFEROVF:
                return "%TREE-E-BUFFEROVF, Output buffer overflow";
            case TreeCONGLOM_NOT_FULL:
                return "%TREE-E-CONGLOM_NOT_FULL, Current conglomerate is not yet full";
            case TreeUNSUPARRDTYPE:
                return "%TREE-E-UNSUPARRDTYPE, Complex data types not supported as members of arrays";
            case TreeINVDFFCLASS:
                return "%TREE-E-INVDFFCLASS, Invalid data fmt: only CLASS_S can have data in NCI";
            case TreeWRITEFIRST:
                return "%TREE-E-WRITEFIRST, Tree has been modified:  write or quit first";
            case TreeMEMERR:
                return "%TREE-E-MEMERR, Memory allocation error.";
            case TreeINVSHOT:
                return "%TREE-E-INVSHOT, Invalid shot number - must be -1 (model), 0 (current), or Positive";
            case TreeDFREAD:
                return "%TREE-E-DFREAD, Error reading from datafile.";
            case TreeNORMAL:
                return "%TREE-S-NORMAL, Normal successful completion";
            case TreeNCIREAD:
                return "%TREE-E-NCIREAD, Error reading node characteristics from file.";
            case TreeNOT_OPEN:
                return "%TREE-W-NOT_OPEN, Tree not currently open";
            case TreeTNF:
                return "%TREE-W-TNF, Tag Not Found";
            case TreePARSEERR:
                return "%TREE-E-PARSEERR, Invalid node search string.";
            case TreeOPENEDITERR:
                return "%TREE-E-OPENEDITERR, Error reopening new treefile for write access.";
            case TreeNOTALLSUBS:
                return "%TREE-I-NOTALLSUBS, Main tree opened but not all subtrees found/or connected";
            case TreeTREENF:
                return "%TREE-W-TREENF, Tree Not Found";
            case TreeNOT_IN_LIST:
                return "%TREE-E-NOT_IN_LIST, Tree being opened was not in the list";
            case TreeMOVEERROR:
                return "%TREE-E-MOVEERROR, Error replacing original treefile with new one.";
            case TreeCONTINUING:
                return "%TREE-I-CONTINUING, Operation continuing: note following error";
            case TreeNOSEGMENTS:
                return "%TREE-E-NOSEGMENTS, No segments exist in this node";
            case TreeNOWRITESHOT:
                return "%TREE-E-NOWRITESHOT, Data for this node can not be written into the SHOT file";
            case TreeMAXOPENEDIT:
                return "%TREE-E-MAXOPENEDIT, Too many files open for edit";
            case TreeFOPENR:
                return "%TREE-E-FOPENR, Error opening file read-only.";
            case TreeFOPENW:
                return "%TREE-E-FOPENW, Error opening file for read-write.";
            case TreeEDITTING:
                return "%TREE-E-EDITTING, Tree file open for edit: operation not permitted";
            case TreeCONNECTFAIL:
                return "%TREE-E-CONNECTFAIL, Error connecting to remote server.";
            case TreeNOOVERWRITE:
                return "%TREE-E-NOOVERWRITE, Write-once node: overwrite not permitted";
            case TreeNMT:
                return "%TREE-W-NMT, No More Tags";
            case TreeNOCURRENT:
                return "%TREE-E-NOCURRENT, No current shot number set for this tree.";
            case TreeINVRECTYP:
                return "%TREE-E-INVRECTYP, Record type invalid for requested operation";
            case TreeALREADY_ON:
                return "%TREE-I-ALREADY_ON, Node is already ON";
            case TreeCANCEL:
                return "%TREE-W-CANCEL, User canceled operation";
            case TreeREADERR:
                return "%TREE-E-READERR, Error reading record for node";
            case TreeOPEN:
                return "%TREE-I-OPEN, Tree is OPEN (no edit)";
            case TreeINVDTPUSG:
                return "%TREE-E-INVDTPUSG, Attempt to store datatype which conflicts with the designated usage of this node";
            case TreeBADRECORD:
                return "%TREE-E-BADRECORD, Data corrupted: cannot read record";
            case TreeCLOSEERR:
                return "%TREE-E-CLOSEERR, Error closing temporary tree file.";
            case TreeEMPTY:
                return "%TREE-W-EMPTY, Empty string provided.";
            case TreeNOT_CONGLOM:
                return "%TREE-E-NOT_CONGLOM, Head node of conglomerate does not contain a DTYPE_CONGLOM record";
            case TreeALREADY_OPEN:
                return "%TREE-I-ALREADY_OPEN, Tree is already OPEN";
            case TreeOFF:
                return "%TREE-W-OFF, Node is OFF";
            case TreeON:
                return "%TREE-I-ON, Node is ON";
            case TreeCONGLOMFULL:
                return "%TREE-E-CONGLOMFULL, Current conglomerate is full";
            case TreeBOTH_OFF:
                return "%TREE-W-BOTH_OFF, Both this node and its parent are off";
            case TreeNOTOPEN:
                return "%TREE-E-NOTOPEN, No tree file currently open";
            case TreeDUPTAG:
                return "%TREE-E-DUPTAG, Tag name already in use";
            case TreeILLEGAL_ITEM:
                return "%TREE-E-ILLEGAL_ITEM, Invalid item code or part number specified";
            case TreeNNF:
                return "%TREE-W-NNF, Node Not Found";
            case TreeALREADY_OFF:
                return "%TREE-I-ALREADY_OFF, Node is already OFF";
            case TreeFCREATE:
                return "%TREE-E-FCREATE, Error creating new file.";
            case TreeINVDTYPE:
                return "%TREE-E-INVDTYPE, Invalid datatype for data segment";
            case TreeTREEFILEREADERR:
                return "%TREE-E-TREEFILEREADERR, Error reading in tree file contents.";
            /** MdsShr Exceptions **/
            case StrMATCH:
                return "%STR-S-MATCH, Strings match";
            case LibINVARG:
                return "%LIB-F-INVARG, Invalid argument";
            case LibINSVIRMEM:
                return "%LIB-F-INSVIRMEM, Insufficient virtual memory";
            case StrNOELEM:
                return "%STR-W-NOELEM, Not enough delimited characters";
            case LibNOTFOU:
                return "%LIB-F-NOTFOU, Entity not found";
            case SsINTOVF:
                return "%SS-F-INTOVF, Integer overflow";
            case LibSTRTRU:
                return "%LIB-S-STRTRU, String truncated";
            case StrINVDELIM:
                return "%STR-W-INVDELIM, Not enough delimited characters";
            case LibINVSTRDES:
                return "%LIB-F-INVSTRDES, Invalid string descriptor";
            case StrNOMATCH:
                return "%STR-W-NOMATCH, Strings do not match";
            case LibQUEWASEMP:
                return "%LIB-F-QUEWASEMP, Queue was empty";
            case LibKEYNOTFOU:
                return "%LIB-F-KEYNOTFOU, Key not found";
            case StrSTRTOOLON:
                return "%STR-F-STRTOOLON, String too long";
            /** MdsDcl Exceptions **/
            case MdsdclERROR:
                return "%MDSDCL-W-ERROR, Unsuccessful execution of command";
            case MdsdclTOO_MANY_VALS:
                return "%MDSDCL-E-TOO_MANY_VALS, Too many values";
            case MdsdclIVQUAL:
                return "%MDSDCL-E-IVQUAL, Invalid qualifier";
            case MdsdclException:
                return "%MDSPLUS-?-UNKNOWN, Unknown exception";
            case MdsdclPROMPT_MORE:
                return "%MDSDCL-E-PROMPT_MORE, More input required for command";
            case MdsdclIVVERB:
                return "%MDSDCL-E-IVVERB, No such command";
            case MdsdclABSENT:
                return "%MDSDCL-W-ABSENT, Entity is absent";
            case MdsdclEXIT:
                return "%MDSDCL-S-EXIT, Normal exit";
            case MdsdclPRESENT:
                return "%MDSDCL-S-PRESENT, Entity is present";
            case MdsdclTOO_MANY_PRMS:
                return "%MDSDCL-E-TOO_MANY_PRMS, Too many parameters specified";
            case MdsdclNOTNEGATABLE:
                return "%MDSDCL-E-NOTNEGATABLE, Entity cannot be negated";
            case MdsdclNEGATED:
                return "%MDSDCL-W-NEGATED, Entity is present but negated";
            case MdsdclMISSING_VALUE:
                return "%MDSDCL-E-MISSING_VALUE, Qualifier value needed";
            case MdsdclSUCCESS:
                return "%MDSDCL-S-SUCCESS, Normal successful completion";
            case MdsdclNORMAL:
                return "%MDSDCL-S-NORMAL, Normal successful completion";
        }
    }

    public final static void handleStatus(final int status) throws MdsException {
        final String msg = MdsException.getMdsMessage(status);
        final boolean success = (status & 1) == 1;
        if(!success){
            final MdsException exc = new MdsException(msg, status);
            MdsException.stderr(null, exc);
            throw exc;
        }
        MdsException.stdout(msg);
    }

    private static final String parseMessage(final String message) {
        final String[] parts = message.split(":", 2);
        return parts[parts.length - 1];
    }

    public static final void setStatusLabel(final JLabel status) {
        MdsException.statusLabel = status;
    }

    public static void stderr(final String line, final Exception exc) {
        if(MdsException.statusLabel != null) MdsException.statusLabel.setForeground(Color.RED);
        if(line == null){
            if(exc == null){
                if(MdsException.statusLabel != null) MdsException.statusLabel.setText("");
                return;
            }
            final String msg = exc.toString();
            if(MdsException.statusLabel != null) MdsException.statusLabel.setText(msg);
            System.err.println(msg);
        }else if(exc == null){
            if(MdsException.statusLabel != null) MdsException.statusLabel.setText(String.format("ERROR: %s", line));
            System.err.println(String.format("%s", line));
        }else{
            final String msg = exc.getMessage();
            if(MdsException.statusLabel != null) MdsException.statusLabel.setText(String.format("ERROR: %s (%s)", line, msg));
            System.err.println(String.format("%s\n%s", line, msg));
        }
    }

    public static void stdout(final String line) {
        if(MdsException.statusLabel == null) return;
        MdsException.statusLabel.setForeground(Color.BLACK);
        MdsException.statusLabel.setText(line);
    }
    private final int status;

    public MdsException(final int status){
        this(MdsException.getMdsMessage(status), status);
    }

    public MdsException(final String message){
        super(MdsException.parseMessage(message));
        int status = 0;
        try{
            final String[] parts = message.split(":", 2);
            if(parts.length > 1) status = Integer.parseInt(parts[0]);
        }catch(final Exception exc){}
        this.status = status;
    }

    public MdsException(final String header, final Exception e){
        super(String.format("%s: %s", header, e.getMessage()));
        this.status = 0;
    }

    public MdsException(final String message, final int status){
        super(message);
        this.status = status;
    }

    public final int getStatus() {
        return this.status;
    }
}