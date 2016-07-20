/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mds.udt;

import mds.MdsConnection;
import mds.MdsDataProvider;

/**
 * @author manduchi
 */
public class MdsDataProviderUdt extends MdsDataProvider{
    @Override
    protected MdsConnection getConnection() {
        return new MdsConnectionUdt();
    }
}
