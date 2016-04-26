/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mds.udt;

import mds.mdsConnection;
import mds.mdsDataProvider;

/**
 * @author manduchi
 */
public class mdsDataProviderUdt extends mdsDataProvider{
    @Override
    protected mdsConnection getConnection() {
        return new mdsConnectionUdt();
    }
}
