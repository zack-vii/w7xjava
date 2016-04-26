/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mds.udt;

import java.io.DataOutputStream;
import java.io.IOException;
import mds.mdsConnection;

/**
 * @author manduchi
 */
public class mdsConnectionUdt extends mdsConnection{
    @Override
    protected void connectToServer() throws IOException {
        if(this.provider != null){
            this.host = this.getProviderHost();
            this.port = this.getProviderPort();
            this.user = this.getProviderUser();
            final mdsIpProtocolWrapper mipw = new mdsIpProtocolWrapper("udt://" + this.host + ":" + this.port);
            this.dis = mipw.getInputStream();
            this.dos = new DataOutputStream(mipw.getOutputStream());
        }
    }
}
