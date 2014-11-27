/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ifis.ites.hermes.security

import grails.test.spock.IntegrationSpec
import net.ifis.ites.hermes.InitLoginParameters as ILP
import net.ifis.ites.hermes.Generator as GEN
import spock.lang.Unroll

/**
 * Integration test class for his user controller.
 *
 * @author Andreas Sekulski
 */
class UserIntSpec extends IntegrationSpec {
    
    @Unroll
    void "test getAuthorities for user '#user.username' and roles '#roles'"() {
        when:
            def authorities = user.getAuthorities()

        then:
            authorities.size() == roles
            
        where:
            user                |   roles
            GEN.generateUser()  |   0
            ILP.createSU()      |   1
            ILP.createUser()    |   2 
    }
       
    @Unroll
    void "test containsRole for user '#user.username' and role '#roleType'"() {
        when:
            def hasRole = user.containsRole(Role.findByAuthority(roleType))

        then:
            hasRole == result
            
        where:
            user                | roleType             | result
            GEN.generateUser()  | null                 | false
            GEN.generateUser()  | ""                   | false
            GEN.generateUser()  | "ROLE_NOT_SUPPORTED" | false
            ILP.createUser()    | "ROLE_SUPERUSER"     | false 
            ILP.createSU()      | "ROLE_SUPERUSER"     | true
    }
    
    @Unroll
    void "test hasRoles for user '#user.username'"() {
        when:
            def hasRoles = user.hasRoles()

        then:
            hasRoles == result
            
        where:
            user                | result
            GEN.generateUser()  | false
            ILP.createUser()    | true 
            ILP.createSU()      | true
    }
    
    @Unroll
    void "test isSuperadmin for user '#user.username'"() {
        when:
            def isSu = user.isSuperadmin()

        then:
            isSu == result
            
        where:
            user                | result
            GEN.generateUser()  | false
            ILP.createUser()    | false 
            ILP.createSU()      | true
    }

    @Unroll
    void "test isLastSuperadmin for user '#user.username"() {
        when:
            def isLastSu = user.isLastSuperadmin()

        then:
            isLastSu == result
            
        where:
            user                | result
            GEN.generateUser()  | false
            ILP.createSU()      | true
    }
}