/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ifis.ites.hermes.security

import grails.test.spock.IntegrationSpec
import net.ifis.ites.hermes.InitLoginParameters
import net.ifis.ites.hermes.InitLoginParameters as ILP
import net.ifis.ites.hermes.Generator as GEN
import spock.lang.Unroll

/**
 *
 * @author asekulsk
 */
class RoleIntSpec extends IntegrationSpec {

    def setupSpec

    @Unroll
    void "test getSuperuserRole for role '#role.authority'"() {
        expect:
            Role.getSuperuserRole().equals(role) == result
            
        where:
            role                                            |   result
            InitLoginParameters.getRole("NO_SU")            |   false
            InitLoginParameters.getRole("ROLE_SUPERUSER")   |   true
    }	
}