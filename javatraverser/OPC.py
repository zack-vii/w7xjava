def opc(filepath):
    with file(filepath) as f:
        lines = f.readlines()
    i = 0;
    for line in lines:
        if not line.startswith('OPC ('):
            continue
        name = line[5:].split(')',2)[0].split(',',2)[0].strip('\t ')
        print('public static final short Opc%s = %d;'%(name,i))
        i+=1;

def binary(filepath):
    with file(filepath) as f:
        lines = f.readlines()
    go = False
    i = 0
    print('private static final op_rec[] binary    = new op_rec[]{//')
    for line in lines:
        if not go:
            if line.find('binary[]')>=0:
                go = True
            continue
        if line.strip(' \n\t{')=='':
            continue
        args = line.split('}',2)[0].strip('\t ').split(',')
        symbol = ','.join(args[0:-3])
        if symbol=='0':
            symbol='null';
        opcode = args[-3].strip(' ')
        if opcode=='0':
            break
        prec = int(args[-2])
        lorr = int(args[-1])
        print('new op_rec(%s,OPC.%s,(byte)%d,(byte)%d),//%d'%(symbol,opcode,prec,lorr,i))
        i += 1
    print('};')

def unary(filepath):
    with file(filepath) as f:
        lines = f.readlines()
    go = False
    i = 0
    print('private static final op_rec[] unary    = new op_rec[]{//')
    for line in lines:
        if not go:
            if line.find('unary[]')>=0:
                go = True
            continue
        if line.strip(' \n\t{')=='':
            continue
        args = line.split('}',2)[0].strip('\t ').split(',')
        symbol = ','.join(args[0:-3])
        if symbol=='0':
            symbol='null';
        opcode = args[-3].strip(' ')
        if opcode=='0':
            break
        prec = int(args[-2])
        lorr = int(args[-1])
        print('new op_rec(%s,OPC.%s,(byte)%d,(byte)%d),//%d'%(symbol,opcode,prec,lorr,i))
        i += 1
    print('};')

def defcat(filepath):
    with file(filepath) as f:
        lines = f.readlines()
    go = False
    i = 0
    print('private static final def_cat[] tdiDefCat = new def_cat[]{//')
    for line in lines:
        if not go:
            if line.find('TdiREF_CAT[]')>=0:
                go = True
            continue
        if line.strip(' \n\t{')=='':
            continue
        if line.strip(' \n\t{')=='};':
            break
        args = line.split('}',2)[0].strip('{\t ').split(',')
        name = args[0]
        cat = args[1].strip(' ')
        if name=='"P"':
            length = 8;
        else:
            length = int(args[2])
        digits = int(args[3])
        fname = args[4].strip(' ')
        if fname=='0':
            fname='null';
        elif fname=='F_SYM':
            fname='"F"';
        elif fname=='FS_SYM':
            fname='"E"';
        elif fname=='FT_SYM':
            fname='"D"';
        elif fname=='G_SYM':
            fname='"G"';
        elif fname=='D_SYM':
            fname='"V"';
        print('new def_cat(%s,(short)%s,(byte)%d,(byte)%d,%s),//%d'%(name,cat,length,digits,fname,i))
        i += 1
    print('};')

def reffun(filepath):
    def repio(i):
        if(i=='VV'): return 'BU'
        if(i=='XX'): return 'T'
        if(i=='YY'): return 'HC'
        if(i=='SUBSCRIPT'): return 'L'
        if(i=='UNITS'): return 'T'
        return i
    def reptoken(i):
        return i.replace('OK','0')
    with file(filepath) as f:
        lines = f.readlines()
    print('private static final ref_fun[] tdiRefFun = new ref_fun[]{//')
    for line in lines:
        if not line.startswith('OPC'):
            continue
        name,symbol,f1,f2,f3,i1,i2,o1,o2,m1,m2,token = tuple(a.strip('\t ') for a in line.split('(',2)[1].split(')',2)[0].strip('{\t ').split(',',12))
        i1,i2,o1,o2 = map(repio,(i1,i2,o1,o2))
        token = reptoken(token)
        print('new ref_fun("%s","%s",%s,%s,%s,DTYPE.%s,DTYPE.%s,DTYPE.%s,DTYPE.%s,%s,%s,%s),//'%(name,symbol,f1,f2,f3,i1,i2,o1,o2,m1,m2,token))
    print('};')

#reffun('I:/Git/mdsplus/include/opcbuiltins.h')
opc('I:/Git/mdsplus/include/opcbuiltins.h')
binary('I:/Git/mdsplus/tdishr/TdiDecompileR.c')
unary('I:/Git/mdsplus/tdishr/TdiDecompileR.c')
defcat('I:/Git/mdsplus/tdishr/TdiDefCat.c')
