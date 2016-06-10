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
    i = 0;
    go = False
    for line in lines:
        if not go:
            if line.find('binary[]')>=0:
                go = True
            continue
        if line.strip(' \n\t')=='{':
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
        print('binary[%d] = new op_rec(%s,OPC.%s,(byte)%d,(byte)%d);'%(i,symbol,opcode,prec,lorr))
        i+=1;

opc('I:/Git/mdsplus/include/opcbuiltins.h')
binary('I:/Git/mdsplus/tdishr/TdiDecompileR.c')