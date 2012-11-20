package com.janrain.io

import com.janrain.io.apps.model.PseudoIOAppContext
import com.janrain.io.apps.module.BaseModule
import com.janrain.io.apps.stereotype.Informant
import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

@Grapes([    
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.2'),
@GrabResolver(name='janrain', root='https://repository-janrain.forge.cloudbees.com/release'),
@Grab(group = 'com.janrain.io', module = 'io-core', version = '0.0.3')
])
class SampleInformantModule extends BaseModule<Informant> implements Informant {

    @Override
    void subscribe(Map<String, Object> entity, Map<String, Object> params) {
        println "subscribing $entity.email to $params.list_name"
        // TODO: write code to integrate with service
    }

    @Override
    void unsubscribe(Map<String, Object> entity, Map<String, Object> params) {
        println "unsubscribing $entity.email from $params.list_name"
        // TODO: write code to integrate with service
    }

    @Override
    void synchronize(Map<String, Object> entity, Map<String, Object> params) {
        println "synchronize $entity.email with $params.lists"
        // TODO: write code to integrate with service
    }

    @Override
    void boot(Map params) {
        println "booting $params"
    }

    @Override
    void cleanup() {
        println "cleaning up"
    }

    @Override
    boolean self_test(Map params) {
        return true
    }

    public static void main(String[] args) {
        // instantiate the module
        Informant mod = new SampleInformantModule()

        // this is an pseudo context meant to simulate
        mod.context = new PseudoIOAppContext()
        
        // setting debug as true will print to the console all println statements
        mod.props = ["debug": "true"]

        // boot your module with a set of properties
        // that you will use throughout your module by calling props['hello']
        mod.boot(["hello": "world"])

        // let's call capture and use a real world entity
        // docs: http://developers.janrain.com/documentation/api-methods/capture/entity/find/
        def http = new HTTPBuilder('https://io.dev.janraincapture.com')
        
        http.request(Method.POST, ContentType.JSON) {
            uri.path = "/entity.find"
            uri.query = [   
                            filter: "uuid is not null", 
                            max_results: 1, 
                            type_name: "user",
                            client_id: "vsyzav9f9wq8u7xjvwkwhtz7kgg7dy6y",
                            client_secret: "sppfwb2cbwpfauch3xm58re7nmeuex8h"
                        ]
            response.success = { resp, json ->
                // let's collect the first result and use it
                def entity = json.results[0]

                // pass in some extra contextual parameters like the mailing list
                mod.subscribe(entity, ["list_name": "my_mailing_list1"])

                // unsubscribe
                mod.unsubscribe(entity, ["list_name": "my_mailing_list1"])
                
                // on login, let's check if the ESP and Capture are in sync
                mod.synchronize(entity, ["lists": ["my_mailing_list1","my_mailing_list2"]])
            }
        }

        // this will execute after all your operations are complete
        mod.cleanup()
    }
}